package com.wbillingsley.wren

sealed trait Provenance
case object UserSet extends Provenance
case object QuestionSet extends Provenance
case object Unknown extends Provenance
case class Because(constraint: Constraint, values:Seq[Value]) extends Provenance

class Value(val units:String, var value:Option[(Double, Provenance)] = None) {

  def stringify:String = value match {
    case Some((x, prov)) =>
      val (d, p) = prefix(x)
      s"$d$p$units"
    case _ => ""
  }

  def prefix(d:Double):(Double, String) = {
    if (d >= 1000000000) (d / 1000000000, "G")
    else if (d >= 1000000) (d / 1000000, "M")
    else if (d >= 1000) (d / 1000, "k")
    else if (d >= 1) (d, "")
    else if (d >= 0.001) (d * 1000, "m")
    else if (d >= 0.000001) (d * 1000000, "u")
    else if (d >= 0.000000001) (d * 1000000000, "n")
    else (d, "")
  }


}

trait Constraint {

  def name:String

  def calculable:Boolean

  def values:Seq[Value]

  def calculate():Seq[Value]

  def failed:Boolean

  def satisfied:Boolean = values.forall(_.value.nonEmpty) && !failed

}

case class EqualityConstraint(name:String, values:Seq[Value]) extends Constraint {

  override def calculable: Boolean = values.exists(_.value.nonEmpty)

  override def calculate(): Seq[Value] = {
    values.find(_.value.nonEmpty) match {
      case Some(set) =>
        for {
          v <- values.filter(_.value.isEmpty)
        } yield {
          v.value = set.value.map({ case (vv, _) => (vv, Because(this, Seq(v))) })
          v
        }
      case _ => Seq.empty
    }
  }

  override def failed: Boolean = {
    val set = values.filter(_.value.nonEmpty).map(_.value.map(_._1))
    // Error if there are two values that differ by more than 1%
    set.zip(set.tail).exists{ case (Some(x), Some(y)) => Math.abs(x - y) > (x + y) / 100 }
  }

}


case class SumConstraint(name:String, values:Seq[Value], result:Double, tolerance:Double = 0.01) extends Constraint {

  override def calculable: Boolean = values.count(_.value.isEmpty) == 1

  def withinTolerance(a:Double, b:Double):Boolean = Math.abs(a / b) <= tolerance

  override def calculate(): Seq[Value] = {
    if (calculable) {
      val s = (for {
        v <- values
        (num, _) <- v.value
      } yield num).sum

      for {
        v <- values if v.value.isEmpty
      } yield {
        v.value = Some((s, Because(this, values.filter(_.value.isEmpty))))
        v
      }
    } else Seq.empty

  }

  override def failed: Boolean = {
    values.forall(_.value.nonEmpty) && {
      !withinTolerance(result, (for {
        v <- values
        (num, _) <- v.value
      } yield num).sum)
    }
  }

}


case class ConstraintPropagator(constraints:Seq[Constraint]) {

  def canStep:Boolean = constraints.exists(_.calculable)

  def step:Seq[Value] = {
    for {
      c <- constraints if c.calculable
      v <- c.calculate()
    } yield v
  }

  def violated:Seq[Constraint] = constraints.filter(_.failed)

}
