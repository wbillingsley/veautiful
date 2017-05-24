# Veautiful

Veautiful is a home-grown web UI toolkit written in Scala.

## Get started

To be added...


## Guiding principle

The guiding principle is that *you* own the design of your code; the framework doesn't.

Rather than force you to play the way the framework wants you to play, it'll give you a bunch
of components and node types that compose fairly neatly, and each have low conceptual overhead.

That also lets it tie into any number of reactive frameworks -- be it RxScala, or my own little
framework (handy)

All going well, it shouldn't feel "revolutionary", but small and straightforward, while letting
you build UIs that work with very little code.

It is not functionally pure -- the UI mutates, so it seems reasonable to make that plain, especially
as the browser UI is substantially a single-threaded environment where concurrency concerns are
easier to deal with. But it makes it easy to connect reactive, pure, and `Future`ful workflows up to 
the UI.


## A virtual DOM that doesn't "own" your design

Like many frameworks, such as React.js, Veautiful offers a virtual DOM. 
In our case, this is through the trait `VNode`.

You usually won't implement this trait directly -- it has useful subclasses.

A `VNode` is a virtual DOM node, which if it is attached to the DOM holds a reference to the 
real DOM node in `domNode`. It has a lifecycle that is essentially defined by

1. `beforeAttach()`
2. `attach()` - attach yourself to a DOM node that you create
3. `afterAttach()`

and a similar detachment process

1. `beforeDetach()`
2. `detach()`
3. `afterDetach()`

While the node is attached, `domNode` will contain `Some(n:dom.Node)`

For some components, the tree might end there. The VNode might control its portion of the DOM
directly, for example calling d3.js to update a graph. For other nodes, there might be child
VNodes. 


## MakeItSo

Yes, this trait is named after a certain surprisingly-English-sounding French starship captain.

`MakeItSo` means an object can mutate itself to look like an example.

This lets us declare a function that returns what the UI node should look like, pass it to the
attached UI node, and it'll update itself to match (avoiding losing transient state such as
scroll position).

Most Virtual DOMs have something like this, but we've separated it into its own trait -- largely
because later it'll let our view hierarchy bridge smoothly across from parts of the tree that
render as nodes to parts of the tree that paint themselves into a canvas. `MakeItSo` *just* means that this item can update itself based on an example; it does not make
any assumptions that this is a `VNode`.

## DiffNode

Now we reach the traditional part of react-like UIs, where they generate the VDOM as 
you would like it to be and it then does some diffing to work out how to make the DOM like that.

A `DiffNode` is a `VNode` node implements `MakeItSo` by first updating its own node, then
diffing its current children with the children you would like it to have. And then it calls 
`makeItSo` on any children that implement the trait.

If you put a `DiffNode` at the top of the tree, you get a UI that works very much like React.js.
Call `rerender` on the topmost node, and it'll do a diff of the virtual DOM to work out what to
update...


However, in React.js you generally *only* ask the UI to update the whole tree. In Veautiful,
you can ask any DiffNode to rerender its subtree. This puts control in your hands over whether
you you recalculate everything or just the part you know needs updating.

## DiffNode's diff algorithm

The algorithm for doing the diff is fairly naive, and is in `Differ`. It could probably do with
some optimisation, but at the moment it: 

* scans both lists at the same time
* where the lists don't match, it looks ahead in the existing list to see if there is a matching node
  that can be moved forward
* if not, it inserts the element from the list you've past in
* eventually, this leaves all the nodes to be cut from the existing list at the end; these are 
  then removed.
  
Whether two `VNode`s are equal is left for the nodes to define -- it just uses the Scala `==`.

This means you can define what equality means for your subcomponent by how you define `equals` 
on your component class. 

Generally, I recommend defining your component as a case class, with all the properties that
you care about for node equality in the case class's constructor parameters, and all the 
properties you don't care about for equality as separately defined `var`s in the object. 

## HTML elements

VNodes that correspond to HTML elements are provided. They are instances of `DElement`, which
extends `DiffNode`. They implement react-style equality -- two `DElement`s are by default
considered equal (kept in-place and mutated) if they have the same tag, and non-equal (replaced 
and regenerated) if they have different tags.

As with React, there is an optional second parameter that can give them a key that will also be
used in the equality test.

```scala
case class DElement(name:String, uniqEl:Any = "") extends DiffNode {
  // etc
}
```

## ElementComponent

Sometimes you have a control where you want it to take particular actions to mutate itself,
but then let its children sort themselves out by diffing.

For example, a Router at the top of the UI tree, where you would like to have a `goTo` method
that will update the current URL and regenerate the UI. 

`ElementComponent` is quite useful for this. It's really rather small so here is the whole code:

```scala
class ElementComponent(el:DElement) extends VNode {

  def domNode = el.domNode

  def attach() = {
    if (isAttached) {
      throw new IllegalStateException("Attached twice")
    }
    el.attach()
  }

  def detach() = el.detach()

  def renderElements(ch:VNode) = el.updateChildren(Seq(ch))

}
```

As you can see, it delegates everything to the DElement you pass in. But as your subclass now
has a reference to this DElement, you can just call `renderElements(tree)` to set what the child subtree
should look likee.




