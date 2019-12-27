package example

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement

import scala.collection.mutable

object ToDoList {

  /* our model for the to-do list */
  class ToDoItem(val s:String, var done:Boolean)
  val toDo:mutable.Buffer[ToDoItem] = mutable.Buffer(
    new ToDoItem("Add more to-do items", false)
  )

  /* holds the text we're typing in, React-like updated on each keypress */
  var adding:Option[String] = None

  /* an event-handler for typing in the input box */
  def updateAdding(e:dom.Event):Unit = {
    e.target match {
      case i:HTMLInputElement => adding = Some(i.value)
      case _ => // do nothing
    }
  }

  /* called to add the item to the model */
  def add(): Unit = {
    for { a <- adding } {
      toDo.append(new ToDoItem(a, false))
      adding = None

      // By calling re-render on the router, we do a top-level rerender (as in react)
      Router.rerender()
    }
  }

  /* called to remove items from the model */
  def done(i:Int):Unit = {
    toDo.remove(i)

    // By calling re-render on the router, we do a top-level rerender (as in react)
    Router.rerender()
  }

  def addItem(s:String) = {
    toDo.append(new ToDoItem(s, false))
    Router.rerender()
  }


  def page:VHtmlNode = Common.layout(<.div(
    <.h1("Example: To Do List"),
    <.p(
      """
        | As a To Do List is a traditional example, here's a very simple one, that works
        | almost exactly like a React one would. Somewhere there's a list of to do list items,
        | we add to it with a form, and every time we do, the whole thing re-renders.
      """.stripMargin),
    <.div(^.cls := "card",
      <.div(^.cls := "card-body",
        <("h5")(^.cls := "card-title", "To Do list"),
        <.p(^.cls := "card-text", "This is a simple to-do list rendered as a Bootstrap card")
      ),
      <.ul(^.cls := "list-group list-group-flush",
        for {
          (item, idx) <- toDo.zipWithIndex
        } yield {
          <.li(^.cls := "list-group-item",
            item.s,
            <.button(^.cls := "btn btn-sm btn-primary float-right",
              ^.onClick --> done(idx), "remove")
          )
        }
      ),
      <.div(^.cls := "card-footer",
        <.div(^.cls := "input-group",
          <.input(^.attr("type") := "text", ^.cls := "form-control",
            ^.attr("placeholder") := "type item here",
            ^.prop("value") := adding.getOrElse(""),
            ^.on("change") ==> updateAdding
          ),
          <.span(^.cls := "input-group-btn",
            <.button(^.cls := "btn btn-primary", "Add",
              ^.onClick --> add
            )
          )
        )
      )
    )

  ))

}
