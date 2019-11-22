package example

import com.wbillingsley.veautiful.templates.HistoryRouter
import com.wbillingsley.veautiful.{<, ElementComponent, PathDSL}

sealed trait ExampleRoute
case object IntroRoute extends ExampleRoute
case object ToDoRoute extends ExampleRoute
case object ReactLikeRoute extends ExampleRoute
case object DiffusionRoute extends ExampleRoute
case class VSlidesRoute(i:Int) extends ExampleRoute
case object ScatterRoute extends ExampleRoute
case object WrenRoute extends ExampleRoute
case object AssemblyRoute extends ExampleRoute

object Router extends HistoryRouter[ExampleRoute] {

  var route:ExampleRoute = IntroRoute

  def rerender() = renderElements(render())

  def render() = {
    route match {
      case IntroRoute => Intro.page
      case ToDoRoute => ToDoList.page
      case ReactLikeRoute => ReactLike.page
      case DiffusionRoute => Common.layout(Diffusion.SimulationView)
      case VSlidesRoute(i) => VSlidesExample.page(i)
      case ScatterRoute => ScatterExample.page
      case WrenRoute => WrenExample.page
      case AssemblyRoute => AssemblyExample.page
    }
  }

  override def path(route: ExampleRoute): String = {
    import PathDSL._

    route match {
      case IntroRoute => (/# / "").stringify
      case ToDoRoute => (/# / "todo").stringify
      case ReactLikeRoute => (/# / "reactLike").stringify
      case DiffusionRoute => (/# / "diffusion").stringify
      case VSlidesRoute(i) =>
        println((/# / "vslides" / i.toString).stringify)
        (/# / "vslides" / i.toString).stringify
      case ScatterRoute => (/# / "scatter").stringify
      case WrenRoute => (/# / "wren").stringify
      case AssemblyRoute => (/# / "assembly").stringify
    }
  }

  def parseInt(s:String, or:Int):Int = {
    try {
      s.toInt
    } catch {
      case n:NumberFormatException => or
    }
  }

  override def routeFromLocation(): ExampleRoute = PathDSL.hashPathArray() match {
    case Array("") => IntroRoute
    case Array("todo") => ToDoRoute
    case Array("reactLike") => ReactLikeRoute
    case Array("diffusion") => DiffusionRoute
    case Array("vslides") => VSlidesRoute(0)
    case Array("vslides", i) => VSlidesRoute(parseInt(i, 0))
    case Array("scatter") => ScatterRoute
    case Array("wren") => WrenRoute
    case Array("assembly") => AssemblyRoute
    case x =>
      println(s"path was ${x}")
      IntroRoute


  }

}
