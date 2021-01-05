package com.wbillingsley.veautiful.html

class Styling(val modifiers: Map[String, String] = Map.empty)(using ss:StyleSuite) {
  
  def this(just:String)(using ss:StyleSuite) = this(Map("" -> just))
  
  val className = ss.randomName
  
  def modifiedBy(pairs:(String, String)*) = Styling(modifiers ++ pairs)
  
  def register() = ss.register(this)
}

class StyleSuite() {
  
  import scala.collection.mutable
  import scala.util.Random
  
  private var installed = false

  private val stylings:mutable.Map[String, Styling] = mutable.Map.empty
  
  def randomName = Random.nextString(8)
  
  val name = randomName
  
  private lazy val styleElement = render
  
  def install():Unit = if !installed then {
    val r = styleElement.attach()
    org.scalajs.dom.document.head.appendChild(r)
    installed = true
  }
  
  def update():Unit =
    if installed then styleElement.makeItSo(render)
  
  private def generateCss:String = {
    (for {
      c <- stylings.keys
      m <- stylings(c).modifiers.keys
    } yield
      s"""
         |.$c$m {
         |  ${stylings(c).modifiers(m)}
         |}
         |""".stripMargin).mkString("\n\n")
  }
  
  def register(s:Styling):Styling = { 
    stylings.put(s.className, s)
    update()
    s
  }

  def render = <("style")(^.attr("id") := name, ^.attr("type") := "text/css",
    generateCss
  )



}

