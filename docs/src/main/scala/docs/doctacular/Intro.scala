package docs.doctacular

import com.wbillingsley.veautiful.html._
import org.scalajs.dom

import docs.Common._

val introPage = <.div(
  markdown(
    """ # Doctacular
      | 
      | Doctacular is a site generator based on Veautiful.
      |""".stripMargin)
)