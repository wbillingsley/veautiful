package com.wbillingsley.veautiful.html

import scala.collection.mutable
import org.scalajs.dom

import com.wbillingsley.veautiful.{DynamicValue, DynamicSource, Update}

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

  def queue(a:Animatable):Unit = queue(a.animationCallback(_))

  val now:DynamicValue[Double] = DynamicValue {
      dom.window.requestAnimationFrame(_ => now.clear())
      dom.window.performance.now()
  }

}

/** Indicates that an object has an animationCallback function and can be put directly into the animator */
trait Animatable {
  def animationCallback(d:Double):Unit
}

/**
  * Provides an animation callback that can reject animation updates if they are too soon, and a
  * DynamicSource.ClearListener that requests an animation frame.
  * 
  * By default, animation frames are rejected if the component has already been called in this animation frame.
  * However, it can be useful for rejecting callbacks that can happen too often - e.g.
  * if you have a component only showing seconds, but the dynamic variable driving it clears at 60fps. 
  * 
  * Implement `animationUpdate(d, dt)` to describe how to update your component
  * 
  * Override `dontUpdateBefore` if you want to change the animation-rejection behaviour
  */
trait AnimationAdapter extends Animatable {

  var lastAnimation:Double = Double.MinValue

  def animationCallback(d:Double):Unit = {
    if d > dontUpdateBefore then
      val dt = d - lastAnimation
      lastAnimation = d
      this.animationUpdate(d, dt)
  }

  val dynamicListener: DynamicSource.ClearListener = _ => 
    Animator.queue(animationCallback(_))

  def animationUpdate(d:Double, dt:Double):Unit

  def dontUpdateBefore = lastAnimation

}