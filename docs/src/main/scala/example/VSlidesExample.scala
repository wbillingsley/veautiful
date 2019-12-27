package example

import com.wbillingsley.veautiful.html.{<, ^}
import com.wbillingsley.veautiful.templates.VSlides

object VSlidesExample {

  def page(i:Int) = Common.layout(<.div(
    <.h1("VSlides"),
    <.div(^.cls := "resizable",
      VSlides(width=1280, height=720)(Seq(
        <.div(
          <.h2("Slide 1"),
          <.p("Here's some text on slide 1")
        ),
        <.div(
          <.h2("Slide 2"),
          <.p("Here's some text on slide 2")
        ),
        Diffusion.SimulationView,
        ScatterExample.scatterCanvas
      ), i

    ))

  ))

}
