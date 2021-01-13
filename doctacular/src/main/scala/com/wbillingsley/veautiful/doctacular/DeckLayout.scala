package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^, StyleSuite}
import com.wbillingsley.veautiful.templates.{DefaultVSlidesPlayer, Sequencer, VSlides, VSlidesConfig, WindowScaler}
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
    println("Index change!")
    site.router.routeTo(site.DeckRoute(deckName, i))
  }

  override def render: VHtmlDiffNode = {
    val (site, deckName, deck, index) = prop

    val sequencer = Sequencer()(
      deck.content, index, layout = deck.layout, Some(internalOnIndexChange)
    )

    <.div(
      WindowScaler(width, height)(
        <.div(sequencer, sequencer.footBox), scaleToWindow
      )
    )
  }
}

class DeckLayout(site:Site) {
  
  def renderDeck(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = {
    DoctacularFSVSlidesPlayer(deck.width, deck.height)(site, name, deck, page)
  }

}

