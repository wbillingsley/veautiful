package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.{<, ElementComponent, VHtmlNode}
import com.wbillingsley.veautiful.logging.Logger
import org.scalajs.dom
import org.scalajs.dom.Event

abstract class HistoryRouter[Route] extends ElementComponent(<.div) {

  private val logger = Logger.getLogger("com.wbillingsley.veautiful.templates.HistoryRouter")

  var route:Route

  def render:VHtmlNode

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
    logger.debug(s"History event $event")
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

  /** Goes to the specified route, pushing a new history item */
  def routeTo(r:Route):Unit = {
    route = r
    val p = path(route)
    logger.debug(s"routeTo $r with path $p")
    dom.window.history.pushState(r.toString, "", p)
    renderElements(render)
  }

  /** Goes to the specified route, replacing the current history item */
  def silentlyRouteTo(r:Route):Unit = {
    route = r
    val p = path(route)
    logger.debug(s"routeTo $r with path $p")
    dom.window.history.replaceState(r.toString, "", p)
    renderElements(render)
  }

  override def afterDetach():Unit = {
    deregisterHistoryListeners()
  }

  override def afterAttach():Unit = {
    registerHistoryListeners()
    route = routeFromLocation()
    renderElements(render)
  }


}
