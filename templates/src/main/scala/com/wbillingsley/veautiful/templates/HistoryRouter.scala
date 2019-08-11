package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.{<, ElementComponent, VNode}
import org.scalajs.dom

abstract class HistoryRouter[Route] extends ElementComponent(<.div) {

  registerHistoryListeners()

  var route:Route

  def render:VNode

  def path(route:Route):String

  def routeFromLocation():Route

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

  override def afterAttach():Unit = {
    renderElements(render)
  }


}
