package docs

import com.wbillingsley.veautiful.html.{<, EventMethods, VHtmlComponent, VHtmlNode, ^, unique}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement

val markupComponents = unique(<.div(
    Common.markdown(
    """
      |# Markup components
      |
      |A lot of sites contain content written in markup languages, such as *Markdown*.
      |For example. much of this documentation is written in Markdown.
      |
      |Although Veautiful doesn't provide a Markdown parser, it does provide a component to help you wire one in.
      |How you use it depends on how you want to bring in a Markdown library.
      |
      |#### Wiring in a markup function from the global scope
      |
      |Suppose your page just imports a Markdown library via a script tag. For instance, marked.js version 3
      |provided a markdown parser in a global function called `marked`
      |
      |```html
      |<script src="https://cdn.jsdelivr.net/npm/marked@3.0.8/marked.min.js"></script>
      |```
      |
      |In this case, we can create a `Markup` helper that uses this global function.
      |Often, we'll declare this helper as a `given` so it can be automatically found by contexts that would
      |like a markdown helper - such as writing VSlides slide decks.
      |
      |```scala
      |import com.wbillingsley.veautiful.html.Markup
      |given marked:Markup = Markup({ (s:String) => js.Dynamic.global.marked.parse(s).asInstanceOf[String] })
      |```
      |
      |#### Wiring in a markup function from a Scala.js ECMAScript Module import
      |
      |It may be, however, that you have Scala.js configured to emit JavaScript modules and are using
      |Webpack to bundle your code with any npm code into a single deployable JavaScript file.
      |
      |In this case, for instance using Marked version 4 which is deployed as a module, we might do this:
      |
      |```scala
      |@js.native
      |@JSImport("marked", "marked")
      |object Marked extends js.Object:
      |  def parse(s:String):String = js.native
      |
      |given markdown:Markup = Markup({ (s:String) => Marked.parse(s) })
      |```
      |
      |#### Including markup in your page
      |
      |`Markup` provides a few different helpers for creating markup nodes. Usually, we want `Markup.Fixed(s)`, which
      |will render a piece of markup into a `div` element. 
      |
      |```scala
      |def myPage = <("article")(
      |  markdown.Fixed("# This is a page about `SomeFunkyWidget`"),
      |  SomeFunkyWidget(),
      |  markdown.Fixed("This surrounding text is written in *Markdown*")    
      |)
      |```
      |
      |`Markup.Fixed(s)` uses string equality to determine whether it should be replaced. In other words, if you re-render the page with a different string, 
      |it will throw away the old `div` and you'll get a new `div` containing the new content.
      |
      |""".stripMargin
    )

))