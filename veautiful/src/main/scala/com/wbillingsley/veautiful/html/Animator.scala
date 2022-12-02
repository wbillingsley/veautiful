package com.wbillingsley.veautiful.html

import scala.collection.mutable
import org.scalajs.dom

import com.wbillingsley.veautiful.DynamicValue

/**
  * The Animator will queue a batch of callback animation functions to be run via requestanimationframe
  * with the same timestamp. This gives a rerender an easy way to check if it has already been called
  * in this animation frame.
  */
object Animator {

    var _tasks: mutable.Buffer[Double => Unit] = mutable.Buffer.empty

    var _lastTimestamp:Double = dom.window.performance.now()

    var _scheduled = false

    val callback = (ts:Double) => {
        runTasks(ts)
    }

    def runTasks(ts:Double) = {
        _lastTimestamp = ts
        val tasks = _tasks.toSeq
        _scheduled = false
        _tasks.clear()
        for t <- tasks do t.apply(_lastTimestamp)
    }

    def queue(f:Double => Unit) = {
        _tasks.append(f)
        if !_scheduled then 
          dom.window.requestAnimationFrame(callback)
          _scheduled = true
    }

    val now:DynamicValue[Double] = DynamicValue {
        dom.window.requestAnimationFrame(_ => now.clear())
        dom.window.performance.now()
    }

}