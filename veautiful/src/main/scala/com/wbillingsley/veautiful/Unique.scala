package com.wbillingsley.veautiful

import com.wbillingsley.veautiful.html.VHtmlNode

/**
  * A VNode that delegates its operations to the delegate passed in its contstructor.
  * 
  * Equality is by reference, so this allows you to wrap a component that would normally morph itself to match another
  * tree, in order to make it a unique value that will consider itself different from all other values.
  * 
  * Currently, the implementation declares it implements Update, but will only update an Update delegate
  * 
  * @param delegate
  * @tparam N the type of node this VNode controls. E.g., dom.Node, dom.Element, but could be any target
  */
class Unique[N](val delegate: VNode[N]) extends VNode[N] with Update {
  
  export delegate.domNode
  export delegate.attach
  export delegate.detach

  override def beforeAttach(): Unit = delegate.beforeAttach()
  override def beforeDetach(): Unit = delegate.beforeDetach()
  override def afterAttach(): Unit = delegate.afterAttach()
  override def afterDetach(): Unit = delegate.afterDetach()
  
  def update():Unit = delegate match {
    case u:Update => u.update()
    case _ => // Do nothing
  }
  
}


