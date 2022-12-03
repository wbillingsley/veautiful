package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.{DiffComponent, DynamicValue, DynamicSource, Receiver, PushVariable, ValueMethod}
import scala.collection.mutable
import org.scalajs.dom



/** Helps us build a little DSL for things to push events through */
class PushBuilder[A, B](f:A => Option[B]) {
  def map[C](g: B=>C) = PushBuilder((a:A) => f(a).map(g))
  def pushTo(sv:Receiver[B]): A => Unit = (a:A) => {
    f(a).foreach(sv.receive)
  }
}

/**
  * A DiffComponent that makes use of some of the facilities we have available in a browser environment, e.g. 
  * use of requestAnimationFrame
  */
trait DomDiffComponent[N <: dom.Element] extends DiffComponent[N, dom.Node] with AnimationAdapter {

  private val dynamics:mutable.Buffer[DynamicValue[_]] = mutable.Buffer.empty

  class DynVal[T](dv:DynamicValue[T]) extends ValueMethod[T] {
    def value = dv.subscribe(dynamicListener)
  }

  override def animationUpdate(now:Double, dt:Double):Unit = {
    rerender()
  }

  def stateVariable[T](initial:T) = PushVariable(initial) { value => 
    requestUpdate() 
  }

  def dynamicState[T](source:DynamicSource[T]):ValueMethod[T] = {
    val derived = source.map(identity)
    dynamics.append(derived)
    DynVal(derived)
  }

  def requestUpdate() = Animator.queue(this)

  override def afterDetach() = for d <- dynamics do d.clear()

}