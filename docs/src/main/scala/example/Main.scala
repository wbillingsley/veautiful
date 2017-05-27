package example

import com.wbillingsley.veautiful.{<, Attacher, DElement, ^}
import org.scalajs.dom

import scala.scalajs.js

object Main extends js.JSApp {

  override def main(): Unit = {
    val root = Attacher.newRoot(dom.document.getElementById("render-here"))
    root.render(Router)
  }

}
