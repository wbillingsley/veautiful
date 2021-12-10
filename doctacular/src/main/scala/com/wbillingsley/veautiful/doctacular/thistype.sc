import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.{<, VHtmlComponent}

object Foo {

  class MyAnyMorpher[T]()(init: T) extends VHtmlComponent with Morphing(init) {

    val morpher = createMorpher(this)

    def render = <.div()

  }


  val a = MyAnyMorpher()(1)


}
































