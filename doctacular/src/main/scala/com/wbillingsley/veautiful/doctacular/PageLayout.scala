package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^, Styling, StyleSuite}

class PageLayout {

  given styleSet:StyleSuite = StyleSuite()

  val leftSideBarAndContentStyle = Styling(
    """display: grid;
      |grid-template-columns: 300px auto;
      |min-height: 100vh;
      |""".stripMargin).register()

  val leftSidebarStyle = Styling(
    """border-right: 1px solid rgba(0,0,0,0.7);
      |background: #f9f9ff;
      |position: sticky;
      |overflow-y: auto;
      |padding: 5px;
      |""".stripMargin).register()

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
  
  
  def renderPage(site:Site, contentFunction: => VHtmlNode):VHtmlNode = {
    <.div(^.cls := leftSideBarAndContentStyle.className,
      leftSideBar,
      <.div(^.cls := contentContainerStyle.className, 
        //<.div(^.cls := contentWrapperStyle.className,
          contentFunction
        //)
      )
    )
  }
  
  def leftSideBar = <("aside")(^.cls := leftSidebarStyle.className,
    renderToc(site, site.toc)
  )
  
  def renderToc(site:Site, toc:Toc, depth:Int = 0):VHtmlNode = {
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
