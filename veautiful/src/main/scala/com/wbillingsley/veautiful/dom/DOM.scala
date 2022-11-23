package com.wbillingsley.veautiful.dom

import com.wbillingsley.veautiful
import org.scalajs.dom

type VNode = veautiful.VNode[dom.Node]
type VElement = veautiful.VNode[dom.Element]

type DDomComponent = veautiful.DiffComponent[dom.Element, dom.Node]
type DDomBlueprint = veautiful.Blueprint[veautiful.html.DElement[dom.Element]]
type DDomElement = veautiful.html.DElement[dom.Element]
type DDomContent = DDomElement | DDomBlueprint

