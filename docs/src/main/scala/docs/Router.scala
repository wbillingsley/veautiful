package docs


import com.wbillingsley.veautiful.html.{HTML, <, ^}
import HTML.*
import org.scalajs.dom


def routing = <.div(Common.markdown(
    """|# Routing
       |
       |A basic router trait, `HistoryRouter`, is included in the html package. Its intent is to be the smallest reasonable router, using the history API. 
       |
       |The code isn't sophisticated, but there shouldn't be too many surprises. Other routers (e.g. in Doctacular) can extend from it.
       |
       |Implementing it involves the steps of:
       |
       |* Picking a type to route on (i.e. defining your hierarchy of routes)
       |* Defining a method to turn a route into a path string
       |* Defining a method to get the current route from the current location
       |* Defining what to render for each route
       |
       |A trivial example:
       |
       |```scala
       |import com.wbillingsley.veautiful.html.*
       |
       |enum Route:
       |  case Home
       |  case Page(s:String)
       |
       |object Router extends HistoryRouter[Route] {
       |
       |  override def path(r:Route) = r match 
       |    case Home => "#/"
       |    case Page(s) => s"#/$s"
       |
       |  override def routeFromLocation() = 
       |    // hashPathList() takes the hash string and splits it into an ordinary list of strings on '/' characters
       |    // nothing fancy, but easy to understand what it's doing.
       |    // e.g. "example.com#/page/toys" becomes List(page, toys)
       |    PathDSL.hashPathList() match {
       |      case "page" :: s :: Nil => Route.Page(s)
       |      case _ => Route.Home
       |    }
       |    
       |  override def render = this.route match 
       |    case Route.Home => <.p("The home route")
       |    case Route.Page(s) => <.div(pages.lookup(s))
       |
       |}
       |```
       |
       |### Installing the router
       |
       |The router is a VNode, so we can just mount it:
       |
       |```scala
       |mountToBody(Router)
       |```
       |
       |### Changing the route
       |
       |`HistoryRouter` registers itself to listen to the window history API, so it will react to links (often hash fragment changes)
       |
       |To change the route from code:
       |
       |* Router.routeTo(route) if you want a new entry in the history to enable the back button
       |* Router.silentlyRouteTo(route) if you want it to use replaceState to replace the current entry in the history
       |
       |""".stripMargin
))