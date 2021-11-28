package docs.doctacular

import com.wbillingsley.veautiful.html.{<, ^}
import com.wbillingsley.veautiful.doctacular.*
import com.wbillingsley.veautiful.templates.DeckBuilder

import docs.given

case class GoogleSlides(id:String)

given DeckPlayer[GoogleSlides] with 
  extension (v:GoogleSlides) {
    def defaultView(name:String) =
      <.div(
        <.iframe(
          ^.attr("width") := 960, ^.attr("height") := 749, ^.src := s"https://docs.google.com/presentation/d/e/${v.id}/embed?start=false&loop=false&delayms=3000",
          ^.attr("frameborder") := "0", ^.attr("allow") :="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture",
          ^.attr("allowfullscreen") := "allowfullscreen")
      )

    def fullScreenPlayer = None
  }

val tutorialDeck = DeckBuilder()
  .markdownSlide(
    """# Doctacular tutorial
      |
      |And an example of how VSlide decks display in sites
      |""".stripMargin).withClass("center middle")
  .markdownSlide(
    """## Getting started
      |
      |... Need to write the content, just using the deck to show how decks display at the moment.
      |""".stripMargin
  ).markdownSlides(
  """## Another slide
    |
    |Here's some text, just testing the splitting in markdownSlides
    |
    |---
    |
    |## Yet another slide
    |
    |Bla bla bla
    |
    |""".stripMargin)
  .veautifulSlide(<.div(
    <.h2("A slide with an embedded video"),
    YouTube("YE7VzlLtp-4").embeddedPlayer(720, 480)
  ))
  .renderSlides