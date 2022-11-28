package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.{<, DElement, SVG, VDomNode, ^}

case class ScatterPlot(plotWidth:Int, plotHeight:Int, xName:String, yName:String, xLabel:(Double) => String, yLabel:(Double) => String, defaultxMax:Double, defaultyMax:Double) {

  def tickInterval(max:Double, num:Int):Double = max / num

  val marginLeft = 100
  val marginTop = 30
  val marginBottom = 70
  val marginRight = 40

  val viewBox = s"${-marginLeft} ${-marginTop} ${plotWidth + marginLeft + marginRight} ${plotHeight + marginTop + marginBottom}"

  def plot(data:Seq[(Double, Double)]):VDomNode = {

    val xMax = if (data.isEmpty) defaultxMax else {
      Math.max(defaultxMax, data.maxBy(_._1)._1)
    }
    val yMax = if (data.isEmpty) defaultyMax else {
      Math.max(defaultyMax, data.maxBy(_._2)._2)
    }

    val xInterval = tickInterval(xMax, 10)
    def xScale(v:Double):Int = {
      val adj = 10 * xInterval
      val ratio = plotWidth.toDouble / adj
      (ratio * v).toInt
    }

    val yInterval = tickInterval(yMax, 5)
    def yScale(v:Double):Int = {
      val adj = 5 * yInterval
      val ratio = plotHeight.toDouble / adj
      plotHeight - (ratio * v).toInt
    }

    def xAxis(ticks:Int, name:String):VDomNode = {
      <("g", ns=DElement.svgNS)(
        <("line", ns=DElement.svgNS)(^.attr("x1") := "0", ^.attr("x2") := plotWidth.toString, ^.attr("y1") := plotHeight.toString, ^.attr("y2") := plotHeight.toString),
        <("text", ns=DElement.svgNS)(
          ^.attr("x") := plotWidth.toString, ^.attr("y") := s"${plotHeight + 60}", ^.cls := "axis-label-x", name
        ),
        for {
          i <- 0 to ticks
        } yield {
          val v = i * xInterval
          val x = xScale(v)

          <("g", ns=DElement.svgNS)(
            <("line", ns=DElement.svgNS)(
              ^.attr("x1") := x.toString, ^.attr("x2") := x.toString, ^.attr("y1") := plotHeight.toString, ^.attr("y2") := (plotHeight + 10).toString, ^.cls := "tick-line"
            ),
            <("text", ns=DElement.svgNS)(
              ^.attr("y") := (plotHeight + 30).toString, ^.attr("x") := x.toString, ^.cls := "tick-label-x",
              xLabel(v)
            )
          )
        }
      )
    }

    def yAxis(ticks:Int, name:String):VDomNode = {

      SVG.g(
        <("line", ns=DElement.svgNS)(
          ^.attr("x1") := "0", ^.attr("x2") := "0", ^.attr("y1") := "0", ^.attr("y2") := plotHeight.toString
        ),
        <("text", ns=DElement.svgNS)(
          ^.attr("x") := "0", ^.attr("y") := "-50", ^.cls := "axis-label-y", name
        ),
        for {
          i <- 0 to ticks
        } yield {
          val v = i * yInterval
          val y = yScale(v)

          SVG.g(
            <("line", ns=DElement.svgNS)(
              ^.attr("x1") := "0", ^.attr("x2") := "-10", ^.attr("y1") := y.toString, ^.attr("y2") := y.toString, ^.cls := "tick-line"
            ),
            <("text", ns=DElement.svgNS)(^.attr("y") := y.toString, ^.attr("x") := "-20", ^.cls := "tick-label-y",
              yLabel(v)
            )
          )
        }
      )
    }

    def plotPoint(x:Double, y:Double) = {
      val cx = xScale(x)
      val cy = yScale(y)

      SVG.circle(
        ^.attr("cx") := cx.toString, ^.attr("cy") := cy.toString, ^.attr("r") := "3", ^.cls := "plot-point"
      )
    }

    <.svg(
      ^.cls := "scatterplot",
      ^.attr("viewBox") := viewBox,
      ^.attr("width") := plotWidth,
      ^.attr("height") := plotHeight
    )(

      xAxis(10, xName),
      yAxis(5, yName),
      for { (x, y) <- data } yield plotPoint(x, y)
    )
  }

}
