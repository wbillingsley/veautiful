package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.{MakeItSo, Update}
import org.scalajs.dom.{Element, Node}

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
  case class Fixed(data:String, element:DElement[Element] = <.div(^.cls := "markup-node")) extends VHtmlNode {

    private var _domNode: Option[Element] = None

    /** The dom node that this is currently attached to. */
    override def domNode: Option[Node] = _domNode

    /**
      * Called to perform an attach operation -- ie, create the real DOM node and put it into
      * domNode
      */
    override def attach(): Node = {
      val e = element.create()
      _domNode = Some(e)
      e
    }

    override def afterAttach(): Unit = {
      super.afterAttach()
      for {e <- _domNode} {
        try {
          e.innerHTML = transform(data)
        } catch {
          case x: Throwable =>
            x.printStackTrace()
            e.innerHTML = "ERROR: " + x.getMessage
        }
      }
    }

    /**
      * Called to perform a detach operation -- ie, anything necessary to clean up the DOM node,
      * and then remove it from domNode so we know it's gone.
      */
    override def detach(): Unit = {
      // do nothing
    }

  }

  /**
    * A Settable MarkupNode is equal to any other Settable MarkupNode of the same generator. It takes its data as a
    * var, and "MakeItSo" will update it if the data has changed.
    * @param data
    */
  case class Settable()(var data:String) extends VHtmlNode with MakeItSo {

    private var _domNode: Option[Element] = None

    /** The dom node that this is currently attached to. */
    override def domNode: Option[Node] = _domNode

    /**
      * Called to perform an attach operation -- ie, create the real DOM node and put it into
      * domNode
      */
    override def attach(): Node = {
      val e = <.div(^.cls := "markup-node").create()
      _domNode = Some(e)
      e
    }

    override def afterAttach(): Unit = {
      super.afterAttach()
      renderMarkup()
    }

    private def renderMarkup():Unit  = {
      for {e <- _domNode} {
        try {
          e.innerHTML = transform(data)
        } catch {
          case x: Throwable =>
            x.printStackTrace()
            e.innerHTML = "ERROR: " + x.getMessage
        }
      }
    }

    /**
      * Called to perform a detach operation -- ie, anything necessary to clean up the DOM node,
      * and then remove it from domNode so we know it's gone.
      */
    override def detach(): Unit = {
      // do nothing
    }

    override def makeItSo = { case other:Settable =>
      if (data != other.data) {
        data = other.data
        renderMarkup()
      }
    }
  }

  /**
    * An updatable markup node is equal if it uses the same function to generate the data. Note, this will often
    * return false because anonymous functions in JavaScript are not equal to each other even if they are the same.
    * @param data
    */
  case class Updatable()(data: () => String) extends VHtmlNode with Update {

    private var _domNode: Option[Element] = None
    private var lastData: Option[String] = None

    /**
      * The dom node that this is currently attached to.
      *
      * Note that if a VNode uses more than one real node to implement itself, parent.get.domNode.get might not be
      * the same as domNode.get.getParent(), even if the gets were to succeed.
      */
    override def domNode: Option[Node] = _domNode

    /**
      * Called to perform an attach operation -- ie, create the real DOM node and put it into
      * domNode
      */
    override def attach(): Node = {
      val e = <.div(^.cls := "markup-node").create()
      _domNode = Some(e)
      e
    }

    override def afterAttach(): Unit = {
      super.afterAttach()
      update()
    }

    /**
      * Called to perform a detach operation -- ie, anything necessary to clean up the DOM node,
      * and then remove it from domNode so we know it's gone.
      */
    override def detach(): Unit = {
      // Forget the data that we contained. 
      // This fixes a bug where if we removed and then re-attached an Updatable, the innerHTML
      // would be gone, but would not be regenerated on re-attach because the data hadn't changed.
      lastData = None
    }

    override def update(): Unit = {
      for {e <- _domNode} {
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

