package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^, StyleSuite, unique, Styling}
import com.wbillingsley.veautiful.templates.{DefaultVSlidesPlayer, Sequencer, VSlides, VSlidesConfig, WindowScaler, WindowWidthScaler}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

case class DoctacularFSVSlidesPlayer(width: Int, height: Int, override val key: Option[String] = None, scaleToWindow:Boolean = true)(
  site: Site,
  deckName: String,                                    
  deck: VSlides,
  index: Int = 0
) extends VHtmlComponent with Morphing((site, deckName, deck, index)) {
  val morpher = createMorpher(this)
  
  private val internalOnIndexChange: Int => Unit = { i =>
    site.router.routeTo(site.FullScreenDeckRoute(deckName, i))
  }
  
  def galleryButton = <.button(
    ^.onClick --> site.router.routeTo(site.DeckRoute(deckName, 0)), "⛶"
  )
  
  override def render: VHtmlDiffNode = {
    val (site, deckName, deck, index) = prop

    val sequencer = Sequencer()(
      deck.laidOut, index, layout = Sequencer.footBoxLayout(Seq(galleryButton)), Some(internalOnIndexChange)
    )

    <.div(
      WindowScaler(width, height)(sequencer, scaleToWindow)
    )
  }
}

case class DoctacularVSlidesGallery(width: Int, height: Int, override val key: Option[String] = None, scaleToWindow:Boolean = true)(
  site: Site,
  deckName: String,
  deck: VSlides,
  index: Int = 0
) extends VHtmlComponent with Morphing((site, deckName, deck, index)) {
  val morpher = createMorpher(this)

  private val internalOnIndexChange: Int => Unit = { i =>
    site.router.routeTo(site.DeckRoute(deckName, i))
  }


  override def render: VHtmlDiffNode = {
    val (site, deckName, deck, index) = prop

    <.div(
      WindowWidthScaler(width)(
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
      |""".stripMargin).modifiedBy(
    ":hover" -> "filter: brightness(115%);"
  ).register()
  
  def renderDeckFS(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = {
    DoctacularFSVSlidesPlayer(deck.width, deck.height)(site, name, deck, page)
  }
  
  def fullScreenButton(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = {
    <.button(^.cls := fsButtonStyle.className,
      ^.onClick --> site.router.routeTo(site.FullScreenDeckRoute(name, page)),
      "⛶ Play this deck fullscreen"
    )
  }
  
  def renderDeckGallery(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = {
    site.pageLayout.renderPage(
      site,
      <.div(
        <.p(
          fullScreenButton(site, name, deck, page)
        ),
        
        <.div(^.cls := slideGalleryBorder.className,
          DoctacularVSlidesGallery(deck.width, deck.height)(site, name, deck, page)
        )
      )
    )
  }

  def renderDeckNFS(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = {
    site.pageLayout.renderPage(
      site, <.div(^.key := "vslide-example2", ^.cls := "resizable",
        DoctacularFSVSlidesPlayer(deck.width, deck.height)(site, name, deck, page)
      )
    )
  }
  
  styleSet.install()

}

