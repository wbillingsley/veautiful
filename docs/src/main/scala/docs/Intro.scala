package docs

import java.nio.DoubleBuffer

import com.wbillingsley.veautiful.{DiffNode, MutableArrayComponent}
import com.wbillingsley.veautiful.html.{<, EventMethods, SVG, VDomNode, ^, Styling}
import org.scalajs.dom
import org.scalajs.dom.{Element, MouseEvent, Node, svg}
import org.scalajs.dom.html.Canvas

val logoStyling = Styling(
  """|margin-top: 50px;
     |text-align: center;
     |border-bottom: 1px solid #6c757d;
     |margin-left: auto;
     |margin-right: auto;
     |""".stripMargin
).modifiedBy(
  " img" -> "max-height: 150px;",
  " .logo-text" -> 
    """|font-variant-caps: all-petite-caps;
       |font-family: "Times New Roman", serif;
       |font-size: 32px;
       |font-variant: all-small-caps;
       |color: #004479;
       |font-weight: 600;
       |margin-top: -15px;
       |""".stripMargin,
  " .logo-slogan" -> 
    """|font-family: "Times New Roman", serif;
       |font-style: italic;
       |font-weight: 200;
       |font-size: 24px;
       |margin-top: -10px;
       |""".stripMargin
).register()

def intro = <.div(
  <.p(^.cls := logoStyling,
    Common.logoWithTitle(200, 200),
    <.p(^.cls := "logo-slogan", "A devastatingly simple Scala.js front end.")
  ),
  Common.markdown(
    s"""
      |Veautiful is a web front end for Scala.js, written by [Will Billingsley](https://www.wbillingsley.com).
      |It combines low level control with high level ease of use. 
      |
      |I wanted something flexible enough to write explorable explanations (teaching materials with embedded models and simulations)
      |but that would feel simpler to use than common frameworks such as React. 
      |As I have also written university courses in Scala and Advanced Web Programming, I also wanted its concepts to be
      |straightforward enough to teach to undergraduate students in less than a week. It's origin story is that in 2017 I got bored of
      |teaching *how to use* front end toolkits and decided to try creating one small enough I could show how it was built. The
      |first version was only around 400 lines of code. It's grown, but I try to keep it as simple as possible, while still letting me
      |be expressive writing explorable explanations and interactive materials.
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
      |* [Supercollaborative](https://theintelligentbook.com/supercollaborative) - for a software studio course, includes
      |  a git simulation built into the decks in the version control week.
      |* [The Coding Escape](http://theintelligentbook.com/fos1_codingescape/) - an hour-of-code style outreach
      |  exercise that includes a blocks programming language written in Veautiful.
      |
      |# Getting it
      |
      |Veautiful (the core UI toolkit), veautiful-templates (a collection of useful stuff like a router and slide decks), and 
      |doctacular (a system for publishing docs or teaching sites) are published to Maven Central and listed on 
      |[Scaladex](https://index.scala-lang.org/wbillingsley/veautiful).
      |
      |```
      |"com.wbillingsley" %%% "veautiful" % "$latestVersion" // just the core toolkit, latest milestone
      |```
      |
      |or
      |
      |```
      |"com.wbillingsley" %%% "doctacular" % "$latestVersion" // also includes some components and the site system, latest milestone
      |```
      |
      |The sourcecode is published to [GitHub](https://github.com/wbillingsley/veautiful) using the MIT Licence.
      |
      |Scaladoc APIs can be found on javadoc.io:
      |
      |* [Veautiful API](https://javadoc.io/doc/com.wbillingsley/veautiful_sjs1_3)
      |* [Doctacular API](https://javadoc.io/doc/com.wbillingsley/doctacular_sjs1_3/latest/index.html)
      |
      |### What's unique about Veautiful?
      |
      |Scala is a mixed paradigm language that permits imperative code *and* makes functional code pleasent to write. 
      |Veautiful is a mixed paradigm framework that permits low-level imperative manipulation of elements *and* makes declarative UIs pleasant to write. 
      |
      |The toolkit starts from some very small low-level primitives, but quickly provides classes that give you the smooth
      |declarative style of UI writing you might be looking for. 
      |
      |e.g.:
      |
      |* Declare your overall page in a declarative style... 
      |* ... but one of your components works a bit more like a d3.js data animation ...
      |* ... and another wraps a plain old DOM element ...
      |* ... and they all need to play together nicely.
      |
      |Essentially, this was the challenge I faced writing interactive teaching materials. Every page of a tutorial could contain
      |interactive widgets that needed to work differently because they *were different*. From simulations of a thousand molecules,
      |to programmable maze games, to circuits simulating the voltages in wires in a register of flip-flops, to plain old 
      |forms and pages. So that's what my toolkit tries to make easy to do.
      |
      |""".stripMargin
  ),
  
)

