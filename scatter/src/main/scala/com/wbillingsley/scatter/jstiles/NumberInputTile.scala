package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Tile, TileComponent, TileForeignObject, TileSpace, TileText}
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{<, DElement, DiffComponent, PropVal, ^}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement

class NumberInputTile (tileSpace:TileSpace[JSExpr], width:Int = 2, initial:Option[Double] = None) extends Tile(tileSpace) with DiffComponent {

  import NumberInputTile._

  var number:Option[Double] = initial

  val input = <.input(^.attr("type") := "Number", ^.cls := "", ^.on("input") ==> setNumber, ^.on("pointerdown") --> {})

  override def afterAttach(): Unit = {
    super.afterAttach()
  }

  def setNumber(e:dom.Event):Unit = {
    number = try {
      val on = Some(e.target.asInstanceOf[HTMLInputElement].value.toDouble)
      for {
        n <- on
      } input.prop(PropVal("value", n.toString))
      on
    } catch {
      case x:Exception => number
    }
  }

  override val tileContent: TileComponent[JSExpr] = HBox(
    TileForeignObject(input)
  )

  override def returnType: String = "Number"

  override def toLanguage: JSExpr = JSBlank
}

object NumberInputTile {
  val logger:Logger = Logger.getLogger(NumberInputTile.getClass)
}