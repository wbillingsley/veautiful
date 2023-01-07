package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.{<, DElement, SVG, Styling, DHtmlModifier, DHtmlComponent, DDomContent, ^, CustomElementChild, VHtmlElement}
import com.wbillingsley.veautiful.templates.{Sequencer, templateStyleSuite}
import Sequencer.LayoutFunc

object Challenge {

  val challengeHeaderHeight = "50px"
  val challengeHeaderBackground = "#444"
  val challengeHeaderColour = "white"

  val defaultTheme:Styling = Styling(
    s"""display: grid;
      |grid-template-columns: 1fr 320px;
      |grid-template-rows: $challengeHeaderHeight 1fr 70px;
      |height: 100%;
      |""".stripMargin).modifiedBy(
    " .challenge-header" ->
      s"""grid-column-start: 1;
        |grid-column-end: 2;
        |grid-row-start: 1;
        |border-bottom: 1px solid lightgrey;
        |background: $challengeHeaderBackground;
        |color: $challengeHeaderColour;""".stripMargin,
    " .challenge-header .home-link" ->
      """width: 60px;
        |border-right: 2px solid white;
        |float: left;
        |margin-right: 25px;
        |line-height: 50px;
        |color: white;
        |font-size: 26px;
        |text-align: center;""".stripMargin,
    " .challenge-header .challenge-name" -> s"font-size: 26px; line-height: $challengeHeaderHeight;",
    " .challenge" -> "grid-column-start: 1; grid-row-start: 2; grid-row-end: 4; background: white;",
    " .countdown-box" ->
      s"""border-left: 1px solid lightgrey;
       |border-bottom: 1px solid lightgrey;
       |grid-column-start: 2;
       |grid-row-start: 1;
       |background: $challengeHeaderBackground;""".stripMargin,
    " .stage-progress " -> "grid-column-start: 2; grid-row-start: 2; grid-row-end: 4; border-left: 1px solid lightgrey; height: 100%; background: white;",
    " .stage-progress .progress-level" -> "padding: 10px; border-bottom: 1px solid lightgray;",
    " .stage-progress .progress-level.level-active" -> "color: white; background-color: #7d5177;",
    " .stage-progress .progress-level.level-active a" -> "color: white;",
    " .stage-progress .progress-level .stage-link" -> "font-size: 36px; line-height: 36px;",
    " .stage-progress .progress-level.level-active a.stage-link.stage-active" -> "color: cadetblue;",
    " .stage-progress .progress-level a" -> "color: inherit;",
    " .page-controls" ->
      """grid-column-start: 2;
        |grid-row-start: 3;
        |text-align: center;
        |padding: 10px;
        |border-left: 1px solid lightgray; background: white;""".stripMargin
  ).register()

  def hgutter = <.div(^.cls := "row hgutter")

  def card(s:String)(ac: DHtmlModifier *) = <.div(^.cls := "card",
    <.div(^.cls := "card-body",
      <.div(^.cls := "card-title", <.h4(s)),
      <.div(ac:_*)
    )
  )

  def card(ac: DHtmlModifier *) = <.div(^.cls := "card",
    <.div(^.cls := "card-body",
      <.div(ac:_*)
    )
  )

  def cardText(ac: DHtmlModifier *) = <.div(^.cls := "card-text", <.div(ac:_*))

  val textColumnStyling = Styling("margin-top: 50px; margin-left: 50px; margin-right: 50px;").register()
  def textColumn(ac: DHtmlModifier *) = <.div(^.cls := s"text-column ${textColumnStyling.className}", <.div(ac:_*))

  def textAndEx(left: DHtmlModifier *)(right: DHtmlModifier *) = {
    split(textColumn(left:_*))(right:_*)
  }

  val split2Styling = Styling("display: grid; grid-template-columns: 1fr 1fr;").register()
  def split(l:DHtmlModifier*)(r:DHtmlModifier*) = <.div(^.cls := s"split2 ${split2Styling.className}",
    <.div(l:_*),
    <.div(r:_*)
  )


  /** A Stage within a Level */
  trait Stage extends DHtmlComponent {

    /** Stages can be marked complete or partially complete. This is used, for instance, in Circuits Up! to visualise progress. */
    def completion:Completion

    /** The "kind" of Stage changes the icon shown for it. */
    def kind:String

  }

  /** A Level has a name and a sequences of Stages */
  case class Level(name:String, stages:Seq[Stage])

  sealed trait Completion {
    def cssClass:String
  }
  case object Open extends Completion {
    val cssClass = "open"
  }
  case object Incomplete extends Completion {
    val cssClass = "incomplete"
  }
  case class Complete(mark:Option[Double], medal:Option[String]) extends Completion {
    def cssClass = "complete " + medal.getOrElse("")
  }

  type HomePath = (Challenge) => String
  type LevelPath = (Challenge, Int) => String
  type StagePath = (Challenge, Int, Int) => String

  def defaultHomeIcon = <.span(^.cls := "home-icon", "⌂")

  def defaultHeader(homePath:HomePath, homeIcon: => DDomContent = defaultHomeIcon) = { (c:Challenge) =>
    <.div(
      <.a(^.cls := "home-link", ^.href := homePath(c), homeIcon),
      <.span(^.cls := "challenge-name", c.levels(c.level).name)
    )
  }

  def defaultTopRight() = { (c:Challenge) => <.div() }

  def progressTile(level:Level, i:Int, levelPath: LevelPath, stagePath: StagePath) = { (c:Challenge) =>

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
          <.a(^.cls :=
            s"stage-link ${if (stageActive(j)) "stage-active" else ""} ${s.completion.cssClass}", ^.href := stagePath(c, i, j),
              s.kind match {
                case "video" =>
//                  <.svg(^.attr("width") := 25, ^.attr("height") := 25, SVG.polygon(^.attr("points") := "5,5 5,20 20,12"))
                  <.span(^.cls := "video-stage", "▸")
                case _ => <.span(^.cls := "default-stage", "●")
              }
          )
        }
      )
    )
  }

  def defaultProgressBlock(levels:Seq[Challenge.Level], levelPath: LevelPath, stagePath: StagePath) = { (c:Challenge) =>
    <.div(^.cls := "progress-block",
      for {
        (l, i) <- levels.zipWithIndex
      } yield progressTile(l, i, levelPath, stagePath)(c)
    )
  }

  def defaultPageControls(levels:Seq[Challenge.Level], levelPath: LevelPath, stagePath: StagePath) = { (c:Challenge) =>
    <.div(^.cls := "btn-group",
      for { (l, s) <- c.previous } yield <.a(^.cls := "btn btn-outline-secondary",
        ^.href := stagePath(c, l, s), s"Previous"
      ),
      for { (l, s) <- c.next } yield <.a(^.cls := "btn btn-outline-secondary",
        ^.href := stagePath(c, l, s), s"Next"
      )
    )
  }

  def apply(levels: Seq[Challenge.Level], homePath: HomePath, levelPath: LevelPath, stagePath: StagePath, homeIcon: => DDomContent = defaultHomeIcon, scaleToWindow:Boolean = true) = {
    new Challenge(levels,
      defaultHeader(homePath, homeIcon),
      defaultTopRight(),
      defaultProgressBlock(levels, levelPath, stagePath),
      defaultPageControls(levels, levelPath, stagePath),
      scaleToWindow
    )
  }
}


/**
  * A self-paced interactive tutorial laid out in a fixed resolution (that then auto-scales to fit in the screen).
  * It is divided into `Level`s that have a title and are themselves divided into `Stage`s. 
  * 
  * The Levels of one Stage are held in the DOM at any time. This hopefully makes a good trade-off between wanting 
  * movement back and forth between stages not to cause re-loads of slow-to-load items (e.g. videos), but not keeping
  * the entire tree loaded into the DOM at once.
  * 
  * Currently, Challenges are laid out in 1080p (i.e. 1920 x 1080), however this might be made variable in future versions.
  */
class Challenge(val levels: Seq[Challenge.Level],
                val header: (Challenge) => DDomContent,
                val tr: (Challenge) => DDomContent,
                val progressBlock: (Challenge) => DDomContent,
                val pageControls: (Challenge) => DDomContent,
                scaleToWindow:Boolean = true
               ) extends DHtmlComponent {

  var level:Int = 0
  var stage:Int = 0

  /** The level and stage number preceeding this one */
  def previous:Option[(Int, Int)] = {
    if (level <= 0 && stage <= 0) None else Some(
      if (stage > 0) (level, stage - 1) else (level - 1, levels(level - 1).stages.length - 1)
    )
  }

  /** The level and stage number after this one */
  def next:Option[(Int, Int)] = {
    if (level >= levels.length - 1 && stage >= levels(level).stages.length -1 ) None else Some(
      if (stage >= levels(level).stages.length - 1) (level + 1, 0) else (level, stage + 1)
    )
  }

  /** Show a particular level and stage */
  def show(l:Int, s:Int) = {
    level = l
    stage = s
    rerender()
  }

  def elements = {
    levels(level).stages
  }

  /** Levels are rendered as a slide deck. This gives access to the slides. */
  def levelSlides:VSlides = {
    VSlides(1920, 1080, elements, layout=Layout)
  }

  object Layout extends VSlides.Layout:
    def apply(s:VSlides, node:VHtmlElement, i:Int) = {
      <.div(^.cls := s"challenge-wrapper ${Challenge.defaultTheme.className}",
        <.div(^.cls := "challenge-header", header(Challenge.this)),
        <.div(^.cls := "challenge", node),
        <.div(^.cls := "countdown-box", tr(Challenge.this)),
        <.div(^.cls := "stage-progress", progressBlock(Challenge.this)),
        <.div(^.cls := "page-controls", pageControls(Challenge.this))
      ).build()
    }

  def render = {
    <.div(
      DefaultVSlidesPlayer(levelSlides, scaleToWindow=scaleToWindow)(stage, sequencerLayout = Sequencer.bareLayout)
    )
  }

}