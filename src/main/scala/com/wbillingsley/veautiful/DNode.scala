package com.wbillingsley.veautiful

import org.scalajs.dom

/**
  * A DNode has a create and a makeItSo
  */
trait DNode extends VNode {

  def create():dom.Element

  var domNode:Option[dom.Element]

  /**
    * Children VNodes
    */
  def children:Seq[VNode]

  def attach() = {
    val n = create()
    for { ch <- children } {
      ch.attach()
    }

    for {
      d <- children
      childN <- d.domNode
    } n.appendChild(childN)

    domNode = Some(n)
    n
  }

  def detach() = {
    for {
      n <- domNode
      d <- children
      childN <- d.domNode
    } n.removeChild(childN)

    for {
      d <- children
    } d.detach()
  }

}