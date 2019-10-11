package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.TileLanguage
import com.wbillingsley.veautiful.{SVG, VNode, ^}

import scala.scalajs.js.JSON

object JSLang extends TileLanguage[JSExpr] {

  def nodeIcon(returnType:String):VNode = returnType match {

    case "String" => SVG.text("$")
    case "void" => SVG.text("x")
    case "Number" => SVG.text("#")
    case "Boolean" => SVG.text("?")
    case _ => SVG.g()

  }

  override def socketIcon(acceptType: Option[String]): VNode = acceptType match {
    case Some(s) => nodeIcon(s)
    case _ => SVG.g()
  }
}


trait JSExpr {
  def toJS(indent:Int):String
}
case object JSBlank extends JSExpr {
  def toJS(indent:Int) = ""
}

case class JSNumber(n:Double) extends JSExpr {
  def toJS(indent:Int) = n.toString
}

case class JSString(s:String) extends JSExpr {
  def toJS(indent:Int) = {
    JSON.stringify(s)
  }
}
case class JSBlock(steps:Seq[JSExpr]) extends JSExpr {
  def toJS(indent:Int) = {
    val i = "  " * indent
    steps.map(s => s"$i${s.toJS(indent)}")
  }.mkString(";\n")
}

case class JSIfElse(cond: JSExpr, t:JSBlock, f:JSBlock) extends JSExpr {
  def toJS(indent:Int) = {
    val i = "  " * indent

    s"""${i}if (${cond.toJS(indent)}) {
       |${t.toJS(indent + 1)}
       |${i}} else {
       |${f.toJS(indent + 1)}
       |${i}}""".stripMargin
  }
}