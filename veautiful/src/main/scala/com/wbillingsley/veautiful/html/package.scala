package com.wbillingsley.veautiful

import org.scalajs.dom
import org.scalajs.dom.{Event, html}

package object html {

  type VHtmlNode = VNode[dom.Node]

  type VHtmlDiffNode = DiffNode[dom.Element, dom.Node]

  type VHtmlComponent = DiffComponent[dom.Element, dom.Node]

  /**
    * Extension functions on Event that are useful for writing input elements
    */
  implicit class EventMethods(val e:Event) extends AnyVal {

    def inputValue:Option[String] = e.target match {
      case h:html.Input => Some(h.value)
      case t:html.TextArea => Some(t.value)
      case _ => None
    }

    def checked:Option[Boolean] = e.target match {
      case h:html.Input => Some(h.checked)
      case _ => None
    }

  }

}
