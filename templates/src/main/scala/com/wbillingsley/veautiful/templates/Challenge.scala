package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.<.DElAppliable
import com.wbillingsley.veautiful.html.{<, DElement, VHtmlComponent, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.Challenge.{HomePath, LevelPath, StagePath}
import com.wbillingsley.veautiful.templates.Sequencer.LayoutFunc

/**
  * Layout based on the one that is used for Escape the Lava Maze
  */
object Challenge {

  def hgutter = <.div(^.cls := "row hgutter")

  def card(s:String)(ac: DElAppliable *) = <.div(^.cls := "card",
    <.div(^.cls := "card-body",
      <.div(^.cls := "card-title", <.h4(s)),
      <.div(ac:_*)
    )
  )

  def card(ac: DElAppliable *) = <.div(^.cls := "card",
    <.div(^.cls := "card-body",
      <.div(ac:_*)
    )
  )

  def cardText(ac: DElAppliable *) = <.div(^.cls := "card-text", <.div(ac:_*))

  def textColumn(ac: DElAppliable *) = <.div(^.cls := "text-column", <.div(ac:_*))

  def textAndEx(left: DElAppliable *)(right: DElAppliable *):VHtmlNode = {
    split(textColumn(left:_*))(right:_*)
  }

  def split(l:DElAppliable*)(r:DElAppliable*) = <.div(^.cls := "split2",
    <.div(l:_*),
    <.div(r:_*)
  )


  trait Stage extends VHtmlComponent {

    def completion:Completion

    def kind:String

  }

  case class VideoStage(yt:String) extends Stage {
    var completion = Open

    val kind = "video"

    def render = <.div(yt)
  }

  case class Level(name:String, stages:Seq[Stage])

  sealed trait Completion
  case object Open extends Completion
  case object Incomplete extends Completion
  case class Complete(mark:Option[Double], medal:Option[String]) extends Completion

  type HomePath = (Challenge) => String
  type LevelPath = (Challenge, Int) => String
  type StagePath = (Challenge, Int, Int) => String

  def defaultHeader(homePath:HomePath, homeIcon: => VHtmlNode) = { c:Challenge =>
    <.div(
      <.a(^.cls := "home-link", ^.href := homePath(c), <("i")(^.cls := "material-icons", "home")),
      <.span(^.cls := "challenge-name", c.levels(c.level).name)
    )
  }

  def defaultTopRight() = { c:Challenge => <.div() }

  def progressTile(level:Level, i:Int, levelPath: LevelPath, stagePath: StagePath) = { c:Challenge =>

    def levelActive = c.level == i

    def stageActive(j:Int) = c.level == i && c.stage == j

    <.div(^.cls := (if (levelActive) "progress-level level-active" else "progress-level"),
      <.div(^.cls := "level-name",
        <.a(^.href := levelPath(c, i), level.name)
      ),
      <.div(^.cls := "stage-links",
        for {
          (s, j) <- level.stages.zipWithIndex
        } yield {
          <.a(^.cls := (if (stageActive(j)) "stage-link stage-active" else "stage-link"), ^.href := stagePath(c, i, j),
            <("i")(^.cls := "material-icons",
              s.kind match {
                case "video" => "videocam"
                case _ => "lens"
              }
            )
          )
        }
      )
    )
  }

  def defaultProgressBlock(levels:Seq[Challenge.Level], levelPath: LevelPath, stagePath: StagePath) = { c:Challenge =>
    <.div(^.cls := "progress-block",
      for {
        (l, i) <- levels.zipWithIndex
      } yield progressTile(l, i, levelPath, stagePath)(c)
    )
  }

  def defaultPageControls(levels:Seq[Challenge.Level], levelPath: LevelPath, stagePath: StagePath) = { c:Challenge =>
    <.div(^.cls := "btn-group",
      for { (l, s) <- c.previous } yield <.a(^.cls := "btn btn-outline-secondary",
        ^.href := stagePath(c, l, s), s"Previous"
      ),
      for { (l, s) <- c.next } yield <.a(^.cls := "btn btn-outline-secondary",
        ^.href := stagePath(c, l, s), s"Next"
      )
    )
  }

  def apply(levels: Seq[Challenge.Level], homePath: HomePath, homeIcon: => VHtmlNode, levelPath: LevelPath, stagePath: StagePath) = {
    new Challenge(levels,
      defaultHeader(homePath, homeIcon),
      defaultTopRight(),
      defaultProgressBlock(levels, levelPath, stagePath),
      defaultPageControls(levels, levelPath, stagePath)
    )
  }
}

class Challenge(val levels: Seq[Challenge.Level],
                val header: (Challenge) => VHtmlNode,
                val tr: (Challenge) => VHtmlNode,
                val progressBlock: (Challenge) => VHtmlNode,
                val pageControls: (Challenge) => VHtmlNode
               ) extends VHtmlComponent {

  var level:Int = 0
  var stage:Int = 0

  def previous:Option[(Int, Int)] = {
    if (level <= 0 && stage <= 0) None else Some(
      if (stage > 0) (level, stage - 1) else (level - 1, levels(level).stages.length - 1)
    )
  }

  def next:Option[(Int, Int)] = {
    if (level >= levels.length - 1 && stage >= levels(level).stages.length -1 ) None else Some(
      if (stage >= levels(level).stages.length - 1) (level + 1, 0) else (level, stage + 1)
    )
  }

  def show(l:Int, s:Int):VHtmlNode = {
    level = l
    stage = s
    rerender()
  }

  def elements = {
    levels(level).stages
  }

  def layout(s:Sequencer, si:SequenceItem, i:Int):VHtmlNode = {
    <.div(^.cls := "challenge-wrapper",
      <.div(^.cls := "challenge-header", header(this)),
      <.div(^.cls := "challenge", si.content),
      <.div(^.cls := "countdown-box", tr(this)),
      <.div(^.cls := "stage-progress", progressBlock(this)),
      <.div(^.cls := "page-controls", pageControls(this))
    )
  }

  def render = {
    <.div(
      VSlides(1920, 1080, layout=layout)(elements).atSlide(stage)
    )
  }

}