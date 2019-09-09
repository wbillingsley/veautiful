package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.{<, ElementComponent, VNode}
import org.scalajs.dom

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
  }


  def registerHistoryListeners():Unit = {
    dom.window.onpopstate = {
      event =>
        route = routeFromLocation()
        renderElements(render)
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
