package example

import com.wbillingsley.veautiful.{<, VNode, ^}

/**
  * Common UI components to all the views
  */
object Common {

  def linkToRoute(r:Router.ExampleRoute, s:String):VNode = <.a(
    ^.href := "#",
    ^.cls := (if (Router.route == r) "nav-link active" else "nav-link"),
    ^.onClick --> Router.routeTo(r),
    s
  )

  def leftMenu:VNode = <.div(
    <.h3("Veautiful"),
    <.ul(^.cls := "nav nav-pills flex-column",
      <.li(
        ^.cls := "nav-item",
        linkToRoute(Router.IntroRoute, "Hello world")
      ),
      <.li(
        ^.cls := "nav-item",
        linkToRoute(Router.ReactLikeRoute, "React-like rendering, into an SVG")
      )
    )
  )

  def layout(ch:VNode) = <.div(
    ^.cls := "row",
    <.div(^.cls := "col-sm-4 sidebar", leftMenu),
    <.div(^.cls := "col-sm-8", ch)
  )


}
