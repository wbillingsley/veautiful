package docs

import com.wbillingsley.veautiful.html.{<, ^}
import org.scalajs.dom

def styleSuites = <.div(
  Common.markdown(
    """# CSS-in-JS
      |
      |Components often need to come with a set of CSS styles, in order to make them layout correctly. So that you
      |don't need to deal with a separate packaging mechanism for CSS, Veautiful comes with a simple *CSS-in-JS*
      |system.
      |
      |First, declare a `StyleSuite`. If you are writing a site rather than a library, you might want to make this a 
      |`given` value so you don't have to keep referring to it.
      |
      |```scala
      |import com.wbillingsley.veautiful.html.{Styling, StyleSuite}
      |
      |given mySiteStyles:StyleSuite = StyleSuite()
      |```
      |
      |This is going to hold the set of styles you declare, and (later) can install itself into the page.
      |
      |Next, declare a `Styling`:
      |
      |```scala
      |val myInputStyle = Styling("border-radius: 10px; background: aliceblue;").register()
      |```
      |
      |Your `Styling` will now be registered in the `StyleSuite` and will have a randomly generated CSS class name.
      |You can use this in your code:
      |
      |```scala
      |def nameField = <.div(
      |  <.label("name"), <.input(^.cls := myInputStyle.className, ^.attr("type") := "text")
      |)
      |```
      |
      |Before your site first renders, ask the `StyleSuite` to install itself. 
      |
      |```scala
      |mySiteStyles.install()
      |```
      |
      |It will insert a `style` element into the head of the document, containing the class names and their rules.
      |The class names are randomly chosen unicode characters, making them very unlikely to collide.
      |For example, you might find it produces
      |
      |```html
      |<style id="뜩胩鿴颴鋈鼓蚔" type="text/css">
      |  .ⶠ�䉁袎✢寭鉓뾐 {
      |    border-radius: 10px; background: aliceblue;
      |  }
      |</style>
      |```
      |
      |## Modifiers
      |
      |If you wish to create a rule that has some kind of modifier or pseudo-selector, call `modifiedBy` during the
      |creation of the styling. For instance
      |
      |```scala
      |val tocLinkStyle = Styling(
      |  "margin: 5px 0 0 0;"
      |).modifiedBy(
      |  ".active" -> "border-right: 3px solid orange; background: #ffffff80;",
      |  ":hover" -> "filter: brightness(0.9)";
      |).register()
      |```
      |
      |Any spaces at the beginning of a modifier are significant. The Styling, when writing itself to CSS, will prepend its class to the modifier. So,
      |
      |```scala
      |Styling("").modifiedBy(
      |  " .selected" -> "font-size: 15px;"
      |).register()
      |```
      |
      |Will produce a CSS rule that looks something like
      |
      |```css
      |stylingClass.selected {
      |  font-size: 15px;
      |}
      |```
      |
      |Whereas 
      |
      |```scala
      |Styling("").modifiedBy(
      |  " .selected" -> "font-size: 15px;"
      |).register()
      |```
      |
      |Will produce a CSS rule that selects on its descendents: 
      |
      |```css
      |stylingClass .selected {
      |  font-size: 15px;
      |}
      |```
      |
      |## At-Rules
      |
      |In CSS, At-Rules (e.g. `@media`) go at the top level of a stylesheet, rather than embedding the at-rule embedded in the style.
      |
      |So that this can be generated conveniently, `Styling`s also keep a set of at-rules.
      |
      |```scala
      |val contentContainerStyle = Styling(
      |  "width: 100%;"
      |).withAtRules(
      |  "@media (min-width: 576px)" -> "max-width: 540px;",
      |  "@media (min-width: 768px)" -> "max-width: 720px;",
      |  "@media (min-width: 992px)" -> "max-width: 960px;",
      |  "@media (min-width: 1200px)" -> "max-width: 1140px;",
      |).register()
      |```
      |
      |Note that you can't currently apply modifiers into the at-rule. Largely because I haven't yet 
      |encountered a case where I'd need to, instead of it being cleaner just to change between two stylings instead.
      |
      |## Applying a styling to an element
      |
      |The little language for writing VHtmlContent accepts stylings' `className` or just the styling itself into the `^.attr` modifier
      |
      |e.g.
      |
      |```scala
      |div(^.cls := myStyling.clasName, "Some content")
      |```
      |
      |or
      |
      |```scala
      |div(^.cls := mySyling, "Some content")
      |```
      |
      |or even 
      |
      |```scala
      |div(^.cls := (myFirstSyling, mySecondStyling, "selected"), "Some content")
      |```
      |
      |
      |## Going mutable
      |
      |Generally, I recommend using Stylings immutably.
      |However, some sites might want to modify a styling from a library slightly. 
      |For instance, this docs site alters the color of the Doctacular sidebar to `aliceblue`.
      |
      |`Styling` contains a method `addRules` that permits the caller to insert some extra CSS to that
      |styling. For example:
      |
      |```scala
      |site.pageLayout.leftSideBarStyle.addRules("background: aliceblue; border: none;")
      |```
      |
      |Modifier rules can be added by passing a `Map`. For example:
      |
      |```scala
      |site.pageLayout.sideBarToggleStyle.addRules(Map(":hover" -> "filter: brightness(1.1);"))
      |```
      |
      |""".stripMargin
  )
)