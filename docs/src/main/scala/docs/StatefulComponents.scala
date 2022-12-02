package docs

import com.wbillingsley.veautiful.html.{<, DHtmlComponent, ^, EventMethods, getTargetValue}

case class Greeter() extends DHtmlComponent {

  private val name = stateVariable("")

  override def render = <.div(^.cls := "hello-world",
    <.input(
      ^.prop("value") := name.value,
      ^.attr("placeholder") := "Hello who?", ^.onInput ==> getTargetValue.pushTo(name)
    ),
    <.span(s" Hello ${ (if name.value.isEmpty then "World" else name.value) }"),
  )

}

def statefulComponents = <.div(Common.markdown("""
    |# Stateful components
    |
    |If your component needs to keep some ephemeral local state, implement it as a class extending 
    |`DHtmlComponent`. Usually a case class. 
    |
    |`DHtmlComponent` requires you to implement a `render` method to describe the tree your component should produce. 
    |This should produce a consistent outer element (e.g. a `<.div()` or a `<.span()`), but its contents can then be
    |whatever you want.
    |
    |If you want to handle updates somewhat manually, you can then keep local state inside your class, and your component 
    |can ask itself to rerender at any time using the component's `rerender` method.
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
    |to re-render *this component*. We could go all the way up to re-rendering the Attacher if we want. However, usually that's
    |not necessary. Veautiful is designed so that updates tend to be local and more efficient.
    |
    |### Case classes versus Keep
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
    |### State variables and the Animator
    |
    |Rather than keep our state in an ordinary var, and asking for an immediate rerender manually, we could keep our state in *state variablse*. 
    |
    |These are designed to work in a very simple manner. When they receive a new value, they ask the `Animator` to queue a re-render of the component.
    |To create a state variable, inside your component initialise it with
    |
    |```scala
    |val myState = stateVariable("") // Produces a StateVariable[String]
    |```
    |
    |The value could be manually updated either via `value =` or via `receive`
    |
    |```scala
    |// Either of these would update the value and request a rerender
    |myState.value = "new value"
    |myState.receive("new value")
    |```
    |
    |We can tie these to events using ordinary functions, e.g. 
    |
    |```scala
    |^.onInput ==> { (e) => e.inputValue.foreach(myState.value = _) }
    |```
    |
    |or there are some function builders that can let you write that in a more fluid way. But note you are still just building an ordinary
    |function and passing it to the event hander; there's no special sauce going on here.
    |
    |```
    |^.onInput ==> getTargetValue.pushTo(myState)
    |```
    |
    |The `Animator` is an object whose job is simply to call requestAnimationFrame when it has a batch of renders to make. It calls all its 
    |queued tasks with *the same timestamp*, so if a component has already re-rendered in this animation frame it won't do it again. That lets
    |us have a bunch of state variables and not worry about whether anything's getting rerendered multiple times.
    |
    |Here's that component using this style of code
    |
    |```scala
    |case class Greeter() extends DHtmlComponent {
    |
    |  private val name = stateVariable("")
    |
    |  override def render = <.div(^.cls := "hello-world",
    |    <.input(
    |      ^.prop("value") := name.value,
    |      ^.attr("placeholder") := "Hello who?", ^.onInput ==> getTargetValue.pushTo(name)
    |    ),
    |    <.span(s" Hello ${ (if name.value.isEmpty then "World" else name.value) }"),
    |  )
    |
    |}
    |```
    |
    |### Queueing your own animation frames
    |
    |If you want to queue a rerender on the next animation frame (without using a state variable), call `requestUpate()` on the component.
    |
    |There is also an `update()` method that would produce an immediate update. However, usually if you're using the Animator, it's cleaner
    |to put all the updates for the component through it.
    |
    """.stripMargin)
)
