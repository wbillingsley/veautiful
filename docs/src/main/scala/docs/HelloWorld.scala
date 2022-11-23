package docs

import com.wbillingsley.veautiful.html.{HTML, <, ^}
import HTML.*
import org.scalajs.dom


def helloWorld = <.div(Common.markdown(
  """# Hello World
    |
    |As with any framework, we need a way of getting our UI onto the page. In Veautiful, this is the `Attacher`.
    |The `Attacher` connects to a DOM element, and can render a Veautiful tree into it:
    |
    |```scala
    |import com.wbillingsley.veautiful.html.{Attacher, HTML}
    |import HTML.*
    |import org.scalajs.dom
    |
    |val root = Attacher.newRoot(dom.document.getElementById("render-here"))
    |root.render(p("Hello world"))
    |```
    |
    |Later, we'll render more interesting components that can update themselves, but this will get us started.
    |
    |## Rendering elements
    |
    |As with other frameworks, Veautiful defines a way to write HTML nodes. So, in the code above
    |
    |```scala
    |p("Hello world")
    |```
    |
    |Produces:
    |
    |""".stripMargin),
  div(^.cls := embeddedExampleStyle.className,
    p("Hello World"),
  ),
  Common.markdown("""
    |
    |If you'd like to check, you can take a look at the code for this page on GitHub.
    |
    |### `HTML`, `SVG`, and `<`
    |
    |In our hello world example, I just imported all the HTML tags. (`import HTML.*`.) 
    |I could instead have said `HTML.p("Hello world")`. Or, the `HTML` object is also aliased as `<`. 
    |This can let you write code in a way that is a bit more visually reminiscent of HTML and stands out in your code. 
    |It also helps avoid namespace collisions with the fact that
    |`i`, `a`, `s`, etc, are HTML tag names but also common names people use for little local variables.
    |The `<` style was inspide by the Scalatags project.
    |
    |e.g.
    |
    |```
    |<.div(
    |  <.h1("My heading"),
    |  <.p("And a paragraph")
    |)
    |```
    |
    |There are pre-defined methods on `<` for most HTML tags, but if we've forgotten one, you can create it anyway via
    |(for example):
    | 
    |```scala 
    |<("newFangledElement")("This is a new fangled element that Veautiful doesn't know yet")
    |```
    |
    |There's also an `SVG` object for declaring SVG elements. Within the `com.wbillingsley.veautiful.svg` package,
    |it's also aliased as `<`. 
    |
    |## Attributes, properties, and events
    |
    |Just as the `HTML` and `SVG` objects (or `<`) let us define tags for HTML elements, there is a `modifiers` object
    |in each package that can set attributes, event listeners, and some other special modifiers.
    |
    |It's aliased as `^`, again mimicking the Scalatags style. Or you could `import modifiers.*` if you don't want the `^` prefix.
    |
    |e.g.:
    |
    |```scala
    |<.div(^.cls := embeddedExampleStyle.className,
    |  <.button(
    |    ^.style := "background: cornflowerblue; color: white;",
    |    ^.onClick ==> { (_) => dom.window.alert("I was clicked") },
    |    "Pop an alert"
    |  )
    |)
    |```
    |
    |or 
    |
    |```scala
    |import HTML.*
    |import modifiers.*
    |
    |div(cls := embeddedExampleStyle.className,
    |  button(
    |    style := "background: cornflowerblue; color: white;",
    |    onClick ==> { (_) => dom.window.alert("I was clicked") },
    |    "Pop an alert"
    |  )
    |))
    |```
    |
    |""".stripMargin),
  <.div(^.cls := embeddedExampleStyle.className,
    <.button(
      ^.attr("style") := "background: cornflowerblue; color: white;",
      ^.onClick ==> { (_) => dom.window.alert("I was clicked") },
      "Pop an alert"
    )
  ),
  Common.markdown("""
    |Just as the `<` object contained methods for defining elements, the `^` object contains items for defining
    |attributes, properties, and event handlers. Again, there are methods defined for many of the common ones, but
    |there will be some that have been missed, so:
    |
    |* `^.attr("attrName") := "foo"` sets an attribute 
    |* `^.on("eventName") ==> { (e) => dom.console.log(e) }` sets an event handler
    |* `^.on("eventName") --> { dom.console.log("something happened") }` sets an event handler, if you don't want to 
    |   receive the event object.
    |* `^.prop("propName") := "foo"` sets a property (e.g. `value` for `input` elements)
    |
    |Again, the notation for event handlers is inspired from scalajs-react and scalatags.
    |
    |## Children and Modifiers
    |
    |In the button example, you'll notice that we nest attributes and child elements for a node inside the same 
    |set of parentheses.
    |
    |Technically, what we're doing is calling the `apply` method of the element with a number of `ElementChild[T]`
    |elements. This takes advantage of Scala 3's union types, allowing us to pass:
    |
    |* Child nodes and components
    |* Strings, that are converted to text nodes
    |* Attributes
    |* Properties
    |* Event handler settings
    |* Virtual node settings (extra functionality the framework defines)
    |* or `Iterable[ElementChild[T]]`. i.e. we can pass `Option`s, `Seq`s, and `List`s of modifiers and they'll work as
    |  you'd expect
    |
    |""".stripMargin)
)