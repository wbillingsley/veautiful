package docs.doctacular

import com.wbillingsley.veautiful.html._
import org.scalajs.dom

import docs.Common._

val introPage = <.div(
  markdown(
    """# Doctacular
      | 
      |Doctacular is a site generator based on Veautiful. To see what one looks like, look at this site!
      |
      |Or, these are also built using Doctacular:
      |
      |  * [Thinking About Programming](https://theintelligentbook.com/thinkingaboutprogramming) - embedded robot simulations
      |    in a little course that teaches computational thinking.
      |  * [Circuits Up](https://theintelligentbook.com/circuitsup) - embedded circuit simulations in a little course
      |    that teaches computer architecture from electronics up.
      |  * [The Adventures of Will Scala](https://theintelligentbook.com/willscala) - a simpler site (mostly video
      |    and text) that goes alongside my undergraduate Scala course.
      |  * [Supercollaborative](https://theintelligentbook.com/supercollaborative) - for a software studio course, includes
      |    a git simulation built into the decks in the version control week.
      |
      |Predominantly, doctacular gives a site a sidebar and navigation based on some content types that are useful for Open
      |Educational Resources. e.g.
      |
      |  * Slide decks (in either a flat vertical notes-like view, or playable)
      |  * Vidoes (often of what was in the slide decks)
      |  * Pages
      |  * Challenges
      |
      |They're configured like they're a little script. Adding content and setting a table of contents.
      |
      |### Creating a site
      |
      |```scala
      |import com.wbillingsley.veautiful.html.{Attacher, StyleSuite, Styling, <}
      |import com.wbillingsley.veautiful.doctacular.*
      |
      |val site = Site()
      |site.toc = site.Toc(
      |  // This is going to be the table of contents      |
      |)
      |
      |// The site's home page is set separately, because many sites have more of a landing page (e.g. without a sidebar)
      |// In this case, though, we just tell it to render the Intro page as the homepage.
      |site.home = () => site.renderPage(intro)
      |
      |// Render our site into the page 
      |site.attachTo(dom.document.getElementById("render-here"))
      |```
      | 
      |### The table of contents
      |
      |Generally, I find it easiest to see the table of contents as the way into defining a site. For now, let's show the example of this site
      |
      |```scala
      |    // The TOC is the table of contents in teh left side-bar.
      |    // In this case, at the same time as setting the TOC, we're also adding the pages to the site's router.
      |    site.toc = site.Toc(
      |      site.TocNodeLink(Common.logoWithTitle(150, 150), site.HomeRoute),
      |      site.TocLine,
      |      "Intro" -> site.Toc(
      |        "Home" -> site.HomeRoute,
      |        "Hello world" -> site.addPage("hello-world", helloWorld),
      |        "Low-level components" -> site.addPage("low-level", lowlevelComponents),
      |        "Function components" -> site.addPage("pure-functions", pureFunctions),
      |        "Stateful components" -> site.addPage("stateful-components", statefulComponents),
      |        "Morphing components" -> site.addPage("morphing-components", morphingComponents),
      |        "Dynamic state" -> site.addPage("dynamic-state", dynamicState),
      |        "Performance" -> site.addPage("low-level-components", performance),
      |        "Markup components" -> site.addPage("markup-components", markupComponents),
      |        "CSS in JS" -> site.addPage("style-suites", styleSuites)
      |      ),
      |      "Design" -> site.Toc(
      |        
      |      ),
      |      "Extras" -> site.Toc(
      |        "VSlides" -> site.addPage("vslides", VSlidesExample.page(0)),
      |        "Challenges" -> site.addChallenge("challenges", ChallengeExample.levels)
      |      ),
      |      "Doctacular" -> site.Toc(
      |        "Intro" -> site.addPage("doctacular-intro", doctacular.introPage),
      |        "Slides: Tutorial" -> site.addDeck("doctacular-tutorial-deck", doctacular.tutorialDeck),
      |        "Videos" -> site.addPage("doctacular-videos", doctacular.videoIntro),
      |        "Alternatives" -> site.add("alternatives", 
      |          Alternative("Play a slide deck", Medium.Deck(() => GoogleSlides("2PACX-1vQ8ZlyoV6f1g1-AWiKiqJ886n6O9sK8XymUirDVUbHYZaTalHE4Cty1BMbZLm0t0SBywAZkmGAOEb0Q"))),
      |          Alternative("Play a video", Medium.Video(() => YouTube("YE7VzlLtp-4")))
      |        )
      |      ),
      |      "Examples" -> site.Toc(
      |        "To-Do List" -> site.addPage("to-do-list", ToDoList.page),
      |        "Orbiting asteroids" -> site.addPage("orbiting-asteroids", ReactLike.page),
      |        "Diffusion" -> site.addPage("diffusion-experiment", Diffusion.SimulationView),
      |        "Scatter" -> site.addPage("scatter", ScatterExample.page)
      |      )
      |    )
      |```
      |
      |""".stripMargin)
)