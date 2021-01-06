package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.PathDSL
import PathDSL.Compose._
import com.wbillingsley.veautiful.html.{<, Attacher, VHtmlNode}
import com.wbillingsley.veautiful.templates.{HistoryRouter, VSlides}

import scala.collection.mutable
import scala.util.Try

sealed trait Route
case object HomeRoute extends Route
case class PageRoute(name:String) extends Route
case class DeckRoute(name:String, slide:Int) extends Route
trait CustomRoute extends Route {
  def render():VHtmlNode
  
  def path:String
}


case class Toc(entries: TocEntry*) {
  export entries.isEmpty
}

type TocEntry = (String, Route) | (String, Toc)

/**
  * A Doctacular site. 
  * 
  * At the moment, pages, decks, and home are done mutably. This is because sites are generally so simple (just set up
  */
class Site() {
  
  private var pages:mutable.Map[String, () => VHtmlNode] = mutable.Map.empty
  private var decks:mutable.Map[String, () => VSlides] = mutable.Map.empty
  var home:() => VHtmlNode = () => <.div("No home page has been set yet")
  var toc = Toc()
  
  var pageLayout = PageLayout(this)
  def renderPage(f: => VHtmlNode):VHtmlNode = pageLayout.renderPage(this, f)
  
  def addPage(name:String, content: => VHtmlNode):PageRoute = {
    pages.put(name, () => content)
    PageRoute(name)
  }
  
  def addDeck(name:String, content: => VSlides):DeckRoute = {
    decks.put(name, () => content)
    DeckRoute(name, 0)
  }

  object intParam {
    def unapply(s:String):Option[Int] = (Try { s.toInt }).toOption
  }
  
  object router extends HistoryRouter[Route] {

    var route:Route = HomeRoute

    override def path(r:Route):String = r match {
      case HomeRoute => /#.stringify
      case PageRoute(name) => (/# / "pages" / name).stringify
      case DeckRoute(name, page) => (/# / "decks" / name / page.toString).stringify
      case c:CustomRoute => c.path
    }

    override def routeFromLocation(): Route = PathDSL.hashPathList() match {
      case "decks" :: name :: intParam(page) :: _ => DeckRoute(name, page)
      case "decks" :: name :: _ => DeckRoute(name, 0)
      case "pages" :: name :: _ => PageRoute(name)
      case _ => HomeRoute
    }
    
    def render = {
      route match {
        case HomeRoute => home()
        case PageRoute(name) if pages.contains(name) => renderPage(pages(name)())
        case DeckRoute(name, slide) if decks.contains(name) => decks(name)().atSlide(slide)
        case custom:CustomRoute => custom.render()
        case _ => home()
      }
    }

  }

  /**
    * Attaches the site to a DOM element in the document. Call this to launch your site...
    */
  def attachTo(node:org.scalajs.dom.Element):Unit = {
    val route = Attacher.newRoot(node)
    route.render(router)
  }
  
  
}


