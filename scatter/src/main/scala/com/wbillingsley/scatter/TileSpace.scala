package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, Layout, OnScreen, ^}
import org.scalajs.dom.raw.SVGElement

import scala.collection.mutable

case class TileSpace(override val key:Option[String] = None)(val prefSize:(Int, Int) = (480, 640)) extends DiffComponent with OnScreen {

  val tiles:mutable.SortedSet[Tile] = mutable.SortedSet.empty(Ordering.by(_.hashCode()))

  override def render: DiffNode = <.svg(^.attr("width") := prefSize._1.toString, ^.attr("height") := prefSize._2.toString, ^.cls := "scatter-area",
    tiles.toSeq
  )

  override def setPosition(x: Double, y: Double): Unit = ???

  override def size: Option[(Int, Int)] = Some(prefSize)
}
