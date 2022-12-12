package com.wbillingsley.scatter

import com.wbillingsley.veautiful.VNode
import com.wbillingsley.veautiful.dom.DDomContent

trait TileLanguage[T] {

  def nodeIcon(returnType:String):DDomContent

  def socketIcon(acceptType:Option[String]):DDomContent

}
