package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.*
import org.scalajs.dom
import org.scalajs.dom.{Event, html}


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


/** This is so that later we can do some type programming to work out whether the result should have update */
inline def unique[N](n:VNode[N]):Unique[N] = Unique(n)

