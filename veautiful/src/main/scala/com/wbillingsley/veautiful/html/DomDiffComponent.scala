package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.{DiffComponent, DynamicValue, Receiver, PushVariable}
import org.scalajs.dom



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

  var _lastAnimated:Double = 0d

  def animationUpdate(now:Double):Unit = {
    if now > _lastAnimated then
      _lastAnimated = now
      rerender()
  }

  def stateVariable[T](initial:T) = PushVariable(initial) { _ => requestUpdate() }

  def requestUpdate() = Animator.queue(animationUpdate(_))

}