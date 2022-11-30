package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.{DiffNode, Blueprint}
import org.scalajs.dom

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

  def applyInner(modifiers:ElementChild[T]*) = 
    inner.apply(modifiers*)

  override def makeItSo = {
    case de:DynamicElement[T] @unchecked if de.name == name && de.ns == ns =>


  }


  // The first time we are built, we need to apply the blueprint's modifiers
  applyInner(blueprint.modifiersFor(this)*)
}


class DEBlueprint[+T <: dom.Element](val name:String, val ns:String=NS, modifiers:Seq[ElementChild[T]] = Seq.empty) extends Blueprint[DynamicElement[T]](classOf[DynamicElement[T]]) {

  def apply(modifiers:ElementChild[T]*):DEBlueprint[T] = DEBlueprint[T](name, ns, this.modifiers ++ modifiers)

  def modifiersFor[TT <: dom.Element](de:DynamicElement[TT]) = 
    if name == de.name && ns == de.ns then modifiers.asInstanceOf[Seq[ElementChild[TT]]]
    else Seq.empty // TODO: throw an error here.

  def build() = DynamicElement(this)

}

class DEBlueprintBuilder[Base <: dom.Element](ns:String) extends DSLFactory[DEBlueprint, Base] {
  def applyT[T <: Base](name:String):DEBlueprint[T] = DEBlueprint[T](name, ns)
}