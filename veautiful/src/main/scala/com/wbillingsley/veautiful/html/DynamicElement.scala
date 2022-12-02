package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.{DiffNode, Blueprint, DynamicValue}
import org.scalajs.dom

sealed trait PredefinedDynamicModifier

object DynamicModifier {
  case class DynamicAttr[T](name:String, dynamicValue:DynamicValue[T]) extends PredefinedDynamicModifier
  case class DynamicProp[T](name:String, dynamicValue:DynamicValue[T]) extends PredefinedDynamicModifier
}

/**
  * An individual item that can be passed into the `apply` method of a DElement. e.g. in
  * 
  * <.button(^.onClick --> println("bang"), "Hello ", <.b("World"))
  */
type SingleDynamicModifier[-T <: dom.Element] = SingleElementChild[T] | PredefinedDynamicModifier 

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

  def applyInner(modifier:DynamicModifier[T]):Unit = modifier match {
    case attr:DynamicModifier.DynamicAttr[_] =>
      def updateValue():Unit = {
        val v = attr.dynamicValue.subscribe(_ => Animator.queue((_) => if isAttached then resync()))
        println(s"Attached is ${this.isAttached} and v is $v")
        inner.setAttribute(PredefinedElementChild.AttrVal(attr.name, v.toString()))
      }
      updateValue()

    case attr:DynamicModifier.DynamicProp[_] =>
      def updateValue():Unit = {
        val v = attr.dynamicValue.subscribe(_ => Animator.queue((_) => if isAttached then resync()))
        println(s"Attached is ${this.isAttached} and v is $v")
        inner.setProperty(PredefinedElementChild.PropVal(attr.name, v.toString()))
      }
      updateValue()
    
    case it:Iterable[DynamicModifier[T]] @unchecked => it.foreach(applyInner)

    case m:ElementChild[T] @unchecked => inner.apply(m) // TODO: fix hack
  }


  override def beforeAttach(): Unit = resync()

  def resync():Unit = 
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

}

class DEBlueprintBuilder[Base <: dom.Element](ns:String) extends DSLFactory[DEBlueprint, Base] {
  def applyT[T <: Base](name:String):DEBlueprint[T] = DEBlueprint[T](name, ns)
}