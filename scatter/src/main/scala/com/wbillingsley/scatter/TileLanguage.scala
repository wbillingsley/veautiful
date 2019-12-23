package com.wbillingsley.scatter

import com.wbillingsley.veautiful.VNode
import com.wbillingsley.veautiful.html.VHtmlNode

trait TileLanguage[T] {

  def nodeIcon(returnType:String):VHtmlNode

  def socketIcon(acceptType:Option[String]):VHtmlNode

}
