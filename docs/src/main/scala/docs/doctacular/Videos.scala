package docs.doctacular

import com.wbillingsley.veautiful.html.{<, ^, unique}
import com.wbillingsley.veautiful.doctacular._
import docs.Common.markdown

case class YouTube(id:String)

given VideoPlayer[YouTube] with 
  extension (v:YouTube) def embeddedPlayer(width:Int, height:Int) =
    <.div(
      <.iframe(
        ^.attr("width") := width, ^.attr("height") := height, ^.src := s"https://www.youtube.com/embed/${v.id}",
        ^.attr("frameborder") := "0", ^.attr("allow") :="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture",
        ^.attr("allowfullscreen") := "allowfullscreen")
    )

val videoIntro = unique(<.div(
  markdown("""# Videos
    | 
    |There are many different places that a video could come from that you want to embed in a page or show as its own
    |page. Files, YouTube, Vimeo, your own cloud video system, etc.
    |
    |To try to keep it relatively simple, but cater for sites that might have unique video systems, we say that a 
    |video is any data type for which a video player is given.
    |
    |So, for instance in this page, we have declared our own class for YouTube:
    |
    |```scala
    |case class YouTube(id:String)
    |```
    |
    |It's just a class. No methods or anything.
    |
    |And then all we need is a player for YouTube videos in scope when we add the video. That will often come from a 
    |library, but they're also very small to directly define yourself. Here's one for YouTube:
    | 
    |```scala
    |given VideoPlayer[YouTube] with 
    |  extension (v:YouTube) def embeddedPlayer(width:Int, height:Int) =
    |    <.div(
    |      <.iframe(
    |        ^.attr("width") := width, ^.attr("height") := height, ^.src := s"https://www.youtube.com/embed/${v.id}",
    |        ^.attr("frameborder") := "0", ^.attr("allow") :="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture",
    |        ^.attr("allowfullscreen") := "allowfullscreen")
    |    )
    |```
    |
    |Part of the point of doing it this way is that by defining the video class separately from the player, we can import
    |different players if we want to.
    |
    |Adding a video to a page, however, is then just
    |
    |```scala
    |site.addVideo("myVideo", YouTube(youTubeVideoId))
    |```
    |
    |Or you can embed a player using Veautful with 
    |
    |```scala
    |YouTube(youTubeVideoId).embeddedPlayer(width, height)
    |```
    |
    |For example, let's embed Blender's open source video *Big Buck Bunny*:
    |
    |```
    |YouTube("YE7VzlLtp-4").embeddedPlayer(720, 480)
    |```
    |""".stripMargin),
  YouTube("YE7VzlLtp-4").embeddedPlayer(720, 480)
))