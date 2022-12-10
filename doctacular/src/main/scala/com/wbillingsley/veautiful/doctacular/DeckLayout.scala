package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.{<, DHtmlComponent, VHtmlDiffNode, VHtmlContent, ^, StyleSuite, unique, Styling}
import com.wbillingsley.veautiful.templates.{DefaultVSlidesPlayer, Sequencer, VSlides, VSlidesConfig, WindowScaler, WindowWidthScaler}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

case class DoctacularFSVSlidesPlayer(
  site: Site, deckName: String, deck: VSlides,
  override val key: Option[String] = None, scaleToWindow:Boolean = true
)(
  index: Int = 0
) extends DHtmlComponent with Morphing(index) {
  val morpher = createMorpher(this)
  
  private val internalOnIndexChange: Int => Unit = { i =>
    site.router.routeTo(site.FullScreenDeckRoute(deckName, i))
  }

  def galleryButton = <.button(
    ^.onClick --> site.router.routeTo(site.DeckRoute(deckName, 0)), "⛶"
  ).build()

  override def render = {
    val index = prop

    val sequencer = Sequencer()(
      deck.laidOut, index, layout = Sequencer.footBoxLayout(Seq(galleryButton)), Some(internalOnIndexChange)
    )

    <.div(
      WindowScaler(deck.width, deck.height)(sequencer, scaleToWindow)
    )
  }
}

case class DoctacularVSlidesGallery(
  site: Site, deckName: String, deck: VSlides,
  override val key: Option[String] = None, scaleToWindow:Boolean = true)(
  index: Int = 0
) extends DHtmlComponent with Morphing(index) {
  val morpher = createMorpher(this)

  private val internalOnIndexChange: Int => Unit = { i =>
    site.router.routeTo(site.DeckRoute(deckName, i))
  }


  override def render = {
    val index = prop
    
    <.div(
      WindowWidthScaler(deck.width)(
        <.div(deck.laidOut).build(), scaleToWindow
      )
    )
  }

  override def beforeAttach(): Unit = {
    super.beforeAttach()

    // Ensure the template styles are installed in the page
    com.wbillingsley.veautiful.templates.templateStyleSuite.install()
  }
}

class DeckLayout(site:Site) {

  given styleSet:StyleSuite = StyleSuite()

  val slideGalleryBorder = Styling("border: 1px solid #ddd;").register()

  val fsButtonStyle = Styling(
    """border-radius: 5px;
      |background-color: antiquewhite;
      |text-align: center;
      |border: 1px solid #aaa;
      |margin-right: 1rem;
      |""".stripMargin).modifiedBy(
    ":hover" -> "filter: brightness(115%);"
  ).register()
  
  def renderDeckFS(site:Site, name:String, deck:DeckResource, page:Int) = {
    deck.fullScreenPlayer match {
      case Some(player) => player(name, page)
      case None => deck.defaultView(name)
    }
  }

  def alternativesButtons(site:Site, name:String, deck:DeckResource, page:Int) = <.p(
    (for fsSupperted <- deck.fullScreenPlayer yield 
      <.button(^.cls := fsButtonStyle.className,
        ^.onClick --> site.router.routeTo(site.FullScreenDeckRoute(name, page)),
        "⛶ Play this deck fullscreen"
      )
    ), 
    
    // This is a bit of a hack, as it needs to move more generally into an alternatives display
    for (r, alt) <- site.alternativesTo(site.DeckRoute(name, 0)) yield
      <.button(^.cls := fsButtonStyle.className,
        ^.onClick --> site.router.routeTo(r),
          alt.item match {
            case Medium.Video(_) => s"⏵ ${alt.descriptor}"
            case Medium.Page(_) => s"🗎 ${alt.descriptor}"
            case Medium.Deck(_) => s"⧉ ${alt.descriptor}"
          }
      )
  )

  def renderDeckGallery(site:Site, name:String, deck:DeckResource, page:Int) = {
    site.pageLayout.renderPage(
      site,
      <.div(
       
        alternativesButtons(site, name, deck, page),

        <.div(^.cls := slideGalleryBorder.className,
          deck.defaultView(name)
        )
      )
    )
  }

  def renderDeckNFS(site:Site, name:String, deck:DeckResource, page:Int) = {
    site.pageLayout.renderPage(site, 
      deck.fullScreenPlayer match {
        case Some(player) => <.div(^.key := "vslide-example2", ^.cls := "resizable", player(name, page))
        case None => deck.defaultView(name)
      }
    )
  }

  styleSet.install()

}

