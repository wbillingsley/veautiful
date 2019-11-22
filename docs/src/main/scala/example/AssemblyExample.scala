package example

import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, Update, VNode, ^}
import com.wbillingsley.veautiful.templates.VSlides
import org.scalajs.dom.raw.HTMLTextAreaElement

import scala.collection.immutable.ArraySeq

object AssemblyExample {

  val codeWidget = new CodeWidget()

  val deck = <.div(
    Seq(<.div(
      <.h1("hello"),
      codeWidget
    ))
  )

  def page() = {
    Common.layout(<.div(
      <.h1("VSlides"),
      <.div(^.cls := "resizable", deck)))
  }



  class CPU(numRegisters:Int, numIO:Int, prog:Int) {

    var status = CPUState(
      registers = ArraySeq.fill[Byte](numRegisters)(0x0),
      io = ArraySeq.fill[Byte](numIO)(0x0)
    )

  }

  case class CPUState(status:Byte = 0, pc:Int = 0, sp:Int = 0, registers:ArraySeq[Byte], io:ArraySeq[Byte]) {

    def c:Boolean = (status & 0x01) != 0
    def z:Boolean = (status & 0x02) != 0
    def n:Boolean = (status & 0x04) != 0
    def v:Boolean = (status & 0x08) != 0
    def s:Boolean = (status & 0x10) != 0
    def h:Boolean = (status & 0x20) != 0
    def t:Boolean = (status & 0x40) != 0
    def i:Boolean = (status & 0x80) != 0

    def updateStatus(status:Byte, b:Boolean, shift:Int) = {
      if(b) {
        (status | (0x01 << shift)).toByte
      } else {
        (status & ~(0x01 << shift)).toByte
      }
    }

  }

}


class CodeWidget(cols:Int = 40, rows:Int = 20) extends DiffComponent {

  var currentLine:Option[Int] = None
  var highlights:Seq[(Int, String)] = Seq.empty

  def textUpdate() = {
    println("updating!")
    rerender()
  }

  val ta = <.textarea(^.attr("cols") := cols, ^.attr("rows") := rows, ^.on("input") --> textUpdate())

  def lineNums = <.textarea(^.attr("disabled") := "disabled", ^.cls := "linenums",
    (1 to (text + " ").linesIterator.size).mkString("\n")
  )

  def text:String = ta.domEl.map({ case e:HTMLTextAreaElement => e.value }).getOrElse("")

  override protected def render: DiffNode = {

    println(text)
    <.div(^.cls := "codewidget",
      lineNums, ta
    )
  }
}
