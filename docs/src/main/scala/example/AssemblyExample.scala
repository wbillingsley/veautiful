package example

import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, Update, VNode, ^}
import com.wbillingsley.veautiful.templates.VSlides
import org.scalajs.dom.raw.HTMLTextAreaElement

import scala.collection.immutable.ArraySeq

object AssemblyExample {

  val codeWidget = new CodeWidget()

  val cpu = new CPU(4, 4, 16)

  val deck = <.div(
    Seq(<.div(^.cls := "exercise-grid",
      <.div(^.cls := "exercise-instructions",
        codeWidget
      ),
      <.div(^.cls := "exercise-sim",
        <.h3("Status"),
        statusReg(cpu.state),
        <.h3("Registers"),
        regs(cpu)
      )
    )
  ))

  def page() = {
    Common.layout(<.div(
      <.h1("AVR Assembly simulator"),
      <.div(^.cls := "resizable", deck)))
  }

  def oneZero(b:Boolean):String = if (b) "1" else "0"

  def toBits(b:Byte):Seq[Boolean] = {
    Seq(
      (b & 0x01) != 0,
      (b & 0x02) != 0,
      (b & 0x04) != 0,
      (b & 0x08) != 0,
      (b & 0x10) != 0,
      (b & 0x20) != 0,
      (b & 0x40) != 0,
      (b & 0x80) != 0
    )
  }


  def statusReg(state:CPUState) = {

    <.table(^.cls := "register status-register",
        <.tr(
          <.th(^.cls := "register-name", ^.attr("rowspan") := 2, "Status"),
          <.th("I"), <.th("T"), <.th("H"), <.th("S"), <.th("V"), <.th("N"), <.th("Z"), <.th("C")
        ),
        <.tr(
          <.td(oneZero(state.i)), <.td(oneZero(state.t)), <.td(oneZero(state.h)), <.td(oneZero(state.s)),
          <.td(oneZero(state.v)), <.td(oneZero(state.n)), <.td(oneZero(state.z)), <.td(oneZero(state.c))
        )
    )
  }

  def regs(cpu:CPU):VNode = {
    <.table(^.cls := "register reg-registers",
      (cpu.state.registers.iterator.zipWithIndex map {
        case (b, i) =>
          <.tr(
            <.th(^.cls := "register-name", i.toHexString),
              toBits(b).map { bit => <.td(oneZero(bit)) },
            <.td(^.cls := "hex", b.formatted("%02X"))
          )
      }).toSeq
    )
  }


  def io(state:CPUState):VNode = {
    <.table(^.cls := "register io-registers",


    )
  }


  class CPU(numRegisters:Int, numIO:Int, prog:Int) {

    var state = CPUState(
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
