package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.{DiffNode, Blueprint, DynamicSource, DynamicValue}
import org.scalajs.dom
import scalajs.js

sealed trait PredefinedDynamicModifier

object DynamicModifier {
  case class DynamicAttr[T](name:String, dynamicValue:DynamicValue[T]) extends PredefinedDynamicModifier
  case class DynamicProp[T <: js.Any](name:String, dynamicValue:DynamicValue[T]) extends PredefinedDynamicModifier
}

/**
  * An individual item that can be passed into the `apply` method of a DElement. e.g. in
  * 
  * <.button(^.onClick --> println("bang"), "Hello ", <.b("World"))
  */
type SingleDynamicModifier[-T <: dom.Element] = SingleElementChild[T] | PredefinedDynamicModifier | DynamicSource[ElementChild[T]]

/**
  * Things that can be arguments to the DElement's apply method. 
  * This allows Sequences, Options, etc of SingleChild to be included, simplifying the syntax
  */
type DynamicModifier[-T <: dom.Element] = SingleDynamicModifier[T] | Iterable[SingleDynamicModifier[T]] 


/**
 * A DynamicElement can take dynamic values in its modifiers.
 * 
 */
class DynamicElement[+T <: dom.Element](bp0:DEBlueprint[T]) extends DiffNode[T, dom.Node] {
  
  val inner = DElement[T](bp0.name, None, bp0.ns)

  private var _blueprint:DEBlueprint[T] = bp0

  private var _dynamics:Seq[DynamicValue[_]] = Seq.empty

  def blueprint = _blueprint

  def name = _blueprint.name
  def ns = _blueprint.ns

  override def retention = blueprint.retention

  export inner.reconciler
  export inner.attachSelf
  export inner.detachSelf
  export inner.children
  export inner.create
  export inner.nodeOps
  export inner.domNode


  var lastAnimation:Double = Double.MinValue

  def animationUpdate(now:Double, dt:Double):Unit = 
    if isAttached then resync()

  val dynamicListener: DynamicSource.ClearListener = _ => 
    Animator.queue(animationCallback(_))

  def animationCallback(d:Double):Unit = {
    if d > lastAnimation then
      val dt = d - lastAnimation
      lastAnimation = d
      this.animationUpdate(d, dt)
  }

  private def applyInner(modifier:DynamicModifier[T]):Unit = modifier match {
    case attr:DynamicModifier.DynamicAttr[_] =>
      def updateValue():Unit = {
        val v = attr.dynamicValue.subscribe(dynamicListener)
        println(s"Attached is ${this.isAttached} and v is $v")
        inner.setAttribute(PredefinedElementChild.AttrVal(attr.name, v.toString()))
      }
      updateValue()

    case attr:DynamicModifier.DynamicProp[_] =>
      def updateValue():Unit = {
        val v = attr.dynamicValue.subscribe(dynamicListener)
        inner.setProperty(PredefinedElementChild.PropVal(attr.name, v.toString()))
      }
      updateValue()

    case it:Iterable[DynamicModifier[T]] @unchecked => it.foreach(applyInner)

    case m:ElementChild[T] @unchecked => inner.apply(m) // TODO: fix hack

    // TODO: what happens if somehow we do get a DynamicSource passed into this method?

  }


  /**
   * Creates a new DElement, applying all the modifiers with their current values, and returns a list of 
   * dynamic subscriptions it made along the way. The dynamic variables are bound so that if any value changes,
   * a sync request will be scheduled. Rather than subscribe directly to the dynamic variable, we create a child -
   * this is so that on detach, we can "clear" it to ensure we've unsubscribed.
   */
  private def undynamic:(Seq[DynamicValue[_]], DElement[T]) =  {
    def proc(in:(Seq[DynamicValue[_]], DElement[T]), m:DynamicModifier[T]):(Seq[DynamicValue[_]], DElement[T]) = {
      val (soFar, el) = in 
      m match {
        case v:DynamicSource[_ <: ElementChild[T]] => 
          // derive dynamic variables that would trigger a resync
          val derived = v.map(identity)
          val nowVal = derived.subscribe(dynamicListener)
          (soFar :+ derived, el(nowVal))

        case p:DynamicModifier.DynamicProp[_ <: js.Any] @unchecked => 
          val derived = p.dynamicValue.map(identity)
          val nowVal = derived.subscribe(dynamicListener)
          (soFar :+ derived, el(PredefinedElementChild.PropVal(name, nowVal)))
          
        case p:DynamicModifier.DynamicAttr[_] => 
          val derived = p.dynamicValue.map(identity)
          val nowVal = derived.subscribe(dynamicListener).toString
          (soFar :+ derived, el(PredefinedElementChild.AttrVal(name, nowVal)))

        case it:Iterable[_ <: SingleDynamicModifier[T]] => 
          it.foldLeft((soFar, el))(proc)
        
        case ec:ElementChild[_ >: T] => (soFar, el(ec))
      }
    }

    blueprint.modifiersFor(this).foldLeft((Seq.empty[DynamicValue[_]], DElement(blueprint.name, ns=blueprint.ns)))(proc)
  }

  override def beforeAttach(): Unit = resync()

  override def afterDetach(): Unit = _dynamics.foreach(_.clear())

  def resync():Unit = 
    if blueprint.containsDynamicChildren then
      // We have dynamic variables containing modifiers, so we're going to need to get their current values and do a full resync
      for old <- _dynamics do old.tearDown()
      val (dynamics, dElement) = undynamic
      _dynamics = dynamics
      inner.makeItSo(dElement)
    else
      // We don't have dynamic variables containing modifiers (though might have some dynamic property or attribute bindings)
      blueprint.modifiersFor(this).foreach(applyInner)
      inner.makeItSo(inner)


  override def makeItSo = {
    case de:DynamicElement[T] @unchecked if de.name == name && de.ns == ns =>
      _blueprint = de.blueprint
      if isAttached then resync()

  }
  
}


class DEBlueprint[+T <: dom.Element](val name:String, val ns:String=NS, modifiers:Seq[DynamicModifier[T]] = Seq.empty) extends Blueprint[DynamicElement[T]](classOf[DynamicElement[T]]) {

  def apply(modifiers:DynamicModifier[T]*):DEBlueprint[T] = DEBlueprint[T](name, ns, this.modifiers ++ modifiers)

  def modifiersFor[TT <: dom.Element](de:DynamicElement[TT]) = 
    if name == de.name && ns == de.ns then modifiers.asInstanceOf[Seq[DynamicModifier[TT]]]
    else Seq.empty // TODO: throw an error here.

  def build() = DynamicElement(this)

  /** Whether this blueprint includes any DynamicValue[ElementChild[T]] modifiers */
  def containsDynamicChildren:Boolean = modifiers.exists { 
    case dv:DynamicValue[_] => true
    case _ => false
  }

}

class DEBlueprintBuilder[Base <: dom.Element](ns:String) extends DSLFactory[DEBlueprint, Base] {
  def applyT[T <: Base](name:String):DEBlueprint[T] = DEBlueprint[T](name, ns, Seq.empty)
}