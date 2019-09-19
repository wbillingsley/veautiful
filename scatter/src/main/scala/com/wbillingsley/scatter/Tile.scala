package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, Layout, Update, VNode, ^}

trait Tile extends Layout with VNode {

  def returnType:String

}

object Tile {

  def path(tile:Tile):VNode = {
    val (x, y, w, h) = tile.bounds

    <("path", ns=DElement.svgNS)(^.attr("d") := boxAndArc(w, h))
  }

  def corner:String = "M 9 3 l 0 -3 "

  val typeLoopRadius = 9

  val typeLoopY1 = 15

  val typeLoopY2 = 4

  val boxStartX = 14

  val typeCorner:String = s"M $boxStartX $typeLoopY1 A $typeLoopRadius $typeLoopRadius 0 1 1 $boxStartX $typeLoopY2 L $boxStartX 0 "

  def boxAndArc(w:Int, h:Int):String = s"$typeCorner l $w 0 l 0 $h l ${-w} 0 z"

}




