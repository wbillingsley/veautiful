package docs.doctacular

import com.wbillingsley.veautiful.html._
import org.scalajs.dom

import docs.Common._

val pages = <.div(
  markdown(
    s"""# Pages
      | 
      |A page in a Doctacular site is made up of Veautiful `VHtmlContent`.
      |
      |For instance we can define a page simply as
      |
      |```scala
      |import com.wbillingsley.veautiful.html.*
      |import HTML.*
      |
      |def myPage = div(
      |  h1("My page"),
      |  p("Here is some content")
      |)
      |```
      |
      |We could then add that to a table of contents
      |
      |```scala
      |site.toc = site.Toc(
      |  "Home" -> site.HomeRoute, 
      |
      |  // "My page" is the text to put in the table of contents navigation
      |  // "my-page" is the string we want to appear in the page's URL route
      |  "My page" -> site.addPage("my-page", myPage)
      |)
      |```
      |
      |## Markdown
      |
      |Commonly, we'll want to use some markdown in the page. 
      |
      |There are many ways to install a markdown parser into your site. For example, using Yarn or Webpack, or as a script tag 
      |in the head of the HTML page. However, if you are looking for something very very small for a tiny site, e.g. a presentation, there
      |is also a method to install one dynamically:
      |
      |```scala
      |import com.wbillingsley.veautiful.doctacular.*
      |
      |// Adds the "installMarked" method to `site`
      |import com.wbillingsley.doctacular.installers.installMarked
      |
      |val site = Site()
      |
      |// Dynamically loads and installs Marked.js into the page.
      |given marked:Markup = site.installMarked()
      |```
      |
      |This puts the `Markup` component into a named given parameter.
      |
      |Essentially:
      |
      |* So long as it's in the package, or imported, methods that look for a given Markup component (e.g. `DeckBuilder.markdownSlide`) will just work
      |* Because it's named, we can call `marked.div(string)` to insert parsed Markdown anywhere else we want, e.g. in the middle of a page.
      |
      |There are other ways to install marked. This is just a super-easy one-liner.
      |As this is loading dynamically, but returning synchronously, however, you might see a brief flash in the content:
      |While the marked.js script loads, markdown content will show that the script is still loading. When it is loaded, the site's router
      |will re-render now that the Markdown parser is present.
      |
      |### Interspersing Markdown with interactive content
      |
      |In pages, I tend to recommend having the outer element be just a `VHtmlContent` `div`, because that
      |makes it easier later to add interactive components (as we can intersperse markdown and Veautiful elements under the div).
      |
      |e.g.
      |
      |```scala
      |import com.wbillingsley.veautiful.html.*
      |import HTML.*
      |
      |def myPage = div(
      |  marked.div(
      |    "# Marked.js content!"
      |  ),
      |  someWidget,
      |  marked.div(
      |    "More *markdown* content!"
      |  )
      |)
      |```
      |
      |""".stripMargin)
)