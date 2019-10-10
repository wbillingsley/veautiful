package example

import com.wbillingsley.veautiful.templates.VSlides
import com.wbillingsley.veautiful.{<, ^}

object VSlidesExample {

  def page = Common.layout(<.div(
    <.h1("VSlides"),
    <.div(^.cls := "resizable",
      VSlides(width=1024, height=680)(
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
      )

    )

  ))

}
