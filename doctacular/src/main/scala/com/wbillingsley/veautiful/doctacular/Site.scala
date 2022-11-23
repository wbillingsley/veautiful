package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.PathDSL
import PathDSL.Compose._
import com.wbillingsley.veautiful.html.{<, Attacher, VDomNode, ^}
import com.wbillingsley.veautiful.templates.{HistoryRouter, VSlides, Challenge}

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
  case class ChallengeRoute(name:String, level:Int, stage:Int) extends Route
  
  trait CustomRoute extends Route {
    def render():VDomNode

    def path:String
  }

  case class Toc(entries: TocEntry*) {
    export entries.isEmpty
  }

  trait CustomTocElement {
    def render:VDomNode
  }
  
  class TocNodeLink(node: => VDomNode, val route: Route) extends CustomTocElement {
    def render = <.a(^.href := router.path(route), node)
  }
  
  object TocLine extends CustomTocElement {
    def render = <("hr")
  }

  type TocEntry = (String, Route) | (String, Toc) | CustomTocElement
  
  private var pages:mutable.Map[String, () => VDomNode] = mutable.Map.empty
  private var decks:mutable.Map[String, () => DeckResource] = mutable.Map.empty
  private var videos:mutable.Map[String, () => VideoResource] = mutable.Map.empty
  private var challenges:mutable.Map[String, () => Seq[Challenge.Level]] = mutable.Map.empty

  private var alternativeMap:mutable.Map[Route, Seq[(Route, Alternative)]] = mutable.Map.empty
  def alternativesTo(r:Route):Seq[(Route, Alternative)] = alternativeMap.getOrElse(r, Seq.empty)
  
  var home:() => VDomNode = () => <.div("No home page has been set yet")
  var toc = Toc()
  
  var pageLayout = PageLayout(this)
  def renderPage(f: => VDomNode):VDomNode = pageLayout.renderPage(this, f)
  
  var deckLayout = DeckLayout(this)
  def renderDeck(name:String, page:Int) = deckLayout.renderDeckGallery(this, name, decks(name)(), page)
  def renderDeckFS(name:String, page:Int) = deckLayout.renderDeckFS(this, name, decks(name)(), page)
  
  var videoLayout = VideoLayout(this)
  def renderVideo(name:String) = videoLayout.renderVideo(this, name, videos(name)())

  def renderChallenge(name: String, level: Int, stage: Int) = {
    val levels = challenges(name)()
    Challenge.apply(
      levels = levels,
      homePath = (_:Challenge) => router.path(HomeRoute),
      levelPath = (c:Challenge, l:Int) => router.path(ChallengeRoute(name, l, 0)),
      stagePath = (c:Challenge, l:Int, s:Int) => router.path(ChallengeRoute(name, l, s)),
    ).show(level, stage)
  }

  def addPage(name:String, content: => VDomNode):PageRoute = {
    pages.put(name, () => content)
    PageRoute(name)
  }

  // Built-in support for VSlides
  given DeckPlayer[VSlides] with 
    extension (v:VSlides) {
      // We need to at least be able to put the deck on the screen
      def defaultView(name:String):VDomNode = <.div(DoctacularVSlidesGallery(site=Site.this, deckName=name, deck=v)(0))

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

  def addChallenge(name:String, content: => Seq[Challenge.Level]):ChallengeRoute = {
    challenges.put(name, () => content)
    ChallengeRoute(name, 0, 0)
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
      case ChallengeRoute(name, level, stage) => (/# / "challenges" / name / level.toString / stage.toString ).stringify
      case c:CustomRoute => c.path
    }

    override def routeFromLocation(): Route = PathDSL.hashPathList() match {
      case "decks" :: name :: intParam(page) :: "fullscreen" :: _ => FullScreenDeckRoute(name, page)
      case "decks" :: name :: intParam(page) :: _ => DeckRoute(name, page)
      case "decks" :: name :: _ => DeckRoute(name, 0)
      case "pages" :: name :: _ => PageRoute(name)
      case "videos" :: name :: _ => VideoRoute(name)
      case "challenges" :: name :: intParam(level) :: intParam(stage) :: _ => ChallengeRoute(name, level, stage)
      case "challenges" :: name :: intParam(level) ::  _ => ChallengeRoute(name, level, 0)
      case "challenges" :: name :: _ => ChallengeRoute(name, 0, 0)
      case _ => HomeRoute
    }
    
    def render = {
      route match {
        case HomeRoute => home()
        case PageRoute(name) if pages.contains(name) => renderPage(pages(name)())
        case DeckRoute(name, slide) if decks.contains(name) => renderDeck(name, slide)
        case FullScreenDeckRoute(name, slide) if decks.contains(name) => renderDeckFS(name, slide)
        case VideoRoute(name) if videos.contains(name) => renderVideo(name)
        case ChallengeRoute(name, level, stage) if challenges.contains(name) => renderChallenge(name, level, stage)
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


