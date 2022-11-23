package docs

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.{<, Markup, SVG, VDomNode, DHtmlComponent, ^}
import scala.scalajs.js
import Common._


case class MyMorphingComponent()(initialName:String) extends DHtmlComponent with Morphing(initialName) {
  val morpher = createMorpher(this)
  
  var count = 0
  
  def render = <.div(
    s"I'm a counter that's been clicked $count times and my property is $prop ",
    <.button(^.onClick --> { count += 1; rerender() }, "Increment count")
  )
  
}

case class BobOrSusan() extends DHtmlComponent {
  var toggle = true
  
  def render = <.div(
    "Switch the counter name to ", 
    <.button(^.onClick --> { toggle = !toggle; rerender() }, (if toggle then "Susan" else "Bob")),
    MyMorphingComponent()(if toggle then "Bob" else "Susan")
  )
}


def morphingComponents = <.div(
  markdown(
    """# Morphing Components
      |
      |Sometimes, you might want to preserve some ephemeral internal state but also allow parent components to pass
      |you different properties.
      |
      |For instance, consider the sidebar in this Doctacular site. Whether it is open or closed is just a matter for
      |the widget - it's a rendering detail, not a piece of important site-wide state, so we just keep it as a boolean
      |inside the widget. 
      |But we'd like it to *stay* open or closed as we move from page to page, so we want to be able to change the
      |content of the widget without replacing the widget itself.
      |
      |We could keep our component in a `val` (to preserve its internal state) and call some update method on it
      |(to update it's content), but that can break the declarative style of our code. Sometimes we'd rather just say 
      |
      |```
      |<.div(
      |  SideBarAndLayout()(leftContent, rightContent)
      |)
      |```
      |
      |When we render it again with a different piece of `rightContent`, we'd like the already-rendered `SideBarAndLayout` to 
      |preserve its instance (keeping all its ephemeral state) but *morph* itself to match the new content we'd like to be 
      |rendered.
      |
      |
      |## Make it so!
      |
      |(Yes, that name was picked from a certain balding starship captain, to make it memorable.)
      |
      |Veautiful components can implement a trait called `MakeItSo` to indicate that, if asked, they can
      |morph themselves to match another version of the same component. To implement `MakeItSo`, you write a partial 
      |function, `makeItSo`, that performs any internal updates you need to do.
      |
      |We could do this manually. We'd need to:
      |
      |* Keep the property in an internal `var`
      |* Declare `makeItSo`, which will update the property and then call `update` to trigger the component to rerender
      |  itself.
      |  
      |It's not a lot of work, but it's common enough that the library provides a helper that keeps it tidy and typesafe:
      |
      |## Morphing
      |
      |When defining a `DHtmlComponent`, you can mix in `Morphing[T]`. For example:
      |
      |```scala
      |case class MyMorphingComponent()(initialName:String) extends DHtmlComponent with Morphing(initialName) {
      |
      |  // Implementing the trait just requires you to insert this line. 
      |  val morpher = createMorpher(this)
      |  
      |  var count = 0
      |  
      |  def render = <.div(
      |    s"I'm a counter that's been clicked $count times and my property is $prop ",
      |    <.button(^.onClick --> { count += 1; rerender() }, "Increment count")
      |  )
      |  
      |}
      |```
      |
      |Now, if your component is asked to morph itself to match a component with a different name, it will do so, 
      |updating its internal `prop` variable and re-rendering itself.
      |
      |It also defines an `updateProp` method if you want to update the prop and trigger a re-render from internal code.
      |
      |Let's put that into a (slightly contrived) simple example. We'll embed our counter inside a component that
      |lets us flip its property between "Susan" and "Bob"
      |
      |""".stripMargin),
  <.div(^.cls := embeddedExampleStyle.className,
    BobOrSusan(), <.p(),
    <.pre(
      """
        |case class MyMorphingComponent()(initialName:String) extends DHtmlComponent with Morphing(initialName) {
        |  val morpher = createMorpher(this)
        |  
        |  var count = 0
        |  
        |  def render = <.div(
        |    s"I'm a counter that's been clicked $count times and my property is $prop ",
        |    <.button(^.onClick --> { count += 1; rerender() }, "Increment count")
        |  )
        |  
        |}
        |
        |case class BobOrSusan() extends DHtmlComponent {
        |  var toggle = true
        |  
        |  def render = <.div(
        |    "Switch the counter name to ", 
        |    <.button(^.onClick --> { toggle = !toggle; rerender() }, (if toggle then "Susan" else "Bob")),
        |    MyMorphingComponent()(if toggle then "Bob" else "Susan")
        |  )
        |}
        |""".stripMargin)
  ),
  markdown(
    """
      |## The sidebar, really
      |
      |For a more realistic example, this is how the Doctacular sidebar is defined:
      |
      |```scala
      |case class SideBarAndLayout()(initL: () => VDomNode, initR: () => VDomNode, var open:Boolean = true) 
      |           extends DHtmlComponent with Morphing((initL, initR)) {
      |
      |  val morpher = createMorpher(this)
      |   
      |  def sideBarToggle = <.button(^.cls := sideBarToggleStyle.className, ^.onClick --> {
      |      open = !open
      |      rerender()
      |    }, <.div(^.cls := sideBarSymbolStyle.className)
      |  )
      |
      |  override def render = {
      |    val (left, right) = prop
      |    
      |    <.div(^.cls := (if open then leftSideBarAndContentStyle.className else s"${leftSideBarAndContentStyle.className} closed"),
      |      <("aside")(^.cls := (if open then leftSideBarStyle.className else s"${leftSideBarStyle.className} closed"),
      |        left(),
      |      ),
      |      <.div(
      |        sideBarToggle,
      |        <.div(^.cls := (if open then contentContainerSidebarOpenStyle.className else contentContainerStyle.className),
      |          right()
      |        )
      |      )
      |    )
      |  }
      |
      |}
      |```
      |
      |
      |## Keeping it type-safe
      |
      |You might notice the line:
      |
      |```scala
      |val morpher = createMorpher(this)
      |```
      |
      |This design ensures the Scala compiler can warn us correctly about a possible typing issue.
      |
      |If you define a component with a concrete property type, it will compile correctly with no warnings:
      |
      |```scala
      |case class MyIntMorph()(init:Int) extends DHtmlComponent with Morphing(init) {
      |  val morpher = createMorpher(this)
      |  def render = // Whatever we want to render
      |}
      |```
      |
      |But suppose we tried to declare a morphing component that had a type parameter for its property:
      |
      |```scala
      |case class MyAnyMorph[T]()(init:T) extends DHtmlComponent with Morphing(init) {
      |  val morpher = createMorpher(this)
      |  def render = // Whatever we want to render
      |}
      |```
      |
      |In this case, the Scala compiler will (correctly) give you the warning:
      |
      |```
      |the type test for docs.MyAnyMorpher[T] cannot be checked at runtime
      |```
      |
      |We want that warning. In Scala, types are erased at runtime. If our morpher has a parametrised type T, at runtime
      |we don't know what it is. That means we could do this:
      |
      |```scala
      |val myIntMorpher = MyAnyMorph()(1)
      |val myStringMorpher = MyAnyMorpher()("Hello")
      |
      |// This would fail at runtime because we can't assign the string "Hello" into myIntMorpher's integer property
      |myIntMorpher.makeItSo(myStringMorpher)
      |```
      |
      |Rather than have it fail unexpectedly at runtime, we'd prefer to get the warning from the Scala compiler.
      |
      |## Retention and Keep
      |
      |The first example in the page was defined as a case class, so that it would equal another component of the same type.
      |
      |Although that is convenient, it might feel a bit clunky. Is a component with one name really *equal* to another component with a different name?
      |
      |If we want to work more cleanly, we can instead change our component's *retention strategy* by mixing in `Keep` or `Keyed`.
      |
      |* `Keep` will default to retaining the component if the `Keep` values match - but some reconcilers might replace it anyway for efficiency
      |* `Keyed` will retain the component if the keys match - and reconcilers will go out of their way to retain the item.
      |
      |Generally, use `Keyed` for components that are expensive to replace, like videos, and `Keep` otherwise.
      |
      |for example
      |
      |```
      |// This component would be morphed for a different name, but replaced for a different greeting
      |class MyKeepComponent(greeting:String)(initialName:String) extends DHtmlComponent
      |  with Keep(greeting) with Morphing(initialName) {
      |    val morpher = createMorpher(this)
      |  
      |    def render =
      |      val name = prop 
      |      <.p(s"$greeting $name")
      |  )
      |}
      |```
      |
      |
      |
      |""".stripMargin)
)