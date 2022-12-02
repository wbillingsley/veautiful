package com.wbillingsley.veautiful

import scala.collection.mutable

object DynamicSource {
  type ClearListener = Unit => Unit
}

/**
 * A DynamicSource can be subscribed to in a particularly transient way.
 * 
 * You provide a listener to the DynamicSource and it gives you the value.
 * When the value changes, it will notify and unsubscribe you. Re-subscribe
 * to get the new value.
 * 
 * The observation here is that a lazy calculation only has a dependency on
 * upstream values when it *has* a derived value. In any period where it is
 * uncalculated, it does not need to listen to them. This also helps with
 * memory management as any dangling dependent values that have gone out of
 * scope are likely to become eligible for garbage collection the next time
 * the value changes.
 * 
 * Dependent values can unsubscribe early instead if they wish.
 */
trait DynamicSource[T] {

  def ready:Boolean 
  
  def subscribe(l:DynamicSource.ClearListener):T

  def unsubscribe(l:DynamicSource.ClearListener):Unit

  def map[B](t: T => B): DynamicSource[B]

}

/**
  * Something that can receive a value
  */
trait Receiver[T] {
  def receive(v:T):Unit
}

/**
  * A trait for state variables within components.
  */
trait StateVariable[T] extends Receiver[T] {
  def value:T
  def value_=(v:T):Unit

  def receive(v:T) = value_=(v)
}


/**
  * Represents a synchronous changeable value.
  * 
  * These are designed to be lazy and only to register a listener with their 'parent' when they have
  * a value. In a UI context, this means we can set it up so that that when a value changes, all its 
  * dependents will clear and only refill if they are in the page (their rerendering triggers a request
  * for a new value). This helps to minimise complexity around listeners and garbage collection.
  * 
  * Based on `Latch[T]` from `com.wbillingsley.handy`, but synchronous (no special support for Future)
  *
  * @param op
  * @param parents
  */
class DynamicValue[T](op: => T, parents:Seq[DynamicSource[_]] = Seq.empty)(using onClear: () => Unit = () => {}) extends DynamicSource[T] {

  private val listeners: mutable.Set[DynamicSource.ClearListener] = mutable.Set.empty

  /**
    * A listener that detects whether the "parent" Latch has cleared (or changed), and clears this one.
    * This should be registered while this Latch has a Future value, but not when it is clear.
    */
  private val parentListener: DynamicSource.ClearListener = {
    _ => clear()
  }

  /**
    * The cached (clearable) future value
    */
  private var cached: Option[T] = None

  private def addListener(l: DynamicSource.ClearListener) = synchronized {
    listeners.add(l)
  }

  private def removeListener(l: DynamicSource.ClearListener) = synchronized {
    listeners.remove(l)
  }

  /**
    * Can be called to manually clear the Latch.
    * This should propagate up any dependency chain until it reaches a point
    * where it is not the only dependent DynamicVariable.
    */
  def clear():Unit = synchronized {
    cached = None
    notifyClear()    
  }

  protected def notifyClear() = {
    parents.foreach(_.unsubscribe(parentListener))
    val notify = listeners.toSet
    listeners.clear()
    notify.foreach {
      _ (None)
    }
  }

  def ready:Boolean = cached.nonEmpty

  /**
    * Can be called to manually fill the Latch with a value.
    * In this case it will not register a listener with any parent (but beware that one might already be in place)
    */
  protected def fill(v: T):T = synchronized {
    if ready then 
      cached = Some(v)
      notifyClear()
    else 
      cached = Some(v)

    v
  }

  /**
    * Called to get a Future value from the latch, which might already have been completed.
    * This is usually what triggers the computation.
    */
  protected def value: T = {
    cached match {
      case Some(x) => x
      case _ => fill(op)
    }
  }

  override def subscribe(l:DynamicSource.ClearListener):T = synchronized {
    addListener(l)
    value
  }

  override def unsubscribe(l: DynamicSource.ClearListener): Unit = synchronized {
    removeListener(l)
    if listeners.isEmpty then clear()
  }

  /**
    * Produces a dependent Latch, that uses "lazy observation" to keep itself up-to-date with this latch.
    */
  def map[B](t: T => B): DynamicValue[B] = {
    new DynamicValue(t(value), Seq(this))
  }

  /**
    * Produces a dependent Latch, that uses "lazy observation" to keep itself up-to-date with this latch.
    * Note: Latch uses flatMap to take advantage of Scala's syntactic sugar for monad-like classes, but
    * the transform is a flatMap on the contained Future (ie, T => Future[B] not T => Latch[B]).
    *
    * A flatMap taking T => Latch[B] would be of limited use, as it would not have the "lazy observer"
    * listener set up.
    */
  def flatMap[B](t: T => DynamicValue[B]): DynamicValue[B] = {
    new DynamicValue(t(value).value, Seq(this))
  }

}

/** 
 * A PushVariable is a variable we can push variables into; as opposed to DynamicValues, which
 * pull values.
 */
class PushVariable[T](initial:T)(onUpdate: T => Unit = (x:T) => ()) extends StateVariable[T] {
  var _value:T = initial

  def value = _value

  val dynamic = DynamicValue[T](value)

  def value_=(v:T):Unit = {
    _value = v
    dynamic.clear()
  }

  /** As it's very easy to forget to put `.value` in a string interpolation, we toString the value */
  override def toString = value.toString
}