package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Tile, TileComponent, TileForeignObject, TileSpace}
import com.wbillingsley.veautiful.html.{<, ^}
import com.wbillingsley.veautiful.logging.Logger
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement

class NumberInputTile (tileSpace:TileSpace[JSExpr], width:Int = 2, initial:Option[Double] = None) extends Tile(tileSpace) {

  import NumberInputTile._

  var number:Option[Double] = initial

  val input = <.input(^.attr("type") := "Number", ^.cls := "scatter-number-input", ^.on("input") ==> onInput).build()

  def setNumber(n:Double):Unit = {
    number = Some(n)
    input.makeItSo(<.input(^.attr("type") := "Number", ^.cls := "scatter-number-input", ^.on("input") ==> onInput, ^.prop("value") ?= number.map(_.toString)))
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
  }

  def onInput(e:dom.Event):Unit = {
    try {
      setNumber(e.target.asInstanceOf[HTMLInputElement].value.toDouble)
    } catch {
      case _:Exception => // Number format
    }
    rerender()
  }

  override val tileContent: TileComponent[JSExpr] = HBox(
    TileForeignObject(input)
  )

  override def returnType: String = "Number"

  override def toLanguage: JSExpr = JSNumber(number getOrElse 0)
}

object NumberInputTile {
  val logger:Logger = Logger.getLogger(NumberInputTile.getClass)
}