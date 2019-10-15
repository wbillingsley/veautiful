package com.wbillingsley.wren

import com.wbillingsley.veautiful.{SVG, VNode, ^}

import scala.collection.mutable

sealed trait Connector {
  val wires: mutable.ArrayBuffer[Wire] = mutable.ArrayBuffer.empty

  def connect(w:Wire):Unit = {
    wires.append(w)
  }
}

class Terminal(pos:(Int, Int)) extends Connector with Component {

  val current = new Value("A", None)

  override def terminals: Seq[Terminal] = Seq.empty

  override def constraints: Seq[Constraint] = Seq.empty

  override def render = {
    val (x, y) = pos

    SVG.circle(^.attr("cx") := x, ^.attr("cy") := y, ^.cls := "terminal")
  }
}

class Junction(pos:(Int, Int)) extends Connector {

}

case class Wire(p1:Connector, p2:Connector) {

  p1.connect(this)
  p2.connect(this)



}

