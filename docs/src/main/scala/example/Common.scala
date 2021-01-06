package example

import com.wbillingsley.veautiful.html.{<, Markup, VHtmlNode, ^}

import scala.scalajs.js

/**
  * Common UI components to all the views
  */
object Common {

  val markdownGenerator = new Markup({ (s:String) => js.Dynamic.global.marked(s).asInstanceOf[String] })

  def markdown(s:String):VHtmlNode = markdownGenerator.Fixed(s)


}
