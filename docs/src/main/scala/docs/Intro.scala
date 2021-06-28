package docs

import java.nio.DoubleBuffer

import com.wbillingsley.veautiful.{DiffNode, MutableArrayComponent}
import com.wbillingsley.veautiful.html.{<, EventMethods, SVG, VHtmlComponent, VHtmlNode, ^, Styling}
import org.scalajs.dom
import org.scalajs.dom.{Element, MouseEvent, Node, svg}
import org.scalajs.dom.html.Canvas

def intro = <.div(
  <.p(^.cls := "logo",
    Common.logoWithTitle(200, 200),
    <.p(^.cls := "logo-slogan", "A devastatingly simple Scala.js front end")
  ),
  Common.markdown(
    """
      |Veautiful is a simple but effective web front end for Scala.js, written by [Will Billingsley](https://www.wbillingsley.com).
      |I wanted something flexible enough to write explorable explanations (teaching materials with embedded models and simulations)
      |but that would feel simpler to use than common frameworks such as React.
      |
      |As I have also written university courses in Scala and Advanced Web Programming, I also wanted its concepts to be
      |straightforward enough to teach to undergraduate students in less than a week.
      |
      |### Veautifully Built
      |
      |To see examples of the sorts of things that have been built with it, try one of the online course sites that have
      |various kinds of embedded models:
      |
      |* [Thinking About Programming](https://theintelligentbook.com/thinkingaboutprogramming) - embedded robot simulations
      |  in a little course that teaches computational thinking.
      |* [Circuits Up](https://theintelligentbook.com/circuitsup) - embedded circuit simulations in a little course
      |  that teaches computer architecture from electronics up.
      |* [The Adventures of Will Scala](https://theintelligentbook.com/willscala) - a simpler site (mostly video
      |  and text) that goes alongside my undergraduate Scala course.
      |* [The Coding Escape](http://theintelligentbook.com/fos1_codingescape/) - an hour-of-code style outreach
      |  exercise that includes a blocks programming language written in Veautiful
      |
      |### What's unique about Veautiful?
      |
      |Most frameworks make you choose between a Virtual DOM and direct updates. Veautiful lets your *components*
      |choose. In practice, this makes writing UIs that can have some very complex behaviours simpler and clearer.
      |""".stripMargin
  ),
  
)

