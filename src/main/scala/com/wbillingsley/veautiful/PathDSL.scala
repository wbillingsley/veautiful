package com.wbillingsley.veautiful

/**
  * Created by wbilling on 26/05/2017.
  */
object PathDSL {

  sealed trait PathComponent

  case class /(root:PathComponent, tail:PathComponent)



}
