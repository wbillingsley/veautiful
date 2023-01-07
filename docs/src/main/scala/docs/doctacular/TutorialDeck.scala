package docs.doctacular

import com.wbillingsley.veautiful.html.{<, ^}
import com.wbillingsley.veautiful.doctacular.*

import docs.given

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