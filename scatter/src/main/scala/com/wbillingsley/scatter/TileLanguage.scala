package com.wbillingsley.scatter

import com.wbillingsley.veautiful.VNode
import com.wbillingsley.veautiful.html.VDomNode

trait TileLanguage[T] {

  def nodeIcon(returnType:String):VDomNode

  def socketIcon(acceptType:Option[String]):VDomNode

}
