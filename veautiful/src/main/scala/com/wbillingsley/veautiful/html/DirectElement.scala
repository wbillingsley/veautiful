package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.{VNode, Keep}
import org.scalajs.dom 

/** 
 * A Veautiful node for a fixed element.
 * 
 * This can be useful if you have code that is written directly using the DOM API. 
 * Just grab your created DOM element, wrap it in a DirectElement, and you can use it.
 */
class DirectElement[T <: dom.Element](el: T) extends VNode[T] with Keep(el) {
    var domNode:Option[T] = None

    def attach(): T = {
        domNode = Some(el)
        el
    }

    def detach(): Unit = { domNode = None }
}
