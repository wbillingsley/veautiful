package com.wbillingsley.veautiful


/**
 * Hints to reconcilers how they should go about managing whether to retain or replace this component
 */
enum Retention {
  /** Keep the component only if it == the destination */
  case Equality

  /** Keep the component if the values match, but don't make special efforts to preserve it. Keep values need not be unique. */
  case Keep[T](value:T)
  
  /** Keep the component if the keys match, making a special effort to preserve items with the same key. Keys should be unique. */
  case Keyed[T](key:T)
}

trait HasRetention { 
  def retention:Retention = Retention.Equality

  def key:Option[Any] = retention match {
    case Retention.Keyed(key) => Some(key)
    case _ => None
  }
}

trait Keep[T](v:T) extends HasRetention {
  override def retention = Retention.Keep(v)
}

extension (r:HasRetention) {
  def retainFor(other:HasRetention):Boolean = (r == other) || (r.retention match {
    case Retention.Equality => 
      other match {
        case b:Blueprint[_] => r == b.build() // This is an inefficient case
        case _ => false
      }
    case Retention.Keep(v) => 
      other match {
        case b:Blueprint[_ <: HasRetention] => r.getClass.isAssignableFrom(b.cls) && (b.retention match {
          case Retention.Keep(vv) => v == vv
          case _ => r.retainFor(b.build()) // This is an inefficient case
        })
        case _ => r.getClass.isAssignableFrom(other.getClass) && r.retention == other.retention
      }
    case Retention.Keyed(k) => 
      other match {
        case b:Blueprint[_ <: HasRetention] => r.getClass.isAssignableFrom(b.cls) && (b.retention match {
          case Retention.Keyed(kk) => k == kk
          case _ => r.retainFor(b.build()) // This is an inefficient case
        })
        case _ => r.getClass.isAssignableFrom(other.getClass) && r.retention == other.retention
      }
  })

}

/**
 * Attempts to make VNodes more testable.
 */
trait HasTestMatch { self:HasRetention =>
  /** 
   * Returns true if this node is considered equivalent to the destination. 
   * The default implementation is whether or not the node should be retained in a tree reconciliation.
   * Traits that retain but alter nodes (e.g. Morphing) should also check the properties they morph on
   */
  def testMatches(other:HasRetention):Boolean = this.retainFor(other)
}


/**
  * A blueprint is an immutable representation of a (usually mutable) structure. 
  * 
  * The use of Blueprints his helps avoid the easy programming error of putting a mutable VNode structure into a val, 
  * e.g. to describe a common element on a website, but being surprised when it morphs.
  * 
  * Blueprints have to have a retention strategy, so that the reconciler knows how to compare them.
  * 
  * A Blueprint with Retention.Equality is inefficient, because the reconciler will have to build the item
  * as it compares it with items in the tree, possibly leading to it being built multiple times. The 
  * Blueprint is a different class than the MakeItSo, so the Blueprint itself cannot be == the MakeItSo.
  * 
  * However, if the MakeItSo and the Blueprint both implement the Keyed or Keep retention strategies, 
  * the reconciler can attempt to do the comparison just on the data from the Blueprint without calling build().
  */
trait Blueprint[+C <: HasRetention](val cls:Class[_ <: C]) extends HasRetention {

  /** Builds the item. Called by the reconciler if your object needs inserting into a rendered view. */
  def build():C

}
