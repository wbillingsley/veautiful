package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.TileLanguage
import com.wbillingsley.veautiful.html.{SVG, VHtmlNode}

import scala.scalajs.js.JSON

object JSLang extends TileLanguage[JSExpr] {

  def nodeIcon(returnType:String):VHtmlNode = returnType match {

    case "String" => SVG.text("$")
    case "void" => SVG.text("x")
    case "Number" => SVG.text("#")
    case "Boolean" => SVG.text("?")
    case _ => SVG.g()

  }

  override def socketIcon(acceptType: Option[String]): VHtmlNode = acceptType match {
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

/* Loops */

case class JSWhile(cond: JSExpr, t:JSBlock) extends JSExpr {
  def toJS(indent:Int) = {
    val i = "  " * indent

    s"""${i}while (${cond.toJS(indent)}) {
       |${t.toJS(indent + 1)}
       |${i}}""".stripMargin
  }
}

case class JSDoWhile(t:JSBlock, cond: JSExpr) extends JSExpr {
  def toJS(indent:Int) = {
    val i = "  " * indent

    s"""${i}do {
       |${t.toJS(indent + 1)}
       |${i}} while (${cond.toJS(indent)})""".stripMargin
  }
}

case class JSFor(init: JSBlock, cond: JSExpr, after:JSBlock, block:JSBlock) extends JSExpr {
  def toJS(indent:Int) = {
    val i = "  " * indent

    s"""${i}for (${init.toJS(indent)}; ${cond.toJS(indent)}; ${after.toJS(indent)}) {
       |${block.toJS(indent + 1)}
       |${i}}""".stripMargin
  }
}

/* Functions */

case class JSFunction(name:String, params:Seq[String], body:JSBlock) extends JSExpr {
  def toJS(indent:Int) = {
    val i = "  " * indent

    s"""${i}function $name (${params.mkString(",")}) {
       |${body.toJS(indent + 1)}
       |${i}}""".stripMargin
  }
}

case class FunctionCall(name:String, params:Seq[JSExpr]) extends JSExpr {
  override def toJS(indent: Int): String = {
    s"${name}(${params.map(_.toJS(indent)).mkString(", ")})"
  }
}

/* Variables */

case class JSLet(name:String, t:JSExpr) extends JSExpr {
  def toJS(indent:Int) = {
    val i = "  " * indent

    s"${i}let $name = ${t.toJS(0)}".stripMargin
  }
}

case class JSVariable(name:String) extends JSExpr {
  def toJS(indent:Int) = {
    val i = "  " * indent
    s"${i}$name"
  }
}

case class JSAssign(name:String, t:JSExpr) extends JSExpr {
  def toJS(indent:Int) = {
    val i = "  " * indent

    s"${i}$name = ${t.toJS(0)}".stripMargin
  }
}
