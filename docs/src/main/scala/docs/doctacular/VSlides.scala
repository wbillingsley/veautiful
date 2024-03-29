package docs

import com.wbillingsley.veautiful.html.{<, VDomNode, ^}
import com.wbillingsley.veautiful.templates.WindowScaler
import com.wbillingsley.veautiful.doctacular.{DeckBuilder, DefaultVSlidesPlayer, VSlides}

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
      s"""
        |## Rendering from Scala.js
        |
        |When rendering from Scala.js, we *don't* use `renderInto` (which would create a separate root node). Instead,
        |we just ask the builder to create the deck - which is a `VDomNode` and so can be directly included in the
        |page.
        |
        |~~~scala
        |let slides = deck.renderNode
        |
        |<.div(^.cls := "${WindowScaler.resizableSurround.className}", slides.atSlide(i))
        |~~~
        |""".stripMargin)
      .markdownSlide("""
        |## Embedding widgets
        |
        |In the builder, we can also include other \`VDomNodes`\. For example, this part of the deck call
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

  def page1(i:Int) = {
    <.div(^.cls := WindowScaler.resizableSurround,
      DefaultVSlidesPlayer(deck)(i)
    )
  }

  def page(i:Int) = {
      <.div(
        Common.markdown(
          s"""
            |# Slide Decks (VSlides)
            |
            |Slide decks are written as scripts, adding slides to a builder, letting users mix slides written in
            |markdown with slides that contain more complex or interactive features.
            |
            |When played full-screen, they can auto-scale to the window, so that you can lay out the internals of your deck as if you had 
            |a fixed resolution canvas. They also have a vertical view that can lay the entire deck out in a scrollable format.
            |
            |A basic builder is created like this:
            |
            |```scala
            |val simpleDeck = DeckBuilder(1920, 1080)(using marked)
            |  .markdownSlide("# This is a title slide").withClass("center middle")
            |  .markdownSlide(\"\"\"
            |    |# This is a content slide
            |    |
            |    |It's written in Markdown.
            |    |The pipe characters `|` are just there so we can write this
            |    |nicely indented into the code without Markdown complaining.
            |    |
            |    |(We just need to remember to call "stripMargin" to remove them)
            |  \"\"\".stripMargin)
            |  .markdownSlide(\"\"\"
            |    |# Template slides
            |    |
            |    |The next two slides use templates, so we can have simple calls to show
            |    |fullscreen images and other common things we want to do.
            |  \"\"\".stripMargin)
            |  .renderSlides
            |```
            |
            |The first parameter is the dimensions of the slide deck (e.g. 1920 by 1080). The deck will automatically scale itself to fit in the window,
            |using CSS scaling, but this gives slide authors predictable internal demensions for their content.
            |
            |The second parameter (the `using` parameter) is the [`Markup`](#/pages/markup-components) parser to use. 
            |
            |### Adding the deck to the site
            |
            |Doctacular sites include a player for VSlides, but we do need to import it when we add the deck
            |
            |```scala
            |import site.given
            |
            |site.toc = site.Toc(
            |  // previous routes
            |  
            |  // Add our deck
            |  "My Deck" -> site.addDeck("my-deck", simpleDeck)
            |)
            |```
            |
            |
            |### Inserting widgets
            |
            |As well as inserting `markdownSlides`, you can also insert `veautifulSlides` containing components and widgets. This makes it relatively
            |easy to write decks containing interactve elements.
            |
            |```scala
            |  .markdownSlide("This is just a `Markdown` slide")
            |  .veautifulSlide(<.div(
            |    <.h1("Diffusion simulator"),
            |    Diffusion.SimulationView
            |  ))
            |  .renderSlides
            |```
            |
            |### An example deck
            |
            |The slide deck below is generated by Scala code, embedded into the code for this page.
            |
            |""".stripMargin),
        <.div(^.key := "vslide-example2", ^.cls := WindowScaler.resizableSurround,
          DefaultVSlidesPlayer(deck)(i)
        ),
        Common.markdown(
          """
            |### Templates
            |
            |It's relatively common to define a few templates (more complex slides). The easiest way to do this for a project
            |is to use Scala's *extension methods*.
            |
            |e.g. 
            |
            |```scala
            |extension (db:DeckBuilder) {
            | /**
            |   * Two portrait images, side by side to fill a slide
            |   */
            |  def portraitImageSlide(image1:String, caption:String):DeckBuilder = {
            |    db.veautifulSlide(
            |      <.div(^.cls := imageSlide.className, // this comes from a Styling I have defined
            |        <.img(^.src := image1),
            |        <("figcaption")(caption)
            |      )
            |    )
            |  }
            |}
            |```
            |
            |Would allow us to say something like:
            |
            |```scala
            |  .markdownSlide("This is just a `Markdown` slide")
            |  .portraitImageSlide(
            |    "https://example.com/image1.png", 
            |    "https://example.com/image2.png", 
            |    "Two images side-by-side"
            |  )
            |  .renderSlides
            |```
            |
            |### More examples
            |
            |For more examples, take a look at some of the courses that have been written using Veautiful and Doctaculer.
            |
            |For instance, [Thinking about Programming](https://theintelligentbook.com/thinkingaboutprogramming) contains slide decks that
            |contain programmable game environments.
          """.stripMargin
      )
    )
  }

}
