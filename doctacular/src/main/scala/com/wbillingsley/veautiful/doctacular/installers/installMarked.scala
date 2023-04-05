package com.wbillingsley.veautiful.doctacular.installers

import com.wbillingsley.veautiful.{Update, PushVariable}
import com.wbillingsley.veautiful.html.{Attacher, <, ^, MarkupTransformer}
import com.wbillingsley.veautiful.doctacular.*
import scalajs.js
import org.scalajs.dom 

import scala.annotation.targetName

extension (root:Update) {
    /**
         * Installs marked.js as a plain old JS script in the page, and returns a Markup that uses  it
         *
         * @param version the version of Marked.js to install (4+)
         */
    def installMarked(version:String):MarkupTransformer[dom.html.Element] = {
        val url =s"https://cdnjs.cloudflare.com/ajax/libs/marked/$version/marked.min.js"
        val loaded = PushVariable(false)(_ => ())
        val dynamicLoaded = loaded.dynamic

        Attacher.installInHead(
            <.script(^.src := url, ^.on.load --> { loaded.value = true })
        )

        new MarkupTransformer { 
            def apply(s:String) = 
                <.dynamic.div(^.attr.style := "display: contents;",
                    for loaded <- dynamicLoaded yield
                        if loaded then
                            if js.typeOf(js.Dynamic.global.marked) == "undefined" then 
                                "Loaded script but marked is undefined." 
                            else js.Dynamic.global.marked.parse(s).asInstanceOf[String] 
                        else "Loading marked.js"                
                ) 
            
        }
    }
}

extension (site:Site) {
    /**
         * Installs marked.js as a plain old JS script in the page, and returns a Markup that uses it
         *
         * @param version the version of Marked.js to install (4+)
         */
    def installMarked(version:String = "4.2.5"):MarkupTransformer[dom.html.Element] = site.router.installMarked(version)
}
