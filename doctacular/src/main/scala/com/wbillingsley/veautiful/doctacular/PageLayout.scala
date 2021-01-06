package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.MakeItSo
import com.wbillingsley.veautiful.html.{<, StyleSuite, Styling, VHtmlComponent, VHtmlNode, ^}

class PageLayout(site:Site) {

  given styleSet:StyleSuite = StyleSuite()

  val leftSideBarAndContentStyle = Styling(
    """display: grid;
      |grid-template-columns: 300px auto;
      |min-height: 100vh;
      |transition: grid-template-columns 0.3s;
      |""".stripMargin).modifiedBy(
    ".closed" -> "grid-template-columns: 0px auto;"
  ).register()

  val leftSidebarStyle = Styling(
    """border-right: 1px solid rgba(0,0,0,0.7);
      |background: #f9f9ff;
      |position: sticky;
      |top: 0;
      |height: 100vh;
      |overflow-y: auto;
      |padding: 5px;
      |transition: transform 0.25s;
      |""".stripMargin).modifiedBy(
    ".closed" -> "transform: translateX(-300px);"
  ).register()

  val contentContainerStyle = Styling(
    """width: 100%;
      |margin-left: auto;
      |margin-right: auto;
      |padding-left: 15px;
      |padding-right: 15px;
      |""".stripMargin).withAtRules(
    "@media (min-width: 576px)" -> "max-width: 540px;",
    "@media (min-width: 768px)" -> "max-width: 720px;",
    "@media (min-width: 992px)" -> "max-width: 960px;",
    "@media (min-width: 1200px)" -> "max-width: 1140px;",
  ).register()

  val contentContainerSidebarOpenStyle = Styling(
    """width: 100%;
      |margin-left: auto;
      |margin-right: auto;
      |padding-left: 15px;
      |padding-right: 15px;
      |""".stripMargin).withAtRules(
    "@media (min-width: 876px)" -> "max-width: 540px;",
    "@media (min-width: 1068px)" -> "max-width: 720px;",
    "@media (min-width: 1292px)" -> "max-width: 960px;",
    "@media (min-width: 1500px)" -> "max-width: 1140px;",
  ).register()

  val tocStyles = Map[Int, Styling](
    0 -> Styling(
      """list-style: none;
        |padding-inline-start: 0;
        |margin: 20px 0 0 15px;
        |font-size: 16px;
        |""".stripMargin).register(),
    -1 -> Styling(
      """list-style: none;
        |padding-inline-start: 0;
        |margin: 0 0 0 15px;
        |font-size: 16px;
        |""".stripMargin).register()
  )

  val tocItemStyles = Map[Int, Styling](
    0 -> Styling(
      """margin: 15px 0 0 0;
        |font-weight: bold;
        |""".stripMargin).register(),
    -1 -> Styling(
      """margin: 5px 0 0 0;
        |""".stripMargin).register()
  )

  val sideBarToggleStyle = Styling(
    """border-radius: 3px;
      |border: 1px solid rgba(0,0,0,0.7);
      |border-left: none;
      |position: sticky;
      |top: 0;
      |margin-left: -2px;
      |background: #f9f9ff;
      |""".stripMargin).modifiedBy(
    ":hover" -> "filter: brightness(85%);"
  ).register()

  val sideBarSymbolStyle = Styling(
    """display: flex;
      |width: 20px;
      |height: 30px;
      |background: repeating-linear-gradient(
      |  0deg, rgba(0,0,0,0) 0px, rgb(0,0,0,0) 5px, #aaa 5px, #aaa 9px, rgba(0,0,0,0) 9px, rgb(0,0,0,0) 13px, #aaa 13px, #aaa 17px, rgba(0,0,0,0) 17px, rgb(0,0,0,0) 21px, #aaa 21px, #aaa 25px
      |)
      |""".stripMargin).register()


  /** A stateful component allowing us to open and close the sidebar */
  class SideBarAndLayout(var left: () => VHtmlNode, var right: () => VHtmlNode, var open:Boolean = true) extends VHtmlComponent with MakeItSo {

    def sideBarToggle = <.button(^.cls := sideBarToggleStyle.className, ^.onClick ==> { (_) =>
        open = !open
        rerender()
      }, <.div(^.cls := sideBarSymbolStyle.className)
    )

    override def render = {
      <.div(^.cls := (if open then leftSideBarAndContentStyle.className else s"${leftSideBarAndContentStyle.className} closed"),
        <("aside")(^.cls := (if open then leftSidebarStyle.className else s"${leftSidebarStyle.className} closed"),
          left(),
        ),
        <.div(
          sideBarToggle,
          <.div(^.cls := (if open then contentContainerSidebarOpenStyle.className else contentContainerStyle.className),
            right()
          )
        )
      )
    }

    def makeItSo = {
      case other:SideBarAndLayout =>
        open = other.open
        left = other.left
        right = other.right
    }

  }

  private val slideToggle = SideBarAndLayout(() => <.div(), () => <.div())

  def renderPage(site:Site, contentFunction: => VHtmlNode):VHtmlNode = {
    slideToggle.left = () => leftSideBar(site)
    slideToggle.right = () => contentFunction
    slideToggle.update()
    slideToggle
  }

  def leftSideBar(site:Site) = renderToc(site, site.toc)

  def renderToc(site:Site, toc:Toc, depth:Int = 0):VHtmlNode = {
    println(toc.entries.toList)

    <.ul(^.cls := tocStyles.getOrElse(depth, tocStyles(-1)).className,
      for {
        (title, entry) <- toc.entries
      } yield entry match {
        case r:Route =>
          val path = site.router.path(r)
          <.li(^.cls := tocItemStyles.getOrElse(depth, tocItemStyles(-1)).className,
            <.a(^.href := path, title)
          )
        case t:Toc =>
          <.div(
            <.p(^.cls := tocItemStyles.getOrElse(depth, tocItemStyles(-1)).className,
              title
            ), renderToc(site, t, depth + 1)
          )
      }
    )
  }

  styleSet.install()

}

