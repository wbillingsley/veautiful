package com.wbillingsley.veautiful

import scala.collection.mutable

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
class DynamicVariable[T](op: => T, parents:Seq[DynamicVariable[_]] = Seq.empty)(using onClear: () => Unit = () => {}) {

  private val listeners: mutable.Set[DynamicVariable.Listener[T]] = mutable.Set.empty

  /**
    * A listener that detects whether the "parent" Latch has cleared (or changed), and clears this one.
    * This should be registered while this Latch has a Future value, but not when it is clear.
    */
  private val parentListener: DynamicVariable.Listener[Any] = {
    case _ => clear()
  }

  /**
    * The cached (clearable) future value
    */
  private var cached: Option[T] = None

  private def addListener(l: DynamicVariable.Listener[T]) = synchronized {
    listeners.add(l)
  }

  private def removeListener(l: DynamicVariable.Listener[T]) = synchronized {
    listeners.remove(l)
  }

  private def propagateClear(l: DynamicVariable.Listener[T]):Unit = synchronized {
    removeListener(l)
    if listeners.isEmpty then clear()
  }

  /**
    * Can be called to manually clear the Latch.
    * This should propagate up any dependency chain until it reaches a point
    * where it is not the only dependent DynamicVariable.
    */
  def clear():Unit = synchronized {
    cached = None
    parents.foreach(_.propagateClear(parentListener))
    listeners.foreach {
      _ (None)
    }
  }

  def ready:Boolean = cached.nonEmpty

  /**
    * Can be called to manually fill the Latch with a value.
    * In this case it will not register a listener with any parent (but beware that one might already be in place)
    */
  def fill(v: T):T = synchronized {
    if ready then clear()
    cached = Some(v)
    listeners.foreach {
      _ (Some(v))
    }
    v
  }

  /**
    * Called to get a Future value from the latch, which might already have been completed.
    * This is usually what triggers the computation.
    */
  def value: T = {
    cached match {
      case Some(x) => x
      case _ => fill(op)
    }
  }

  /**
    * Produces a dependent Latch, that uses "lazy observation" to keep itself up-to-date with this latch.
    */
  def map[B](t: T => B): DynamicVariable[B] = {
    new DynamicVariable(t(value), Seq(this))
  }

  /**
    * Produces a dependent Latch, that uses "lazy observation" to keep itself up-to-date with this latch.
    * Note: Latch uses flatMap to take advantage of Scala's syntactic sugar for monad-like classes, but
    * the transform is a flatMap on the contained Future (ie, T => Future[B] not T => Latch[B]).
    *
    * A flatMap taking T => Latch[B] would be of limited use, as it would not have the "lazy observer"
    * listener set up.
    */
  def flatMap[B](t: T => DynamicVariable[B]): DynamicVariable[B] = {
    new DynamicVariable(t(value).value, Seq(this))
  }

}

object DynamicVariable {

  type Listener[T] = (Option[T] => Unit)

  private val listeners:mutable.Set[DynamicVariable.Listener[Any]] = mutable.Set.empty

  /**
    * Called whenever any Latch changes state
    */
  def globalNotify(evt:Option[Any]) = listeners.foreach(_.apply(evt))

  /**
    * Adds a listener function that will be called whenever any Latch changes state. This is useful, for example,
    * for wiring up declarative view re-rendering so that whenever any cached state in the program changes a
    * re-render is called.
    */
  def addGlobalListener(l:DynamicVariable.Listener[Any]) = synchronized {
    listeners.add(l)
  }

  def removeGlobalListener(l:DynamicVariable.Listener[Any]) = synchronized {
    listeners.remove(l)
  }

}