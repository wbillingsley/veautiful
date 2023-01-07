package com.wbillingsley.veautiful.doctacular.Installers

import com.wbillingsley.veautiful.Update
import com.wbillingsley.veautiful.html.{Attacher, <, ^, Markup}
import com.wbillingsley.veautiful.doctacular.*
import scalajs.js

import scala.annotation.targetName

object installMarked {
    extension (root:Update) {
        /**
          * Installs marked.js as a plain old JS script in the page, and returns a Markup that uses  it
          *
          * @param version the version of Marked.js to install (4+)
          */
        def installMarked(version:String):Markup = {
            val url =s"https://cdnjs.cloudflare.com/ajax/libs/marked/$version/marked.min.js"

            Attacher.installInHead(
                <.script(^.src := url, ^.on.load --> root.update())
            )

            Markup { (s:String) => 
                if js.typeOf(js.Dynamic.global.marked) == "undefined" then "Loading marked.js" else js.Dynamic.global.marked.parse(s).asInstanceOf[String] 
            }
        }
    }

    extension (site:Site) {
        /**
          * Installs marked.js as a plain old JS script in the page, and returns a Markup that uses it
          *
          * @param version the version of Marked.js to install (4+)
          */
        def installMarked(version:String = "4.2.5"):Markup = site.router.installMarked(version)
    }
}