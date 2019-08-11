package com.wbillingsley.veautiful

import org.scalajs.dom
import scalajs.js
import org.scalajs.dom.Location

/**
  * Created by wbilling on 26/05/2017.
  */
object PathDSL {

  def location:Location = dom.window.location

  /**
    * Splits a string on the first occurance of a character
    * e.g., splitFirst("query=value", '=') produces ("query", "value")
    */
  def splitFirst(s:String, c:Char): (String, String) = {
    val (a, b) = s.span(_ != c)
    a -> b.drop(1)
  }

  /**
    * Decomposes a path into an array of strings by splitting on '/'
    *
    * This allows matching on the path, e.g.
    *
    * pathArray match {
    *   case Array("customer", id) => Route.Customer(id)
    * }
    */
  def pathArray(pathname:String = location.pathname): Array[String] = {
    pathname.drop(1).split('/').map(js.URIUtils.decodeURI)
  }

  /**
    * Parses the search query into key-value pairs.
    * @param search
    * @return
    */
  def searchMap(search:String = location.search): Map[String, String] = {
    val q = search.drop(1).split('&')
    q.map({ pair =>
      val (a, b) = splitFirst(pair, '=')
      js.URIUtils.decodeURI(a) -> js.URIUtils.decodeURI(b)
    }).toMap
  }

  def searchMapArray(search:String = location.search): Map[String, Array[String]] = {
    val q = search.drop(1).split('&')
    val pairs = q.map { qComponent =>
      val (a, b) = splitFirst(qComponent, '=')
      js.URIUtils.decodeURI(a) -> js.URIUtils.decodeURI(b)
    }
    pairs.groupBy(_._1).mapValues(_.map(_._2))
  }

  case class Loc(path:PathComponent = EmptyPath, search:SearchComponent = EmptySearch, fragment:String = "") {

    def ##(s:String):Loc = this.copy(fragment=s)
    def &(s:SearchComponent):Loc = this.copy(search = new &(search, s))

    def stringify:String = path.stringify + '?' + search.toString + '#' + fragment

  }

  sealed trait PathComponent {
    def /(elem:PathComponent) = new /(this, elem)
    def ?(s:SearchComponent) = Loc(this, s)

    def stringify:String
  }

  case object EmptyPath extends PathComponent {
    def stringify = ""
  }

  implicit class PathString(val s:String) extends PathComponent {
    def stringify = js.URIUtils.encodeURIComponent(s)
  }

  case class /(root:PathComponent, tail:PathComponent) extends PathComponent {
    def stringify = root.stringify + "/" + tail.stringify
  }

  sealed trait SearchComponent {
    def &(elem:SearchComponent) = new &(this, elem)
    def stringify:String
  }
  case object EmptySearch extends SearchComponent {
    def stringify = ""
  }
  case class &(root:SearchComponent, tail:SearchComponent) extends SearchComponent {
    def stringify = root.stringify + "&" + tail.stringify
  }
  implicit class Pair(val kv:(String, String)) extends SearchComponent {
    def stringify = {
      val (k, v) = kv
      js.URIUtils.encodeURIComponent(k) + "=" + js.URIUtils.encodeURIComponent(v)
    }
  }

}
