package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.Blueprint
import com.wbillingsley.veautiful.html.{<, Attacher, VDomContent, VDomNode, HistoryRouter, ^, PathDSL}
import PathDSL.Compose._

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
  case class ListPathRoute(kind:String, name:String, subpath:List[String]) extends Route
  
  trait CustomRoute extends Route {
    def render():VDomContent

    def path:String
  }

  case class Toc(entries: TocEntry*) {
    export entries.isEmpty
  }

  trait CustomTocElement {
    def render:VDomContent
  }
  
  class TocNodeLink(node: => VDomContent, val route: Route) extends CustomTocElement {
    def render = <.a(^.href := router.path(route), node)
  }
  
  object TocLine extends CustomTocElement {
    def render = <("hr")
  }

  type TocEntry = (String, Route) | (String, Toc) | CustomTocElement
  
  private var pages:mutable.Map[String, () => VDomContent] = mutable.Map.empty
  private var decks:mutable.Map[String, () => DeckResource] = mutable.Map.empty
  private var videos:mutable.Map[String, () => VideoResource] = mutable.Map.empty
  private var otherListPathContent:mutable.Map[(String, String), () => ListPathResource] = mutable.Map.empty

  private var alternativeMap:mutable.Map[Route, Seq[(Route, Alternative)]] = mutable.Map.empty
  def alternativesTo(r:Route):Seq[(Route, Alternative)] = alternativeMap.getOrElse(r, Seq.empty)
  
  var home:() => VDomContent = () => <.div("No home page has been set yet")
  var toc = Toc()
  
  var pageLayout = PageLayout(this)
  def renderPage(f: => VDomContent):VDomContent = pageLayout.renderPage(this, f)
  
  var deckLayout = DeckLayout(this)
  def renderDeck(name:String, page:Int) = deckLayout.renderDeckGallery(this, name, decks(name)(), page)
  def renderDeckFS(name:String, page:Int) = deckLayout.renderDeckFS(this, name, decks(name)(), page)
  
  var videoLayout = VideoLayout(this)
  def renderVideo(name:String) = videoLayout.renderVideo(this, name, videos(name)())

  def addPage(name:String, content: => VDomContent):PageRoute = {
    pages.put(name, () => content)
    PageRoute(name)
  }

  given ListPathPlayer[Seq[Challenge.Level]] with
    extension (levels:Seq[Challenge.Level]) {
      def kind = "challenges"

      def defaultSubpath = List("0", "0")

      def view(name:String, subpath:List[String]) = {
        val c = Challenge(
          levels = levels,
          homePath = (_:Challenge) => router.path(HomeRoute),
          levelPath = (c:Challenge, l:Int) => router.path(ListPathRoute("challenges", name, List(l.toString))),
          stagePath = (c:Challenge, l:Int, s:Int) => router.path(ListPathRoute("challenges", name, List(l.toString, s.toString))),
        )

        subpath match {
          case Nil => c.show(0, 0)
          case l :: Nil => c.show(l.toInt, 0)
          case l :: s :: _ => c.show(l.toInt, s.toInt)
        }
      }

    }

  // Built-in support for VSlides
  given DeckPlayer[VSlides] with 
    extension (v:VSlides) {
      // We need to at least be able to put the deck on the screen
      def defaultView(name:String):VDomContent = <.div(DoctacularVSlidesGallery(site=Site.this, deckName=name, deck=v)(0))

      // Optionally, we might be able to directly play the deck full-screen
      def fullScreenPlayer = Some((name:String, slide:Int) => <.div(DoctacularFSVSlidesPlayer(site=Site.this, deckName=name, deck=v)(slide)))
    } 

  
  def addDeckResource(name:String, deckResource:DeckResource):DeckRoute = {
    decks.put(name, () => deckResource)
    DeckRoute(name, 0)
  }

  def addDeck[T : DeckPlayer](name:String, content: => T):DeckRoute = {
    addDeckResource(name, PlayableDeck(content))
  }

  def addVideoResource(name: String, videoResource:VideoResource):VideoRoute = {
    videos.put(name, () => videoResource)
    VideoRoute(name)
  }

  def addChallenge(name:String, content: => Seq[Challenge.Level]):ListPathRoute = {
    addOther(name, content)
  }

  def addOtherResource(name:String, customResource:ListPathResource):ListPathRoute = {
    otherListPathContent.put((customResource.kind, name), () => customResource)
    ListPathRoute(customResource.kind, name, customResource.defaultSubpath)
  }
  
  def addOther[T : ListPathPlayer](name:String, content: => T):ListPathRoute = {
    addOtherResource(name, PlayableListPathResource(content))
  }

  def addVideo[T : VideoPlayer](name: String, video:T):VideoRoute = {
    addVideoResource(name, PlayableVideo(video))
  }
  
  /** Adds an item along with several "alternatives". For instance, a slide deck and a video of the presentation. */
  def add(name:String, first:Alternative, alternatives:Alternative*):Route = {
    
    def register(name:String, item:Medium):Route = item match {
      case Medium.Page(f) => addPage(name, f())
      case d:Medium.Deck[_] => addDeckResource(name, PlayableDeck(d.deck())(using d.player))
      case v:Medium.Video[_] => addVideoResource(name, PlayableVideo(v.video())(using v.player))
      case o:Medium.OtherListPath[_] => addOtherResource(name, PlayableListPathResource(o.content())(using o.player))
    }
    
    val firstRoute = register(name, first.item)
    
    val remainder = for
      (alt, i) <- alternatives.zipWithIndex
      route = register(name + s"-alt$i", alt.item)
    yield (route, alt)
    
    // Map every route to a sequence of the alternatives
    val all = (firstRoute, first) +: remainder
    for (r, a) <- all do
      val rest = all.filter(_ != (r, a))
      alternativeMap(r) = rest

    // return the primary route
    firstRoute
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
      case ListPathRoute(kind, name, subpath) => subpath.foldLeft(/# / kind / name)((path, element) => path / element).stringify
      case c:CustomRoute => c.path
    }

    override def routeFromLocation(): Route = PathDSL.hashPathList() match {
      case "decks" :: name :: intParam(page) :: "fullscreen" :: _ => FullScreenDeckRoute(name, page)
      case "decks" :: name :: intParam(page) :: _ => DeckRoute(name, page)
      case "decks" :: name :: _ => DeckRoute(name, 0)
      case "pages" :: name :: _ => PageRoute(name)
      case "videos" :: name :: _ => VideoRoute(name)
      case kind :: name :: subpath if otherListPathContent.contains((kind, name)) => ListPathRoute(kind, name, subpath)
      case _ => HomeRoute
    }
    
    def render = {
      route match {
        case HomeRoute => home()
        case PageRoute(name) if pages.contains(name) => renderPage(pages(name)())
        case DeckRoute(name, slide) if decks.contains(name) => renderDeck(name, slide)
        case FullScreenDeckRoute(name, slide) if decks.contains(name) => renderDeckFS(name, slide)
        case VideoRoute(name) if videos.contains(name) => renderVideo(name)
        case ListPathRoute(kind, name, subpath) if otherListPathContent.contains((kind, name)) => otherListPathContent((kind, name))().view(name, subpath)
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
  
  def attachToBody():Unit = attachTo(org.scalajs.dom.document.body)
  
}


