package docs

import com.wbillingsley.veautiful.html.{<, EventMethods, VDomNode, ^, unique}
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
      |given marked:Markup = Markup({ (s:String) => Marked.parse(s) })
      |```
      |
      |#### Including markup in your page
      |
      |`Markup` provides a few different helpers for creating markup nodes. Usually, we want `Markup.div(s)`, which
      |will render a piece of markup into a `div` element. 
      |
      |```scala
      |def myPage = <("article")(
      |  marked.div("# This is a page about `SomeFunkyWidget`"),
      |  SomeFunkyWidget(),
      |  marked.div("This surrounding text is written in *Markdown*")    
      |)
      |```
      |
      |For rarer cases, you can specify your own outer element. For example:
      |
      |```scala
      |// This should take an element (not a nested structure) in its second parameter.
      |marked.Fixed("Some *Markdown* text", <("article")(^.cls := "my-own-css-class"))
      |```
      |
      |These produce a `Markup.Fixed` component that uses string equality to determine whether it should be replaced. 
      |In other words, if you re-render a page that contains a `markup.div(s)` with a different string, 
      |the old `div` will be disposed of and you'll get a new `div` containing the new content.
      |
      |#### Rendering to inline elements
      |
      |It may be that you just want a single line of Markdown rendered into an inline element, rather than a block element.
      |For this, there is `Markup.span(s)` which renders into a `<span>`.
      |
      |However, depending on the Markdown library you use, there can be a very small additional step - it may be that your
      |markdown engine by default wraps text in paragraph elements. (E.g. `marked.parse(s)` would render a single line of
      |text into a `<p>` element.) If so, you might find yourself wanting to declare a *second* `Markup` generator, using
      |your parser's `inlineParse` function. E.g.:
      |
      |```scala
      |// This lets me use marked.div(s) for block text
      |given marked:Markup = Markup({ (s:String) => marked.parse(s) })
      |
      |// This lets me use markedInline.span(s) for inline text
      |val markedInline:Markup = Markup({ (s:String) => marked.parseInline(s) })
      |```
      |
      |#### Updatable and Settable
      |
      |Generally, I recommend just using `markup.div(s)` and `markupInline.span(s)`. They are small and have low overhead as
      |it's usually not necessary to re-run the markdown parser (if the string is the same, there's no need; if it's different they'd be replaced anyway).
      |
      |However, there are also `Markup.Settable()(s)` and `Markup.Updatable()(() => s)`. `Settable` works like a morphing component, updating its content
      |whenever it changes. `Updatable` accepts a function returning the Markdown string, and re-evaluates that function each update cycle.
      |""".stripMargin
    )

))