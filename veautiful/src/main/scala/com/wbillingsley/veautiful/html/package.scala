package com.wbillingsley.veautiful

import org.scalajs.dom

package object html {

  type VHtmlNode = VNode[dom.Node]

  type VHtmlDiffNode = DiffNode[dom.Element, dom.Node]

  type VHtmlComponent = DiffComponent[dom.Element, dom.Node]

}
