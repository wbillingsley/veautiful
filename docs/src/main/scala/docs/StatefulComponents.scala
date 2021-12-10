package docs

import com.wbillingsley.veautiful.html.{<, VHtmlComponent, ^, EventMethods}

case class Greeter() extends VHtmlComponent {

  private var name:String = ""

  override def render = <.div(^.cls := "hello-world",
    <.input(
      ^.prop("value") := name,
      ^.attr("placeholder") := "Hello who?", ^.on("input") ==> { e => e.inputValue.foreach(name = _); rerender() }
    ),
    <.span(s" Hello ${ (if (name.isEmpty) "World" else name) }"),
  )

}

def statefulComponents = <.div(Common.markdown("""
    |# Stateful Components
    |
    |If your component needs to keep some ephemeral local state, implement it as a *case class* extending 
    |`VHtmlComponent`. 
    |
    |This requires you to implement a `render` method to describe the tree your component should produce. 
    |This should produce a consistent outer element (e.g. a `<.div()` or a `<.span()`), but its contents can then be
    |whatever you want.
    |
    |You can then keep local state inside your class, and your component can ask itself to rerender at any time using
    |the component's `rerender` method.
    |
    |For example:
    |
    |""".stripMargin
  ),  
  <.div(^.cls := embeddedExampleStyle.className,
    Greeter(), <.p(),
    Common.markdown(
      """
        |```scala
        |import com.wbillingsley.veautiful.html.{<, VHtmlComponent, ^, EventMethods}
        |
        |case class Greeter() extends VHtmlComponent {
        |
        |  private var name:String = "World"
        |
        |  override def render = <.div(^.cls := "hello-world",
        |    <.input(
        |      ^.prop("value") := name,
        |      ^.attr("placeholder") := "Hello who?", ^.on("input") ==> { e => e.inputValue.foreach(name = _); rerender() }
        |    ),
        |    <.span(s" Hello ${ (if (name.isEmpty) "World" else name) }"),
        |  )
        |
        |}
        |```
        |""".stripMargin
    )
  ),
  Common.markdown(
    """ 
    |This will use the default reconciliation strategy to update its DOM tree in the page. This example has been written just
    |to re-render *this component*. We could go all the way up to re-rendering the Attacher if we want, however. Usually,
    |there's some component in the tree where it's obvious you'll want to trigger a re-renders &mdash; even if only the
    |site's router.
    |
    |Typically, components are implemented as `case classes`. This is not a strict requirement, but it makes things
    |much simpler to write because components that use a virtual DOM style (reconciling their children for updates)
    |use equality to determine whether a component needs to be replaced or can just be asked to reconcile itself.
    |Making your component a case class makes it simple and clear to show what would make your component "not equal"
    |to another instance of itself (and require replacement).
    |
    |## Using external state
    |
    |You can also define `VHtmlComponents` that use external state - for instance, if your state is kept in a reactive
    |date store. In this case, you can just hook the `rerender` method to be triggered by your data store's event notification.
    |
    |Override the `afterAttach` method to install your event hook, and override `beforeDetach` to remove it.
    |
    |You should put some thought into your site's update strategy, however. It is, for instance, often feasible just to 
    |define function components, and trigger a top-level re-render (usually on the router) when data changes.
    """.stripMargin)
)
