package com.wbillingsley.veautiful

import org.scalatest.flatspec.AnyFlatSpec
import PathDSL._
import Extract._


class PathDSLTest extends AnyFlatSpec  {

  "Path DSL" should "match a lone string" in {
    val location = "/hello"
    val pl = pathArray(location).toList
    val path = /# / "hello"

    val result = pl match {
      case path((start, remainder)) => "Matched"
    }

    assert(result == "Matched")
  }

  it should "extract a single string parameter after a path" in {
    val pathList = pathArray("/hello/world").toList

    val path = (/# / "hello" / stringParam)

    val result = pathList match {
      case path(((name, start), remainder)) => name
    }

    assert(result == "world")

  }

  it should "make a path using a single string parameter" in {
    val path = (/# / "hello" / stringParam)
    assert(path.mkString(("world", start)) == "#/hello/world")
  }

}
