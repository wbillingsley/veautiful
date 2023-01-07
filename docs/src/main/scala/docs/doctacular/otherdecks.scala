package docs.doctacular

import com.wbillingsley.veautiful.html.{<, ^, unique}
import com.wbillingsley.veautiful.doctacular._
import docs.Common.markdown

case class GoogleSlides(id:String)

given DeckPlayer[GoogleSlides] with 
  extension (gs:GoogleSlides) {

    // We need a method to render the deck. We pass in the deck name because some decks (e.g. VSlides when using a site's player)
    // might know how to render a route to a particular slide in the site. Google Slides doesn't, so we just ignore the name.
    def defaultView(deckName:String) = <.div(
        <.iframe(
            ^.src := s"https://docs.google.com/presentation/d/e/${gs.id}/embed?start=false&loop=false&delayms=3000",
            ^.attr.frameborder :="0", ^.attr.width := "960", ^.attr.height := "569", ^.attr.allowfullscreen := "true",
            ^.attr.mozallowfullscreen := "true", ^.attr.webkitallowfullscreen :="true"
        )
    )

    // Optionally, we might be able to directly play the deck full-screen, indexed to a particular slide
    // But for this little example, we can't
    def fullScreenPlayer = None
  }

def otherDecks = <.div(
  markdown("""# Other kinds of slide deck
    | 
    |You might have other kinds of slide decks, e.g. Google Slides, that you want to embed in your site. As with videos,
    |Doctacular provides a way of inserting anything that has a player as a slide deck
    |
    |```scala
    |case class GoogleSlides(id:String)
    |```
    |
    |It's just a class. No methods or anything.
    |
    |And then what we need is a player for the slide deck. Essentially, we're re-composing the embed code from the slide deck provider.
    |The code below loads Google Slides.
    | 
    |```scala
    |given DeckPlayer[GoogleSlides] with 
    |  extension (gs:GoogleSlides) {
    |
    |    // We need a method to render the deck.
    |    // The method takes in the deck name because some players (e.g. Doctacular's player for VSlides) know how to calculate routes to
    |    // individual slides and show controls on the page. But GoogleSlides doesn't, so we just ignore the deckName and generate the embed
    |    // code off the GoogleSlides id.
    |    def defaultView(deckName:String) = <.div(
    |        <.iframe(
    |            ^.src := s"https://docs.google.com/presentation/d/e/${gs.id}/embed?start=false&loop=false&delayms=3000",
    |            ^.attr.frameborder :="0", ^.attr.width := "960", ^.attr.height := "569", ^.attr.allowfullscreen := "true",
    |            ^.attr.mozallowfullscreen := "true", ^.attr.webkitallowfullscreen :="true"
    |        )
    |    )
    |
    |    // Optionally, we might be able to directly play the deck full-screen, indexed to a particular slide
    |    // But for this little example, we can't
    |    def fullScreenPlayer = None
    |  }
    |```
    |
    |Adding a deck to a page, however, is then just
    |
    |```scala
    |site.addDeck("my-deck", GoogleSlides(longAndHideousId))
    |```
    |
    |For example, let's embed one of Google Slides's sample starter decks below:
    |
    |```
    |// For Google Slides, we use the ID from inside the "File -> publish to the web" embed code.
    |// It is annoyingly long and hard to get to, though.
    |GoogleSlides("2PACX-1vTe-9V0_YiBne3Z1MvUxJEVo0VF-tPBh8W3hlAz1OGs_Wfil9F31HQT4C4DqMcb-GJJVHxFOnM-08-9").defaultView("")
    |```
    |""".stripMargin),
  GoogleSlides("2PACX-1vTe-9V0_YiBne3Z1MvUxJEVo0VF-tPBh8W3hlAz1OGs_Wfil9F31HQT4C4DqMcb-GJJVHxFOnM-08-9").defaultView("")
)