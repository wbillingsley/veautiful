package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, Layout, ^}

import scala.collection.mutable

case class TileSpace(override val key:Option[String] = None)(val prefSize:(Int, Int) = (480, 640)) extends DiffComponent with Layout {

  val tiles:mutable.SortedSet[Tile] = mutable.SortedSet.empty(Ordering.by(_.hashCode()))

  override def render: DiffNode = <.svg(^.attr("width") := prefSize._1.toString, ^.attr("height") := prefSize._2.toString,
    tiles.toSeq
  )

  var bounds: (Int, Int, Int, Int) = (0, 0, prefSize._1, prefSize._2)

  override def layout(bounds: (Int, Int, Int, Int)): Unit = {
    this.bounds = bounds
    rerender()
  }


}
