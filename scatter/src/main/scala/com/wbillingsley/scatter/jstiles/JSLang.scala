package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.TileLanguage
import com.wbillingsley.veautiful.{SVG, VNode, ^}

object JSLang extends TileLanguage {

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
