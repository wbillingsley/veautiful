package com.wbillingsley.wren

import scala.collection.mutable

sealed trait Connector {
  val wires: mutable.ArrayBuffer[Wire] = mutable.ArrayBuffer.empty

  def connect(w:Wire):Unit = {
    wires.append(w)
  }
}

class Terminal(pos:(Int, Int)) extends Connector {

  val current = new Value("A", None)

}

class Junction(pos:(Int, Int)) extends Connector {

}

case class Wire(p1:Connector, p2:Connector) {

  p1.connect(this)
  p2.connect(this)



}

