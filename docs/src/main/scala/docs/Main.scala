package docs

import com.wbillingsley.veautiful.html.{Attacher, StyleSuite, Styling, <}
import com.wbillingsley.veautiful.doctacular.*

import org.scalajs.dom

/** This is our own set of styles for widgets that we define */
given siteStyles:StyleSuite = StyleSuite()
val site = Site()

val latestVersion = "0.3.0"

/** A style class for embedded examples */
val embeddedExampleStyle = Styling(
  """background: antiquewhite;
    |padding: 10px;
    |border-radius: 10px;
    |margin-bottom: 1rem;
    |""".stripMargin).register()

object Main {

  def main(args:Array[String]): Unit = {
    import site.given
    import doctacular.{*, given}
    
    // To set the theme colours, we're rudely adding rules to the CSS that the Site's layout engine produces
    site.pageLayout.leftSideBarStyle.addRules(
      """
        |background: aliceblue;
        |border: none;
        |""".stripMargin)

    site.pageLayout.sideBarToggleStyle.addRules(
      """
        |background: aliceblue;
        |border: none;
        |""".stripMargin)

    site.pageLayout.contentStyle.addRules(
      " pre" -> "background: aliceblue; padding: 10px; border-radius: 10px;",
      " h1,h2,h3,h4" -> "font-family: 'Times New Roman', serif; color: #004479; font-style: italic; margin-top: 2rem;"
    )
    
    // The TOC is the table of contents in teh left side-bar.
    // In this case, at the same time as setting the TOC, we're also adding the pages to the site's router.
    site.toc = site.Toc(
      site.TocNodeLink(Common.logoWithTitle(150, 150), site.HomeRoute),
      site.TocLine,
      "Intro" -> site.Toc(
        "Home" -> site.HomeRoute,
        "Hello world" -> site.addPage("hello-world", helloWorld),
        "Low-level components" -> site.addPage("low-level", lowlevelComponents),
        "Function components" -> site.addPage("pure-functions", pureFunctions),
        "Stateful components" -> site.addPage("stateful-components", statefulComponents),
        "Morphing components" -> site.addPage("morphing-components", morphingComponents),
        "Dynamic state" -> site.addPage("dynamic-state", dynamicState),
        "D3-like components" -> site.addPage("low-level-components", d3like),
        "Markup components" -> site.addPage("markup-components", markupComponents),
        "Routing" -> site.addPage("routing", routing),
        "CSS in JS" -> site.addPage("style-suites", styleSuites)
      ),
      "Doctacular" -> site.Toc(
        "Intro" -> site.addPage("doctacular-intro", doctacular.introPage),
        "Displaying a single deck" -> site.addPage("doctacular-a-single-deck", doctacular.singleDeck),
        "Sites" -> site.addPage("doctacular-sites", doctacular.sites),
        "Pages" -> site.addPage("doctacular-pages", doctacular.pages),
        "Videos" -> site.addPage("doctacular-videos", doctacular.videoIntro),
        "Decks (VSlides)" -> site.addPage("doctacular-vslides", VSlidesExample.page(0)),
        "Slide decks (other kinds)" -> site.addPage("doctacular-other-decks", otherDecks),
        "Challenges" -> site.addPage("doctacular-challenges", doctacular.challenges),
        "Other content" -> site.addPage("doctacular-other-content", doctacular.otherContent),
        "Alternatives" -> site.add("alternatives", 
          Alternative("Play a slide deck", Medium.Deck(() => GoogleSlides("2PACX-1vQ8ZlyoV6f1g1-AWiKiqJ886n6O9sK8XymUirDVUbHYZaTalHE4Cty1BMbZLm0t0SBywAZkmGAOEb0Q"))),
          Alternative("Play a video", Medium.Video(() => YouTube("YE7VzlLtp-4")))
        )
      ),
      "Examples" -> site.Toc(
        "To-Do List" -> site.addPage("to-do-list", ToDoList.page),
      ),
      "Test pages" -> site.Toc(
        "Orbiting asteroids" -> site.addPage("orbiting-asteroids", ReactLike.page),
        "Diffusion" -> site.addPage("diffusion-experiment", Diffusion.SimulationView),
        "Scatter" -> site.addPage("scatter", ScatterExample.page),
        "Slides: Tutorial" -> site.addDeck("doctacular-tutorial-deck", doctacular.tutorialDeck),
        "Challenges" -> site.addChallenge("challenges", ChallengeExample.levels)
      )
    )
    
    // The site's home page is set separately, because many sites have more of a landing page (e.g. without a sidebar)
    // In this case, though, we just tell it to render the Intro page as the homepage.
    site.home = () => site.renderPage(intro)
    
    // Install our custom CSS - note this is the stylings we've created in our custom widgets (rather than the 
    // sidebar styles, which the site will install when we attach it to the DOM)
    siteStyles.install()
    
    // Render our site into the page 
    site.attachTo(dom.document.body)
  }

}
