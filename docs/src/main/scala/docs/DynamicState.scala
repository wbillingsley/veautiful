package docs


import com.wbillingsley.veautiful.{PushVariable, DynamicValue}
import com.wbillingsley.veautiful.html.{HTML, <, ^, Animator}
import HTML.*
import scalajs.js
import org.scalajs.dom

val time = for _ <- Animator.now yield (new js.Date()).toLocaleTimeString()

def dynamicState = <.div(Common.markdown(
  """# Dynamic state
    |
    |Veautiful tries to avoid directing you how to write your data store. There are a lot of very good frameworks for holding and 
    |streaming reactive data, and usually your best approach is to use one and write a little component that knows how to render that sort
    |of state.
    |
    |However, there is a little framework for dynamic bindings built in. It's efficient, easy, and does the job well, but has a very narrow scope.
    |
    |(It's a cut-down version of `Latch` from my "handy" library, that I've used for many years. But whereas Latch is designed always to work with
    |Futures, DynamicValue is synchronous.)
    |
    |### Push variables and pull values
    |
    |The very small concept I'd like to give you for how these work is *push* and *pull*
    |
    |* A `PushVariable` is a variable that we can set and it can trigger some operation when we set it. State variables in components are push values.
    |* A `DynamicValue`, or *pull* value, is a lazy value that we can read from that might change. 
    |
    |If you have a `PushValue`, you can get a dynamic value from it by calling `.dynamic` on it. This is a little like the relationship between a 
    |`Promise` and a `Future`, except this is all synchronous.
    |
    |However, you can also create dynamic variables that don't come from push variables. For example, `Animator.now` holds the value of 
    |`performance.now` as a dynamic variable, updated every animation frame when it is in use.
    |
    |Dynamic variables have `map` so you can derive other values from them, but very few other methods. (Mostly because I haven't needed many other methods
    |from them.) 
    |
    |### Dynamic HTML
    |
    |Normally, when we write our UI, we're describing the structure that we want our component to render at this moment:
    |
    |```
    |div(
    |  h1("My heading"),
    |  p("My content")
    |)
    |```
    |
    |However, the DSL also allows us to insert *dynamic* elements, that can also receive dynamic modifiers
    |
    |```scala
    |dynamic.div(
    |  h1("My heading", ^.style("color") <-- dynamicColorVal),
    |  dynamicContent
    |)
    |```
    |
    |Those dynamic elements in the rendering tree will listen to the dynamic modifiers, and update themselves when they change:
    |
    |* A dynamic property or attribute binding (using `<--`) will update itself immediately
    |* A dynamic variable containing content or other modifiers will trigger a request for an animation frame to update it.
    |
    |
    |For example, let's show the time. `Animator.now` has a dynamic value containing the result of performance.now; let's derive
    |a dynamic value of the current time in a readable format:
    |
    |```scala
    |// As it's a time value, let's just derive it from Animator.now
    |val time = for _ <- Animator.now yield (new js.Date()).toLocaleTimeString()
    |```
    |
    |We can now just throw our dynamic time variable into the children of a dynamic `p` element:
    |
    |```scala
    |div(
    |  dynamic.p(
    |    "The time now is ", time
    |  )
    |)
    |```
    |
    |
    |""".stripMargin),
  <.div(^.cls := embeddedExampleStyle.className,
    <.dynamic.p(
        "The time now is ", time
    )
  ),
  Common.markdown("""
    |
    |
    |### Why `dynamic.div`?
    |
    |This is a stylistic choice of mine. You could, if you wanted, just import the dynamic version of everything and have *all* your elements
    |be the dynamic versions
    |
    |```
    |import com.wbillingsley.veautiful.html.HTML.dynamic.*
    |```
    |
    |However, when I'm describing a component, I like to be more intentional and explicit about whether the tree it will render is 
    |dynamic (elements within it will re-render on their own) or whether the tree will only rerender if the component rerenders.
    |
    |```
    |div(
    |  p("Just some text")
    |  dynamic.p(dynamicContent) // This makes it easier for me to see what's going on
    |)
    |```
    |
    |Dynamic elements do have a little more machinery in them than regular DElements, as they have to remember their blueprints if they need to resync.
    |Although this is all fairly efficient, I think that it's more idiomatic to use the light version most of the time and dynamic
    |elements only when we're actually giving it a dynamic modifier.
    |
    |""".stripMargin)
)