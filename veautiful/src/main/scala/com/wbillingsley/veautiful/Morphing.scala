package com.wbillingsley.veautiful

/**
  * Morphing provides a means of decorating an Update component with the capability to take an
  * updatable property and "Make it so".
  * 
  * When `makeItSo` is called with an equal Morphing, the property is changed and `update()` is called.
  */
trait Morphing[P](val initial: P) extends MakeItSo with Update with HasTestMatch with HasRetention {
  final var _prop: P = initial

  final def prop: P = _prop

  def updateProp(p: P): Unit = {
    _prop = p
    update()
  }

  /**
    * Declares a MakeItSo that is only applicable to container type C and will update
    * the prop value and call update. It's inline because we need to access the container's
    * class C which is erased at runtime, but should be known at the time that the concrete
    * child class says "val morpher = createMorpher(this)"
    *
    * Note that if your concrete MorphComponent is parametrised in the prop, e.g.
    * class ParamMorphComponent[T](init:T) extends MorphComponent[T](init) {
    * val morpher = createMorpher(this)
    * }
    *
    * You will correctly get a warning that the typecheck cannot be made at runtime, whereas
    * for a class where T is fixed you won't.
    *
    * @param container
    * @tparam C
    * @return
    */
  inline def createMorpher[C <: Morphing[P]](container: C): MakeItSo = new MakeItSo {
    def makeItSo = {
      case c: C => 
        container.updateProp(c.prop)
    }
  }

  /** The morpher needs to be in a stable value */
  val morpher: MakeItSo

  /** Delegate MakeItSo implementation to the morpher */
  export morpher.makeItSo

  override def testMatches(other: HasRetention): Boolean = 
    super.testMatches(other) && (other match {
      case m:Morphing[_] => prop == m.prop
      case _ => false
    })

}
