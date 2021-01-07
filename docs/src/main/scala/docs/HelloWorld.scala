package docs

import com.wbillingsley.veautiful.html.{<, ^}
import org.scalajs.dom

def helloWorld = <.div(Common.markdown(
  """# Hello World
    |
    |As with any framework, we need a way of getting our UI onto the page. In Veautiful, this is the `Attacher`.
    |The `Attacher` connects to a DOM element, and can render a Veautiful tree into it:
    |
    |```scala
    |import com.wbillingsley.veautiful.html.{Attacher, <, ^}
    |import org.scalajs.dom
    |
    |val root = Attacher.newRoot(dom.document.getElementById("render-here"))
    |root.render(<.p("Hello world"))
    |```
    |
    |Later, we'll render more interesting components that can update themselves, but this will get us started.
    |
    |## HTML elements
    |
    |As with other frameworks, Veautiful defines a way to write HTML nodes. So, in the code above
    |
    |```scala
    |<.p("Hello world")
    |```
    |
    |Produces:
    |
    |""".stripMargin),
  <.div(^.cls := embeddedExampleStyle.className,
    <.p("Hello World"),
  ),
  Common.markdown("""
    |
    |If you'd like to check, you can take a look at the code for this page on GitHub.
    |
    |`<` is just an object holding the `p` method for defining a `p` element. The library's use of `<` and `^` is
    |inspired by scalatags and scalajs-react, and hopefully is visually reminiscent of all the angle brackets in HTML.
    |
    |There are pre-defined methods on `<` for many HTML tags, but if we've forgotten one, you can create it anyway via
    |(for example):
    | 
    |```scala 
    |<("aside")("This is an aside")
    |```
    |
    |There's also an `SVG` object for declaring elements in the SVG namespace.
    |
    |## Attributes, properties, and events
    |
    |Let's start with an example:
    |
    |```scala
    |<.div(^.cls := embeddedExampleStyle.className,
    |  <.button(
    |    ^.attr("style") := "background: cornflowerblue; color: white;",
    |    ^.onClick ==> { (_) => dom.window.alert("I was clicked") },
    |    "Pop an alert"
    |  )
    |)
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
    |there will be plenty that have been missed, so:
    |
    |* `^.attr("attrName") := "foo"` sets an attribute 
    |* `^.on("eventName") ==> { (e) => dom.console.log(e) }` sets an event handler
    |* `^.on("eventName") --> { dom.console.log("something happened") }` sets an event handler, if you don't want to 
    |   receive the event object.
    |* `^.prop("propName") := "foo"` sets a property (e.g. `value` for `input` elements)
    |
    |Again, the notation for event handlers is inspired from scalajs-react and scalatags.
    |
    |## Element children
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
    |* or `Iterable[ElementChild[T]]`
    | 
    |The fact that we can pass an `Iterable` means we can pass `Option`s, `Seq`s, `List`s, and they'll work as you'd
    |expect.
    |
    |""".stripMargin)
)