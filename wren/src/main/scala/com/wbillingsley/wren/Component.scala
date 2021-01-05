package com.wbillingsley.wren

import com.wbillingsley.veautiful.html.{VHtmlComponent, VHtmlDiffNode}
import com.wbillingsley.veautiful.{DiffComponent, DiffNode, VNode}

trait Component extends VHtmlComponent {

  def terminals:Seq[Terminal]

  def constraints:Seq[Constraint]

  def render:VHtmlDiffNode

}