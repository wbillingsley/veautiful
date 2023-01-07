package docs.doctacular

import com.wbillingsley.veautiful.html.{<, ^, unique}
import com.wbillingsley.veautiful.doctacular._
import docs.Common.markdown

def otherContent = <.div(
  markdown("""# Other kinds of content
    | 
    |Just as VSlides aren't the only slides and YouTube isn't the only video, `Challenge`s might not be the only kind of tutorial content you want.
    |
    |Doctacular provides a way to add any kind of content and connect it into the page heirarchy. The class you implement depends on the URL scheme you
    |are using. 
    |
    |If your path can be represented as a list of strings, e.g. `tutorials/my-tutorial/1/3`, then the class to implement to add a new content type is 
    |`ListPathPlayer`. Below is the player used for challenges:
    |
    |```scala
    |given ListPathPlayer[Seq[Challenge.Level]] with
    |    extension (levels:Seq[Challenge.Level]) {
    |      // This string appears in the URL. E.g. challenges/my-challenge/3/2
    |      def kind = "challenges"
    |
    |      // When the content is added to the site, what subpath should the link go to?
    |      // e.g. in this case, we want to go to level 0, stage 0
    |      def defaultSubpath = List("0", "0")
    |
    |      // The method to implement to render this kind of content
    |      def view(name:String, subpath:List[String]) = {
    |
    |        // This bit's all about Challenges themselves - probably ignore for now
    |        val c = Challenge(
    |          levels = levels,
    |          homePath = (_:Challenge) => router.path(HomeRoute),
    |          levelPath = (c:Challenge, l:Int) => router.path(ListPathRoute("challenges", name, List(l.toString))),
    |          stagePath = (c:Challenge, l:Int, s:Int) => router.path(ListPathRoute("challenges", name, List(l.toString, s.toString))),
    |        )
    |
    |        // Given the subpath in your content, return some VHtmlContent to show
    |        subpath match {
    |          case Nil => c.show(0, 0)  // challenge/my-challenge goes to Level 0, Stage 0
    |          case l :: Nil => c.show(l.toInt, 0)   // challenge/my-challenge/3 goes to Level 3, Stage 0
    |          case l :: s :: _ => c.show(l.toInt, s.toInt)  // challenge/my-challenge/3/2 goes to Level 3, Stage 2
    |        }
    |      }
    |
    |    }
    |```
    |
    |## Adding the content to the site
    |
    |This should seem familiar from Decks, Videos, Pages, etc
    |
    |```scala
    |site.toc = site.Toc(
    |  // Preceeding routes, etc
    |
    |  "My Special Content" -> addOther("my-content", myContent)
    |)
    |```
    |""".stripMargin
  )
)