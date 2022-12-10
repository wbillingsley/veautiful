package docs

import com.wbillingsley.veautiful.html.{<, EventMethods, DHtmlComponent, VDomNode, ^, VHTMLElement}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLInputElement

import scala.collection.mutable

object ToDoList {

  /**
    * In this example, let's implement the whole list as a mutable component. In larger applications, we'd want
    * the data storage to be outside the UI components, but this is just a little to-do list.
    */
  object MyToDoList extends DHtmlComponent {

    /** To-do items are data */
    case class ToDoItem(val s:String, var done:Boolean)

    /** We can hold the data model in anything we want */
    private val toDo:mutable.Buffer[ToDoItem] = mutable.Buffer(
      new ToDoItem("Add more to-do items", false)
    )

    /** called to add the item to the model */
    def addItem(i:ToDoItem):Unit = {
      toDo.append(i)
      rerender()
    }

    def remove(i:Int):Unit = {
      toDo.remove(i)
      rerender()
    }

    /** Let's define a component for adding entries into the list */
    case class AddItem() extends DHtmlComponent {

      /* holds the text we're typing in, React-like updated on each keypress */
      private var adding:Option[String] = None

      override def render = <.div(^.cls := "input-group",
        <.input(^.attr("type") := "text", ^.cls := "form-control",
          ^.attr("placeholder") := "type item here",
          ^.prop("value") := adding.getOrElse(""),
          ^.on("change") ==> { e => adding = e.inputValue }
        ),
        <.span(^.cls := "input-group-btn",
          <.button(^.cls := "btn btn-primary", "Add",
            ^.onClick --> {
              for { t <- adding } {
                adding = None
                addItem(new ToDoItem(t, false))
              }
            }
          )
        )
      )
    }

    /** define how the ToDoList renders */
    override def render = {
      <.div(^.cls := "card",
        <.div(^.cls := "card-body",
          <.h5(^.cls := "card-title", "To Do list"),
          <.p(^.cls := "card-text", "This is a simple to-do list rendered as a Bootstrap card")
        ),
        <.ul(^.cls := "list-group list-group-flush todo-list",
          for {
            (item, idx) <- toDo.zipWithIndex
          } yield {
            if (item.done) {
              <.li(^.cls := "list-group-item item-done",
                <.input(^.attr("type") := "checkbox", ^.prop("checked") := "checked", ^.onClick --> { item.done = false; rerender() }),
                item.s,
                <.button(^.cls := "btn btn-sm btn-secondary float-right", ^.onClick --> remove(idx), <("i")(^.cls := "material-icons", "delete"))
              )
            } else {
              <.li(^.cls := "list-group-item",
                <.input(^.attr("type") := "checkbox", ^.cls := "input-control", ^.onClick --> { item.done = true; rerender() }),
                item.s,
                <.button(^.cls := "btn btn-sm btn-secondary float-right", ^.onClick --> remove(idx), <("i")(^.cls := "material-icons", "delete"))
              )
            }
          }
        ),
        <.div(^.cls := "card-footer", AddItem())
      )
    }
  }


  def page = <.div(
    Common.markdown(
      """
        |# Example: To Do List
        |
        |As a To Do List is a traditional example, here's a very simple one, that works
        |almost exactly like a React one would. Somewhere there's a list of to do list items,
        |we add to it with a form, and every time we do, the whole thing re-renders.
        |""".stripMargin
    ),
    MyToDoList,
    <.p(),
    <.div(
      Common.markdown(
        """
          |```scala
          |object MyToDoList extends DHtmlComponent {
          |
          |  /** To-do items are data */
          |  case class ToDoItem(val s:String, var done:Boolean)
          |
          |  /** We can hold the data model in anything we want */
          |  private val toDo:mutable.Buffer[ToDoItem] = mutable.Buffer(
          |    new ToDoItem("Add more to-do items", false)
          |  )
          |
          |  /** called to add the item to the model */
          |  def addItem(i:ToDoItem):Unit = {
          |    toDo.append(i)
          |    rerender()
          |  }
          |
          |  def remove(i:Int):Unit = {
          |    toDo.remove(i)
          |    rerender()
          |  }
          |
          |  /** Let's define a component for adding entries into the list */
          |  case class AddItem() extends DHtmlComponent {
          |
          |    /* holds the text we're typing in, React-like updated on each keypress */
          |    private var adding:Option[String] = None
          |
          |    override def render:VHTMLElement = <.div(^.cls := "input-group",
          |      <.input(^.attr("type") := "text", ^.cls := "form-control",
          |        ^.attr("placeholder") := "type item here",
          |        ^.prop("value") := adding.getOrElse(""),
          |        ^.on("change") ==> { e => adding = e.inputValue }
          |      ),
          |      <.span(^.cls := "input-group-btn",
          |        <.button(^.cls := "btn btn-primary", "Add",
          |          ^.onClick --> {
          |            for { t <- adding } {
          |              adding = None
          |              addItem(new ToDoItem(t, false))
          |            }
          |          }
          |        )
          |      )
          |    )
          |  }
          |
          |  /** define how the ToDoList renders */
          |  override def render = {
          |    <.div(^.cls := "card",
          |      <.div(^.cls := "card-body",
          |        <.h5(^.cls := "card-title", "To Do list"),
          |        <.p(^.cls := "card-text", "This is a simple to-do list rendered as a Bootstrap card")
          |      ),
          |      <.ul(^.cls := "list-group list-group-flush todo-list",
          |        for {
          |          (item, idx) <- toDo.zipWithIndex
          |        } yield {
          |          if (item.done) {
          |            <.li(^.cls := "list-group-item item-done",
          |              <.input(^.attr("type") := "checkbox", ^.prop("checked") := "checked",
          |                ^.onClick --> { item.done = false; rerender() }
          |              ),
          |              item.s,
          |              <.button(^.cls := "btn btn-sm btn-secondary float-right", ^.onClick --> remove(idx),
          |                <("i")(^.cls := "material-icons", "delete")
          |              )
          |            )
          |          } else {
          |            <.li(^.cls := "list-group-item",
          |              <.input(^.attr("type") := "checkbox", ^.cls := "input-control",
          |                ^.onClick --> { item.done = true; rerender() }
          |              ),
          |              item.s,
          |              <.button(^.cls := "btn btn-sm btn-secondary float-right", ^.onClick --> remove(idx),
          |                <("i")(^.cls := "material-icons", "delete")
          |              )
          |            )
          |          }
          |        }
          |      ),
          |      <.div(^.cls := "card-footer", AddItem())
          |    )
          |  }
          |}
          |```
          |""".stripMargin)
    )
  )

}
