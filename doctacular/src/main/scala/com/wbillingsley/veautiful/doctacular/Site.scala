package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.PathDSL
import PathDSL.Compose._
import com.wbillingsley.veautiful.html.{<, Attacher, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.{HistoryRouter, VSlides}

import scala.collection.mutable
import scala.util.Try

/**
  * A Doctacular site. 
  * 
  * At the moment, pages, decks, and home are done mutably. This is because sites are generally so simple (just set up
  */
class Site() {

  sealed trait Route
  case object HomeRoute extends Route
  case class PageRoute(name:String) extends Route
  case class DeckRoute(name:String, slide:Int) extends Route
  case class FullScreenDeckRoute(name:String, slide:Int) extends Route
  case class VideoRoute(name:String) extends Route
  
  trait CustomRoute extends Route {
    def render():VHtmlNode

    def path:String
  }

  case class Toc(entries: TocEntry*) {
    export entries.isEmpty
  }

  trait CustomTocElement {
    def render:VHtmlNode
  }
  
  class TocNodeLink(node: => VHtmlNode, val route: Route) extends CustomTocElement {
    def render = <.a(^.href := router.path(route), node)
  }
  
  object TocLine extends CustomTocElement {
    def render = <("hr")
  }

  type TocEntry = (String, Route) | (String, Toc) | CustomTocElement
  
  private var pages:mutable.Map[String, () => VHtmlNode] = mutable.Map.empty
  private var decks:mutable.Map[String, () => VSlides] = mutable.Map.empty
  private var videos:mutable.Map[String, () => VideoResource] = mutable.Map.empty
  
  var home:() => VHtmlNode = () => <.div("No home page has been set yet")
  var toc = Toc()
  
  var pageLayout = PageLayout(this)
  def renderPage(f: => VHtmlNode):VHtmlNode = pageLayout.renderPage(this, f)
  
  var deckLayout = DeckLayout(this)
  def renderDeck(name:String, page:Int) = deckLayout.renderDeckGallery(this, name, decks(name)(), page)
  def renderDeckFS(name:String, page:Int) = deckLayout.renderDeckFS(this, name, decks(name)(), page)
  
  var videoLayout = VideoLayout(this)
  def renderVideo(name:String) = videoLayout.renderVideo(this, name, videos(name)())
  
  def addPage(name:String, content: => VHtmlNode):PageRoute = {
    pages.put(name, () => content)
    PageRoute(name)
  }
  
  def addDeck(name:String, content: => VSlides):DeckRoute = {
    decks.put(name, () => content)
    DeckRoute(name, 0)
  }
  
  def addVideo[T : VideoPlayer](name: String, video:T):VideoRoute = {
    videos.put(name, () => PlayableVideo(video))
    VideoRoute(name)
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
      case FullScreenDeckRoute(name, page) => (/# / "decks" / name / page.toString / "fullscreen").stringify
      case VideoRoute(name) => (/# / "videos" / name).stringify
      case c:CustomRoute => c.path
    }

    override def routeFromLocation(): Route = PathDSL.hashPathList() match {
      case "decks" :: name :: intParam(page) :: "fullscreen" :: _ => FullScreenDeckRoute(name, page)
      case "decks" :: name :: intParam(page) :: _ => DeckRoute(name, page)
      case "decks" :: name :: _ => DeckRoute(name, 0)
      case "pages" :: name :: _ => PageRoute(name)
      case "videos" :: name :: _ => VideoRoute(name)
      case _ => HomeRoute
    }
    
    def render = {
      route match {
        case HomeRoute => home()
        case PageRoute(name) if pages.contains(name) => renderPage(pages(name)())
        case DeckRoute(name, slide) if decks.contains(name) => renderDeck(name, slide)
        case FullScreenDeckRoute(name, slide) if decks.contains(name) => renderDeckFS(name, slide)
        case VideoRoute(name) if videos.contains(name) => renderVideo(name)
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


