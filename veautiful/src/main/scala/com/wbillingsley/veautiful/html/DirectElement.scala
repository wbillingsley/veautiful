package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.Keep
import org.scalajs.dom 

/** 
 * A Veautiful node for a fixed element.
 * 
 * This can be useful if you have code that is written directly using the DOM API. 
 * Just grab your created DOM element, wrap it in a DirectElement, and you can use it.
 */
class DirectElement(el: dom.Element) extends VHtmlNode with Keep(el) {
    var domNode:Option[dom.Element] = None

    def attach(): dom.Element = {
        domNode = Some(el)
        el
    }

    def detach(): Unit = { domNode = None }
}
