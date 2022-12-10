package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.{MakeItSo, Update, Decorator, Morphing}
import org.scalajs.dom.html.Element

import scala.scalajs.js

/**
  * Nodes for markup languages such as Markdown.
  *
  * @param transform The function that will transform the markup into HTML
  */
class Markup(transform:(String) => String) {

  def div(data:String):Fixed = Fixed(data, <.div(^.cls := "markup-node"))

  def span(data:String):Fixed = Fixed(data, <.span(^.cls := "markup-node"))
  
  /**
    * A Fixed MarkupNode is only equal if its data is equal. In most uses, this means that if the data has changed
    * the component will be replaced.
    * @param data
    */
  case class Fixed(data:String, bp:DElementBlueprint[Element] = <.div(^.cls := "markup-node")) extends Decorator(bp.build()) {

    override def afterAttach(): Unit = {
      super.afterAttach()
      for {e <- domNode} {
        try {
          e.innerHTML = transform(data)
        } catch {
          case x: Throwable =>
            x.printStackTrace()
            e.innerHTML = "ERROR: " + x.getMessage
        }
      }
    }

  }

  /**
    * A Settable MarkupNode is equal to any other Settable MarkupNode of the same generator. It takes its data as a
    * var, and "MakeItSo" will update it if the data has changed.
    * @param data
    */
  case class Settable()(data:String) extends VHtmlComponent with Morphing(data) {
    override val morpher = createMorpher(this)
    def render = <.div(^.cls := "markup-node")(transform(prop))
  }

  /**
    * An updatable markup node is equal if it uses the same function to generate the data. Note, this will often
    * return false because anonymous functions in JavaScript are not equal to each other even if they are the same.
    * @param data
    */
  case class Updatable()(data: () => String) extends Decorator(<.div(^.cls := "markup-node").build()) with Update {

    private var lastData: Option[String] = None

    override def afterAttach(): Unit = {
      super.afterAttach()
      update()
    }

    /**
      * Called to perform a detach operation -- ie, anything necessary to clean up the DOM node,
      * and then remove it from domNode so we know it's gone.
      */
    override def afterDetach(): Unit = {
      // Forget the data that we contained. 
      // This fixes a bug where if we removed and then re-attached an Updatable, the innerHTML
      // would be gone, but would not be regenerated on re-attach because the data hadn't changed.
      lastData = None
    }

    override def update(): Unit = {
      for {e <- domNode} {
        try {
          val d = data()
          if (!lastData.contains(d)) {
            e.innerHTML = transform(d)
            lastData = Some(d)
          }
        } catch {
          case x: Throwable =>
            x.printStackTrace()
            e.innerHTML = "ERROR: " + x.getMessage
        }
      }
    }
  }

}

