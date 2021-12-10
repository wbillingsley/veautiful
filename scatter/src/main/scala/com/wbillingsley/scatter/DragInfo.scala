package com.wbillingsley.scatter

import com.wbillingsley.veautiful.OnScreen

/**
  * Used by TileSpace to record what component is being dragged
  */
case class DragInfo[T <: OnScreen](draggable:T, itemStartX:Double, itemStartY:Double, mouseStartX: Double, mouseStartY:Double)
