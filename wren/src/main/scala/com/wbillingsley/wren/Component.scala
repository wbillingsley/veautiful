package com.wbillingsley.wren

import com.wbillingsley.veautiful.VNode

trait Component {

  def terminals:Seq[Terminal]

  def constraints:Seq[Constraint]

  def render:VNode

}


