package com.wbillingsley.veautiful

import PathDSL._
import Extract._

class PathDSLSuite extends munit.FunSuite {

  test("Path DSL should match a lone string") {
    val location = "/hello"
    val pl = pathArray(location).toList
    val path = /# / "hello"

    val result = pl match {
      case path((start, remainder)) => "Matched"
    }

    assertEquals(result, "Matched")
  }

  test("PashDSL should extract a single string parameter after a path") {
    val pathList = pathArray("/hello/world").toList

    val path = (/# / "hello" / stringParam)

    val result = pathList match {
      case path(((name, start), remainder)) => name
    }

    assertEquals(result, "world")
  }

  test ("PathDSL should make a path using a single string parameter") {
    val path = (/# / "hello" / stringParam)
    assertEquals(path.mkString(("world", start)), "#/hello/world")
  }

}
