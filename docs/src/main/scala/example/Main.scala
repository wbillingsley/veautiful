package example

import com.wbillingsley.veautiful.html.Attacher
import com.wbillingsley.veautiful.doctacular.Site

import org.scalajs.dom





object Main {

  def main(args:Array[String]): Unit = {
    val site = Site()
    
    site.toc = site.Toc(
      site.TocNodeLink(Common.logoWithTitle(150, 150), site.HomeRoute),
      site.TocLine,
      "Examples" -> site.Toc(
        "To-Do List" -> site.addPage("to-do-list", ToDoList.page),
        "Orbiting asteroids" -> site.addPage("orbiting-asteroids", ReactLike.page),
        "Diffusion" -> site.addPage("diffusion-experiment", Diffusion.SimulationView),
        "VSlides" -> site.addPage("vslides", VSlidesExample.page(0)),
        "Scatter" -> site.addPage("scatter", ScatterExample.page)
      )
    )
    
    println(site.toc.entries.toList)



    site.home = () => {
      
      println(s"Toc is ${site.toc.entries.toList}")
      
      site.renderPage(Intro.page)
    }
    
    site.attachTo(dom.document.getElementById("render-here"))
  }

}
