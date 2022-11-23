package com.wbillingsley.wren
import com.wbillingsley.veautiful.html.{<, SVG, VHtmlDiffNode, ^}
import org.scalajs.dom.Event
import org.scalajs.dom.raw.HTMLInputElement

import scala.util.Random

class ValueSlider(v:Value, pos:(Int, Int), orientation: Orientation = Orientation.East, min:String = "1", max:String, values:Seq[String]  = Seq.empty, onUpdate: () => Unit) extends Component {

  val id = Random.nextString(5)

  override def terminals: Seq[Terminal] = Seq.empty

  override def constraints: Seq[Constraint] = Seq.empty

  val updateValue: Event => Unit = (e:Event) => {
    v.value = sliderVal.map(vv => (vv.toDouble, UserSet))
    onUpdate()
  }

  val slider = <.input(
    ^.attr("type") := "range",
    ^.attr("min") := min,
    ^.attr("max") := max,
    ^.attr("list") := id,
    ^.on("input") ==> updateValue
  )

  def sliderVal = slider.domNode match {
    case Some(i:HTMLInputElement) => Some(i.value)
    case _ => None
  }


  override def render = {
    val (x, y) = pos

    SVG.foreignObject(^.attr("x") := x, ^.attr("y") := y, ^.attr("width") := 150, ^.attr("height") := 30,
      slider,
      <("datalist")(^.attr("id") := id,
        values.map { v => <("option")(^.attr("value") := v)}
      )
    )
  }

}
