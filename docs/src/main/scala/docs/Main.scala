package docs

import com.wbillingsley.veautiful.html.{Attacher, StyleSuite, Styling, <}
import com.wbillingsley.veautiful.doctacular.*

import org.scalajs.dom

/** This is our own set of styles for widgets that we define */
given siteStyles:StyleSuite = StyleSuite()
val site = Site()


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
    
    // The TOC is the table of contents in teh left side-bar.
    // In this case, at the same time as setting the TOC, we're also adding the pages to the site's router.
    site.toc = site.Toc(
      site.TocNodeLink(Common.logoWithTitle(150, 150), site.HomeRoute),
      site.TocLine,
      "Intro" -> site.Toc(
        "Home" -> site.HomeRoute,
        "Hello world" -> site.addPage("hello-world", helloWorld),
        "Function components" -> site.addPage("pure-functions", pureFunctions),
        "Stateful components" -> site.addPage("stateful-components", statefulComponents),
        "Morphing components" -> site.addPage("morphing-components", morphingComponents),
        "Dynamic state" -> site.addPage("dynamic-state", dynamicState),
        "Low level components" -> site.addPage("low-level-components", advancedComponents),
        "Markup components" -> site.addPage("markup-components", markupComponents),
        "CSS in JS" -> site.addPage("style-suites", styleSuites)
      ),
      "Design" -> site.Toc(
        
      ),
      "Extras" -> site.Toc(
        "VSlides" -> site.addPage("vslides", VSlidesExample.page(0)),
        "Challenges" -> site.addChallenge("challenges", ChallengeExample.levels)
      ),
      "Doctacular" -> site.Toc(
        "Intro" -> site.addPage("doctacular-intro", doctacular.introPage),
        "Slides: Tutorial" -> site.addDeck("doctacular-tutorial-deck", doctacular.tutorialDeck),
        "Videos" -> site.addPage("doctacular-videos", doctacular.videoIntro),
        "Alternatives" -> site.add("alternatives", 
          Alternative("Play a slide deck", Medium.Deck(() => GoogleSlides("2PACX-1vQ8ZlyoV6f1g1-AWiKiqJ886n6O9sK8XymUirDVUbHYZaTalHE4Cty1BMbZLm0t0SBywAZkmGAOEb0Q"))),
          Alternative("Play a video", Medium.Video(() => YouTube("YE7VzlLtp-4")))
        )
      ),
      "Examples" -> site.Toc(
        "To-Do List" -> site.addPage("to-do-list", ToDoList.page),
        "Orbiting asteroids" -> site.addPage("orbiting-asteroids", ReactLike.page),
        "Diffusion" -> site.addPage("diffusion-experiment", Diffusion.SimulationView),
        "Scatter" -> site.addPage("scatter", ScatterExample.page)
      )
    )
    
    // The site's home page is set separately, because many sites have more of a landing page (e.g. without a sidebar)
    // In this case, though, we just tell it to render the Intro page as the homepage.
    site.home = () => site.renderPage(intro)
    
    // Install our custom CSS - note this is the stylings we've created in our custom widgets (rather than the 
    // sidebar styles, which the site will install when we attach it to the DOM)
    siteStyles.install()
    
    // Render our site into the page 
    site.attachTo(dom.document.getElementById("render-here"))
  }

}
