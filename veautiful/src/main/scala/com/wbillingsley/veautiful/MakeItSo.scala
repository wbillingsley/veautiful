package com.wbillingsley.veautiful

/**
  * MakeItSo is a trait that is implemented by nodes that can update themselves to match a template that is passed in.
  */
trait MakeItSo {

  def makeItSo:PartialFunction[MakeItSo, _]

}