package example

import com.wbillingsley.veautiful.html.<

object Intro {

  def page = Common.layout(<.div(
    <.h1("Veautiful"),
    <.p(
      """
        | Veautiful is a home-grown declarative web UI for Scala.js. It's inspired by
        | several other projects, notably React.js, scalatags, d3.js, and the UI components of
        | The Intelligent Book.
      """.stripMargin),
    <.h3("Hello world"),
    <.p(
      """
        | The Attacher can attach a root node for a virtual DOM like this:
      """.stripMargin),
    <("pre")(
      """
        | val domEl = dom.document.getElementById("render-here")
        | val root = Attacher.newRoot(domEl)
      """.stripMargin),
    <.p(
      """
        | The new root can then render a virtual DOM like this:
      """.stripMargin),
    <("pre")(
      """
        | root.render(<.p("Hello world))
      """.stripMargin),
    <.p(
      """
        | As with React, this calling a top-level render again will cause the virtual DOM to
        | be diffed to decide what elements to replace and what to update.
      """.stripMargin),
    <.p(
      """
        | However, not every node in the tree has to produce a virtual DOM, and
        | not every re-render has to be at the top level. Rather than let the framework dictate
        | how your app works, it just provides a set of component primitives that compose
        | together neatly. This makes it very easy to mix in components using d3.js, or have
        | the UI tree bridge fairly seemlessly across nodes that render themselves into the DOM,
        | and nodes that paint themselves and their children onto a canvas. It also supports
        | reactive programming well, without forcing you to use it.
      """.stripMargin)
  ))

}
