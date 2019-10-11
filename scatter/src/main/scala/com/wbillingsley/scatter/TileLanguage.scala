package com.wbillingsley.scatter

import com.wbillingsley.veautiful.VNode

trait TileLanguage[T] {

  def nodeIcon(returnType:String):VNode

  def socketIcon(acceptType:Option[String]):VNode

}
