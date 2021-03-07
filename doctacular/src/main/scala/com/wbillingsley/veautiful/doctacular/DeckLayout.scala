package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^, StyleSuite, unique, Styling}
import com.wbillingsley.veautiful.templates.{DefaultVSlidesPlayer, Sequencer, VSlides, VSlidesConfig, WindowScaler, WindowWidthScaler}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

case class DoctacularFSVSlidesPlayer(
  site: Site, deckName: String, deck: VSlides,
  override val key: Option[String] = None, scaleToWindow:Boolean = true
)(
  index: Int = 0
) extends VHtmlComponent with Morphing(index) {
  val morpher = createMorpher(this)
  
  private val internalOnIndexChange: Int => Unit = { i =>
    site.router.routeTo(site.FullScreenDeckRoute(deckName, i))
  }

  def galleryButton = <.button(
    ^.onClick --> site.router.routeTo(site.DeckRoute(deckName, 0)), "⛶"
  )

  override def render: VHtmlDiffNode = {
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
) extends VHtmlComponent with Morphing(index) {
  val morpher = createMorpher(this)

  private val internalOnIndexChange: Int => Unit = { i =>
    site.router.routeTo(site.DeckRoute(deckName, i))
  }


  override def render: VHtmlDiffNode = {
    val index = prop
    
    <.div(
      WindowWidthScaler(deck.width)(
        <.div(deck.laidOut), scaleToWindow
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
  
  def renderDeckFS(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = {
    DoctacularFSVSlidesPlayer(site, name, deck)(page)
  }

  def fullScreenButton(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = <.p(
    <.button(^.cls := fsButtonStyle.className,
      ^.onClick --> site.router.routeTo(site.FullScreenDeckRoute(name, page)),
      "⛶ Play this deck fullscreen"
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

  def renderDeckGallery(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = {
    site.pageLayout.renderPage(
      site,
      <.div(
       
        fullScreenButton(site, name, deck, page),

        <.div(^.cls := slideGalleryBorder.className,
          DoctacularVSlidesGallery(site, name, deck)(page)
        )
      )
    )
  }

  def renderDeckNFS(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = {
    site.pageLayout.renderPage(
      site, <.div(^.key := "vslide-example2", ^.cls := "resizable",
        DoctacularFSVSlidesPlayer(site, name, deck)(page)
      )
    )
  }

  styleSet.install()

}

