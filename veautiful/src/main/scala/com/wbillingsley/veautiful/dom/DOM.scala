package com.wbillingsley.veautiful.dom

import com.wbillingsley.veautiful
import org.scalajs.dom
import com.wbillingsley.veautiful.Blueprint

type VDomNode = veautiful.VNode[dom.Node]
type VDomElement = veautiful.VNode[dom.Element]
type VDomContent = VDomNode | Blueprint[VDomNode]

type DDomComponent = veautiful.html.DomDiffComponent[dom.Element]
type DDomBlueprint = veautiful.Blueprint[veautiful.html.DElement[dom.Element]]
type DDomElement = veautiful.html.DElement[dom.Element]
type DDomContent = DDomElement | DDomBlueprint
type DDomModifier = veautiful.html.ElementChild[dom.Element]

