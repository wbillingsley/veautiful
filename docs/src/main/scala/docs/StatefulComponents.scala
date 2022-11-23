package docs

import com.wbillingsley.veautiful.html.{<, DHtmlComponent, ^, EventMethods}

case class Greeter() extends DHtmlComponent {

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
    |# Stateful components
    |
    |If your component needs to keep some ephemeral local state, implementing it as a class extending 
    |`DHtmlComponent`. Usually a case class.
    |
    |`DHtmlComponent` requires you to implement a `render` method to describe the tree your component should produce. 
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
        |import com.wbillingsley.veautiful.html.{<, DHtmlComponent, ^, EventMethods}
        |
        |case class Greeter() extends DHtmlComponent {
        |
        |  private var name:String = "World"
        |
        |  override def render = <.div(^.cls := "hello-world",
        |    <.input(
        |      ^.prop("value") := name,
        |      ^.attr("placeholder") := "Hello who?", ^.onInput ==> { e => e.inputValue.foreach(name = _); rerender() }
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
    |Often, components are implemented as `case classes`. This tells the reconciler when to try to retain your component and
    |when to replace it. The default strategy is to try to keep your component if it equals the target, and replace it if it doesn't.
    |Making your component a case class makes it simple and clear to show what would make your component "not equal"
    |to another instance of itself (and require replacement).
    |
    |However, if you don't want your component to be a case class, you can also implement `Keep(value)` to tell the reconciler
    |when to keep your component.
    |
    |e.g. 
    |
    |```scala
    |// This isn't a case class, so `MyComponent(1, 2) != MyComponent(1, 2)` 
    |// But because it implements Keep((a, b)), the component would be hinted as being retained rather than replaced.
    |class MyComponent(a:Int, b:Int) extends DHtmlComponent with Keep((a, b)) {
    |  val now = System.currentTimeMillis
    |  def render = p(s"I was created at $now, and $a + $b = ${a + b}")
    |}
    |```
    |
    |## Using external state
    |
    |You can also define `DHtmlComponents` that use external state - for instance, if your state is kept in a reactive
    |date store. In this case, you can just hook the `rerender` method to be triggered by your data store's event notification.
    |
    |Override the `afterAttach` method to install your event hook, and override `beforeDetach` to remove it.
    |
    |You should put some thought into your site's update strategy, however. It is, for instance, often feasible just to 
    |define function components, and trigger a top-level re-render (usually on the router) when data changes.
    """.stripMargin)
)
