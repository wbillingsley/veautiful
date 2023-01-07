package docs.doctacular

import com.wbillingsley.veautiful.html._
import org.scalajs.dom

import docs.marked

val singleDeck = <.div(marked.div(
    s"""# Displaying a single deck
       |
       |Writing sites and decks using Doctacular should feel like scripting, rather than programming. 
       |
       |If we are going to show a deck on the web, however, we will need two things:
       |
       |* A tiny fragment of HTML, which is just going to load and execute our script
       |* The script itself, which `scala-cli` (or `sbt` for more complex sites) is going to compile into JavaScript
       |
       |There are relatively simple ways to engineer larger sites, but this is the tiny version for publishing a single slide deck.
       |
       |### Minimal HTML
       |
       |The HTML we need to load our site is very small:
       |
       |```html
       |<!DOCTYPE html>
       |<html>
       |  <!-- or whatever scala-cli generates your script as -->
       |  <script src="myapp.main.js"></script>
       |</html>
       |```
       |
       |That's it. `head` and `body` tags are not mandatory in HTML (the browser wiill infer them) so we've left them out and just put in the script tag.
       |
       |### Mounting a single deck
       |
       |And here's a minimal bit of Scala to render a single deck. This is written as if you're using scala-cli's `.sc` script extension.
       |If you're writing a `.scala` file, just put the lines from `DeckBuilder` on into a `main` method.
       |
       |```scala
       |// Download the library
       |import $$dep.`com.wbillingsley::doctacular::${docs.latestVersion}`
       |
       |// So we don't have to keep saying com.wbillingsley everywhere
       |import com.wbillingsley.veautiful.veautiful.html.*
       |import com.wbillingsley.veautiful.doctacular.*
       |import installers.installMarked
       |
       |// Two lines to load marked.js dynamically, because we'll include Markdown in our slides
       |val root = mountToBody(HTML.div("Loading..."))
       |given root.installMarked() // this makes marked.js "implicitly" available to the DeckBuilder to parse Markdown
       |
       |// Now just script and mount the deck
       |DeckBuilder(1920, 1080)
       |  .markdownSlide(
       |    \"\"\"
       |    |# My title 
       |    |
       |    |My fancy slide deck
       |    |    \"\"\".stripMargin
       |  ).withClass("center middle")
       |  .markdownSlides(
       |    \"\"\"
       |    |# Markdown slides
       |    |
       |    |Markdown slides can just be composed out of strings
       |    |
       |    |---
       |    |
       |    |# Separating slides
       |    | 
       |    |If you have a bunch of Markdown slides, you can just separate them with `---`
       |    |
       |    |\"\"\".stripMargin
       |  )
       |  .veautifulSlide(
       |    div(
       |      h1("Interactive content"),
       |      p("But we can also insert slides with interactive widgets"),
       |      someFancyWidget()
       |    )
       |  )
       |  .mountToRoot(root)
       |```
       |""".stripMargin
))