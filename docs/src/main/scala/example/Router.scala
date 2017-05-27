package example

import com.wbillingsley.veautiful.{<, ElementComponent}

/**
  * Created by wbilling on 26/05/2017.
  */
object Router extends ElementComponent(<.div) {

  sealed trait ExampleRoute
  case object IntroRoute extends ExampleRoute
  case object ReactLikeRoute extends ExampleRoute

  var route:ExampleRoute = IntroRoute

  override def afterAttach() = {
    println("A router has been attached")
    rerender()
  }

  def routeTo(r:ExampleRoute) = {
    route = r
    rerender()
  }

  def rerender() = renderElements(
    route match {
      case IntroRoute => Intro.page
      case ReactLikeRoute => ReactLike.page
    }
  )

}
