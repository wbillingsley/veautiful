package com.wbillingsley.veautiful

/**
  * Some components, particularly those using SVG or Canvas for rendering, might need to lay themselves out.
  * This trait defines a minimal set of functions to allow them to do so
  */
trait Layout {

  def bounds:(Int, Int, Int, Int)

  def prefSize:(Int, Int)

  def layout(bounds: (Int, Int, Int, Int)):Unit

}
