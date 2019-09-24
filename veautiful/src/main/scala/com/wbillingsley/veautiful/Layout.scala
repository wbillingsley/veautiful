package com.wbillingsley.veautiful

trait OnScreen {

  var x = 0
  var y = 0

  def size:Option[(Int, Int)]

  def bounds: Option[(Int, Int, Int, Int)] = {
    for { (w, h) <- size } yield (x, y, x + w, y + h)
  }

}


/**
  * Some components, particularly those using SVG or Canvas for rendering, might need to lay themselves out.
  * This trait defines a minimal set of functions to allow them to do so
  */
trait Layout extends OnScreen {

  def prefSize:(Int, Int)

  def layout(width:Int, height:Int):Unit

}
