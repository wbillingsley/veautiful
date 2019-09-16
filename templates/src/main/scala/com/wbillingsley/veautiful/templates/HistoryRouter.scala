package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.{<, ElementComponent, VNode}
import org.scalajs.dom
import org.scalajs.dom.Event

abstract class HistoryRouter[Route] extends ElementComponent(<.div) {

  var route:Route

  def render:VNode

  def path(route:Route):String

  def routeFromLocation():Route

  def deregisterHistoryListeners():Unit = {
    dom.window.onpopstate = {
      event =>
        // deregistered
    }

    dom.window.onhashchange = {
      event =>
        // deregistered
    }
  }

  def handleHistoryEvent(event: Event):Unit = {
    println("Popping state!")
    route = routeFromLocation()
    renderElements(render)
  }


  def registerHistoryListeners():Unit = {

    dom.window.addEventListener("popstate", handleHistoryEvent)
    // IE11 doesn't trigger popstate for hashchanges
    if (dom.window.navigator.userAgent.contains("Trident")) {
      dom.window.addEventListener("hashchange", handleHistoryEvent)
    }

  }

  def routeTo(r:Route):Unit = {
    route = r
    val p = path(route)
    println(p)
    //dom.window.history.replaceState("State not supported", "Rendered by Veautiful", p)
    renderElements(render)
  }

  override def afterDetach():Unit = {
    deregisterHistoryListeners()
  }

  override def afterAttach():Unit = {
    registerHistoryListeners()
    renderElements(render)
  }


}
