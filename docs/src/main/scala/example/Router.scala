package example

import com.wbillingsley.veautiful.templates.HistoryRouter
import com.wbillingsley.veautiful.{<, ElementComponent, PathDSL}

sealed trait ExampleRoute
case object IntroRoute extends ExampleRoute
case object ToDoRoute extends ExampleRoute
case object ReactLikeRoute extends ExampleRoute
case object DiffusionRoute extends ExampleRoute
case object VSlidesRoute extends ExampleRoute

object Router extends HistoryRouter[ExampleRoute] {

  var route:ExampleRoute = IntroRoute

  def rerender() = renderElements(render())

  def render() = {
    route match {
      case IntroRoute => Intro.page
      case ToDoRoute => ToDoList.page
      case ReactLikeRoute => ReactLike.page
      case DiffusionRoute => Common.layout(Diffusion.SimulationView)
      case VSlidesRoute => VSlidesExample.page
    }
  }

  override def path(route: ExampleRoute): String = {
    import PathDSL._

    route match {
      case IntroRoute => (/# / "").stringify
      case ToDoRoute => (/# / "todo").stringify
      case ReactLikeRoute => (/# / "reactLike").stringify
      case DiffusionRoute => (/# / "diffusion").stringify
      case VSlidesRoute => (/# / "vslides").stringify
    }
  }

  override def routeFromLocation(): ExampleRoute = PathDSL.hashPathArray() match {
    case Array("") => IntroRoute
    case Array("todo") => ToDoRoute
    case Array("reactLike") => ReactLikeRoute
    case Array("diffusion") => DiffusionRoute
    case Array("vslides") => VSlidesRoute
    case x =>
      println(s"path was ${x}")
      IntroRoute


  }

}
