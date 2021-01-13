package docs

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.{DeckBuilder, DefaultVSlidesPlayer, VSlides}

object VSlidesExample {

  val deck = new DeckBuilder(1280, 720)
    .markdownSlide(
      """
        |# V-Deck from Scala.js
        |
        |Typesafe slide decks with embedded widgets
        |""".stripMargin).withClass("center middle")
    .markdownSlide(
      s"""
        |## Creating a Deck
        |
        |Creating a deck uses a DeckBuilder, again so we have easy access to convenient functions
        |
        |```scala
        |val deck = new DeckBuilder(1280, 720)
        |  .markdownSlide(
        |    ""${"\""}
        |      |# V-Deck from Scala.js
        |      |
        |      |Typesafe slide decks with embedded widgets
        |      |""${"\""}.stripMargin).withClass("center middle")
        |```
        |
        |This hopefully makes it easier to move between the two ways of working
        |""".stripMargin)
    .markdownSlide(
      """
        |## Rendering from Scala.js
        |
        |When rendering from Scala.js, we *don't* use `renderInto` (which would create a separate root node). Instead,
        |we just ask the builder to create the deck - which is a `VHtmlNode` and so can be directly included in the
        |page.
        |
        |~~~scala
        |let slides = deck.renderNode
        |
        |<.div(^.cls := "resizable", slides.atSlide(i))
        |~~~
        |""".stripMargin)
      .markdownSlide("""
        |## Embedding widgets
        |
        |In the builder, we can also include other \`VHtmlNodes`\. For example, this part of the deck call
        |produces the next slide in this deck:
        |
        |~~~scala
        |.veautifulSlide(<.div(
        |  <.h1("Diffusion simulator"),
        |   Diffusion.SimulationView
        |))
        |~~~
        |
        |""".stripMargin)
    .veautifulSlide(<.div(
      <.h1("Diffusion simulator"),
      Diffusion.SimulationView
    ))
    .renderSlides

  def page1(i:Int):VHtmlNode = {
    <.div(^.cls := "resizable",
      DefaultVSlidesPlayer(1280, 720)(deck, i)
    )
  }

  def page(i:Int):VHtmlNode = {
      <.div(
        Common.markdown(
          """
            |## VSlides
            |
            |VSlides is a slightly different approach to writing slide decks via Markdown. Traditionally, for instance
            |in libraries like remark.js, the slides would be defined in a `.md` file and loaded into the page. This
            |can make it difficult to embed live examples (that involve interaction) into a slide.
            |
            |VSlides is built in Veautiful but exported to JavaScript, to allow slide decks to be built using JavaScript
            |or Scala, and to allow embedding live Veautiful nodes into the slides.
            |
            |#### A JavaScript Example
            |
            |The slide deck below is generated by a JavaScript file [vdeckExample.js](vdeckExample.js) on this site.
            |
            |""".stripMargin
        ),
        <.div(^.key := "vslide-example1", ^.cls := "resizable",
          <.div(^.attr("id") := "vdeck-render"),
          <("script")(^.attr("src") := "vdeckExample.js")
        ),
        Common.markdown(
          """
            |### A Scala example
            |
            |The slide deck below is generated by Scala code, embedded into the code for this page.
            |
            |""".stripMargin),
        <.div(^.key := "vslide-example2", ^.cls := "resizable",
          DefaultVSlidesPlayer(1280, 720)(deck, i)
        )
      )
  }

}
