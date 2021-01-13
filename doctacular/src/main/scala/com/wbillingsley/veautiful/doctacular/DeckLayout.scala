package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.{DefaultVSlidesPlayer, Sequencer, VSlides, VSlidesConfig}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

/*
case class DoctacularFSVSlidesPlayer(width: Int, height: Int, override val key: Option[String] = None, scaleToWindow:Boolean = true)(
  site: Site,
  deckName: String,                                    
  deck: VSlides,
  index: Int = 0
) extends VHtmlComponent with Morphing((site, deckName, deck, index)) {

  val morpher = createMorpher(this)

  var scale:Double = 1
  var top:Double = 0
  var left:Double = 0

  def rescale() = for { n <- domNode } {
    val r = n.asInstanceOf[HTMLElement].getBoundingClientRect()
    scale = Math.min(r.height / height, r.width / width)

    left = Math.max((r.width - scale * width) / 2, 0)
    top = Math.max((r.height - scale * height) / 2, 0)

    VSlides.logger.debug(s"Scale is now $scale")
  }

  val rescaleEventListener: (e:Event) => Unit = { (_) =>
    rerender()
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    dom.window.addEventListener("resize", rescaleEventListener)
    VSlides.logger.debug(s"$this attached")
    rescale()
  }

  override def beforeDetach(): Unit = {
    super.beforeDetach()
    dom.window.removeEventListener("resize", rescaleEventListener)
  }

  private val internalOnIndexChange: Int => Unit = { i =>
    prop.onIndexChange match {
      case Some(f) => f(i)
      case None => updateProp(prop.copy(index = i))
    }
  }

  override def render: VHtmlDiffNode = {
    rescale()

    val config = prop

    val sequencer = Sequencer()(
      config.deck.content, config.index, layout = config.deck.layout, Some(internalOnIndexChange)
    )

    <.div(^.cls := (if (scaleToWindow) s"${vslidesTopStyle.className} vslides-top scaled" else s"${vslidesTopStyle.className} vslides-top unscaled"),
      <.div(^.cls := (if (scaleToWindow) s"${slidesScalerStyle.className} vslides-scaler scaled" else s"${slidesScalerStyle.className} vslides-scaler unscaled"),
        ^.attr("style") := (if (scaleToWindow) {
          s"transform: scale($scale); width: ${width}px; height: ${height}px; top: ${top}px; left: ${left}px; "
        } else {
          s"width: ${width}px; height: ${height}px; "
        }),
        sequencer, sequencer.footBox
      )
    )
  }

  def atSlide(i:Int):DefaultVSlidesPlayer = DefaultVSlidesPlayer(width, height, key)(prop.deck, i, prop.onIndexChange)
}
*/

class DeckLayout(site:Site) {

  //given styleSet:StyleSuite = StyleSuite()
  
  def renderDeck(site:Site, name:String, deck:VSlides, page:Int):VHtmlNode = {
    DefaultVSlidesPlayer(deck.width, deck.height)(deck, page)
  }

}

