package com.wbillingsley.veautiful

/**
  * MakeItSo is a trait that is implemented by nodes that can update themselves to match an instruction or a template that is passed in.
  * 
  * It's implemented as a partial function, as at runtime when doing the reconciliations, we wouldn't have access to the type parameter anyway
  * (without having to introduce a tagging system that might overcomplicate things)
  */
trait MakeItSo {

  /**
   * Asks this object to morph itself to match the destination (or follow the instructions in the case of an accepted Blueprint)
   */
  def makeItSo:PartialFunction[Any, _]

}
