package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.DiffComponent
import org.scalajs.dom

trait Receiver[T] {
  def receive(v:T):Unit
}

trait StateVariable[T] extends Receiver[T]{
  def value:T
  def value_=(v:T):Unit

  def receive(v:T) = value_=(v)
}

/** Helps us build a little DSL for things to push events through */
class PushBuilder[A, B](f:A => Option[B]) {
  def map[C](g: B=>C) = PushBuilder((a:A) => f(a).map(g))
  def pushTo(sv:Receiver[B]): A => Unit = (a:A) => f(a).foreach(sv.receive)
}


/**
  * A DiffComponent that makes use of some of the facilities we have available in a browser environment, e.g. 
  * use of requestAnimationFrame
  */
trait DomDiffComponent[N <: dom.Element] extends DiffComponent[N, dom.Node] {

  class DDStateVariable[T](initial:T) extends StateVariable[T] {
    var _value:T = initial

    def value = _value

    def value_=(v:T):Unit = {
      _value = v
      requestUpdate()
    }

    /** As it's very easy to forget to put `.value` in a string interpolation, we toString the value */
    override def toString = value.toString
  }

  var _lastAnimated:Double = 0d

  def animationUpdate(now:Double):Unit = {
    if now > _lastAnimated then
      _lastAnimated = now
      rerender()
  }

  def stateVariable[T](initial:T) = DDStateVariable(initial)

  def requestUpdate() = Animator.queue(animationUpdate(_))

}