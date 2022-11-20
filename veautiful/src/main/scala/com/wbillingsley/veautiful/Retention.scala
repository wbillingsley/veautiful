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
  def retainFor(other:HasRetention) = r.retention match {
    case Retention.Equality => r == other
    case Retention.Keep(v) => r.getClass.isAssignableFrom(other.getClass) && r.retention == other.retention
    case Retention.Keyed(k) => r.getClass.isAssignableFrom(other.getClass) && r.retention == other.retention
  }

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
