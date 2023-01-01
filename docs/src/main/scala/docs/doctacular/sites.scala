package docs.doctacular

import com.wbillingsley.veautiful.html._
import org.scalajs.dom

import docs.Common._

val sites = <.div(
  markdown(
    """# Sites
      |
      |A `Site` helps you to build an Open Educational Resource or a documentation site like this one.
      |
      |It has a sidebar and navigation based on some content types that are useful for teaching. 
      |Most of it is defined through its table of contents, though you'll also need to define the home page
      |(as some sites like that to look more like a cover page than page one of the content).
      |
      |### Creating a site
      |
      |```scala
      |import com.wbillingsley.veautiful.doctacular.*
      |
      |val site = Site()
      |site.toc = site.Toc(
      |  // This is going to be the table of contents
      |)
      |
      |// The site's home page is set separately, because many sites have more of a landing page (e.g. without a sidebar)
      |// In this case, though, we just tell it to render the Intro page as the homepage.
      |site.home = () => site.renderPage(intro)
      |
      |// Render our site into the page 
      |// Here, we've told it to take over the whole body, though we could also use `attachTo` and give it a particular element to render into.
      |site.attachToBody()
      |```
      | 
      |### The table of contents
      |
      |Generally, I find it easiest to see the table of contents as the way into defining a site. For example:
      |
      |```scala
      |    // The TOC is the table of contents in the left side-bar.
      |    // In this case, at the same time as setting the TOC, we're also adding the pages to the site's router.
      |    site.toc = site.Toc(
      |      // A logo. TocNodeLink takes any VNode and lets it be a link
      |      site.TocNodeLink(myLogo, site.HomeRoute),
      |
      |      // Just a line separator
      |      site.TocLine,
      |
      |      // "Intro" is the heading. We've set it to have a sub-table of contents
      |      "Intro" -> site.Toc(
      |        "Home" -> site.HomeRoute,
      |        "Hello world" -> site.addPage("hello-world", helloWorld),
      |        "Low-level components" -> site.addPage("low-level", lowlevelComponents),
      |        "Function components" -> site.addPage("pure-functions", pureFunctions),
      |        "Stateful components" -> site.addPage("stateful-components", statefulComponents),
      |         // etc
      |      ),
      |      "A Section of components" -> site.Toc(
      |        "A page" -> site.addPage("doctacular-intro", doctacular.introPage),
      |        "A deck" -> site.addDeck("doctacular-sites", myFirstDeck),
      |        "Alternatives" -> site.add("alternatives", 
      |          Alternative("Play a slide deck", Medium.Deck(() => GoogleSlides("2PACX-1vQ8ZlyoV6f1g1-AWiKiqJ886n6O9sK8XymUirDVUbHYZaTalHE4Cty1BMbZLm0t0SBywAZkmGAOEb0Q"))),
      |          Alternative("Play a video", Medium.Video(() => YouTube("YE7VzlLtp-4")))
      |        )
      |      ),
      |    )
      |```
      |
      |Each of the values in the deck, for logos, decks and pages (e.g. `myLogo`, `helloWorld`, `lowLevelComponents`, `myFirstDeck`) wouuld be defined
      |elsewhere in the script or in another Scala file. 
      |
      |The types depend on the content, but are hopefully straightforward:
      |
      |* `addDeck` takes a slide deck
      |* `addPage` takes content for a page. (Typically the outermost-element is a div)
      |* etc. See the pages for each type of content for examples of how to script/define them
      |
      |""".stripMargin)
)