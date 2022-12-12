package com.wbillingsley.wren

import com.wbillingsley.veautiful.svg.{DSvgComponent, DSvgContent}
import com.wbillingsley.veautiful.{DiffComponent, DiffNode, VNode}

trait Component extends DSvgComponent {

  def terminals:Seq[Terminal]

  def constraints:Seq[Constraint]

  def render:DSvgContent

}