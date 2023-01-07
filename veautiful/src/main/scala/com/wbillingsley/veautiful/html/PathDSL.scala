package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.logging.Logger
import org.scalajs.dom

import scalajs.js
import org.scalajs.dom.Location

/**
  * Created by wbilling on 26/05/2017.
  */
object PathDSL {

  def location:Location = dom.window.location

  /**
    * In Hash-Based urls, eg. https://example.com/#/my/path?query=value
    * Extracts the path and search from the fragment
    */
  def hashPathAndSearch: (String, String) = splitFirst(dom.window.location.hash.drop(1), '?')

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

  /** For example in #/foo/bar?who=algernon would return Array("foo", "bar") */
  def hashPathArray():Array[String] = {
    val (path, _) = hashPathAndSearch
    pathArray(path)
  }

  /** For example in #/foo/bar?who=algernon would return List("foo", "bar") */
  def hashPathList():List[String] = hashPathArray().toList

  def hashSearchMap(): Map[String, String] = {
    val (_, search) = hashPathAndSearch
    searchMap(search)
  }

  def hashSearchMapArray(): Map[String, Array[String]] = {
    val (_, search) = hashPathAndSearch
    searchMapArray(search)
  }

  /**
    * Parses the search query into key-value pairs.
    * Not that this assumes a key is only given one value.
    */
  def searchMap(search:String = location.search): Map[String, String] = {
    val q = search.drop(1).split('&')
    q.map({ pair =>
      val (a, b) = splitFirst(pair, '=')
      js.URIUtils.decodeURI(a) -> js.URIUtils.decodeURI(b)
    }).toMap
  }

  /**
    * Parses the search query into keys paired with an array of values.
    */
  def searchMapArray(search:String = location.search): Map[String, Array[String]] = {
    val q = search.drop(1).split('&')
    val pairs = q.map { qComponent =>
      val (a, b) = splitFirst(qComponent, '=')
      js.URIUtils.decodeURI(a) -> js.URIUtils.decodeURI(b)
    }
    pairs.groupBy(_._1).map { case (k, arr) => (k, arr.map(_._2)) }
  }

  /** Attempts to parse an int from a String */
  object intParam {
    def unapply(s:String) : Option[Int] = try {
      Some(s.toInt)
    } catch {
      case _:Throwable => None
    }
  }

  /**
    * The classes and methods in this section are simple and designed just for constructing paths in a type-safe
    * manner. E.g., (/# / "courses" / course.id ? "name" -> "joe").stringify
    *
    * This can be useful as you can then just use a basic pattern match on the pathList to extract routes
    * e.g. hashPathList match {
    *   case "courses" :: id :: _ =>
    * }
    */
  object Compose {

    case class Loc(path: PathComponent = EmptyPath, search: SearchComponent = EmptySearch, fragment: String = "") {
      def ##(s: String): Loc = this.copy(fragment = s)

      def &(s: SearchComponent): Loc = this.copy(search = new &(search, s))

      def stringify: String = path.stringify + '?' + search.toString + '#' + fragment
    }

    sealed trait PathComponent {
      def /(elem: PathComponent) = new /(this, elem)

      def ?(s: SearchComponent) = Loc(this, s)

      def stringify: String
    }

    case object EmptyPath extends PathComponent {
      def stringify = ""
    }

    case object /# extends PathComponent {
      def stringify = "#"
    }

    implicit class PathString(val s: String) extends PathComponent {
      def stringify = js.URIUtils.encodeURIComponent(s)
    }

    case class /(root: PathComponent, tail: PathComponent) extends PathComponent {
      def stringify = root.stringify + "/" + tail.stringify
    }

    sealed trait SearchComponent {
      def &(elem: SearchComponent) = new &(this, elem)

      def stringify: String
    }

    case object EmptySearch extends SearchComponent {
      def stringify = ""
    }

    case class &(root: SearchComponent, tail: SearchComponent) extends SearchComponent {
      def stringify = root.stringify + "&" + tail.stringify
    }

    implicit class Pair(val kv: (String, String)) extends SearchComponent {
      def stringify = {
        val (k, v) = kv
        js.URIUtils.encodeURIComponent(k) + "=" + js.URIUtils.encodeURIComponent(v)
      }
    }

  }

  /**
    * This is an experimental DSL that allows us to create typesafe extractors and composers for paths (searches are
    * not supported yet).
    *
    * There is an oddity that because we compose the tuple types in a fairly simple manner, the path parameters are
    * reversed in the data tuple.
    */
  object Extract {

    sealed trait PathMarkers
    object stringParam extends PathMarkers
    object start extends PathMarkers

    sealed trait Path[T] {
      def unapply(o:List[String]):Option[(T, List[String])]
      def mkString(params:T):String

      def /(s:String) = PathString(s, this)
      def /(x:stringParam.type) = PathStrParam(this)
    }

    case object /# extends Path[start.type] {
      protected val logger = Logger.getLogger(this.getClass)

      def mkString(params: start.type) = "#"

      def unapply(o:List[String]) = {
        Some((start, o))
      }
    }
    case class PathString[T](s:String, tail:Path[T]) extends Path[T] {
      protected val logger = Logger.getLogger(this.getClass)

      def mkString(params:T) = tail.mkString(params) + "/" + s

      def unapply(o:List[String]) = {
        tail.unapply(o) match {
          case Some((found, remainder)) => remainder match {
            case h :: t if h == s => Some((found, t))
            case _ => None
          }
          case None => None
        }
      }
    }
    case class PathStrParam[T](tail:Path[T]) extends Path[(String, T)] {
      protected val logger = Logger.getLogger(this.getClass)

      def mkString(params:(String, T)) = {
        val (p, remainder) = params
        tail.mkString(remainder) + "/" + p
      }

      def unapply(o:List[String]) = {
        logger.info(s"Checking on $this, with ${o.toString}")
        tail.unapply(o) match {
          case Some((found, h :: t)) => Some(((h, found), t))
          case _ => None
        }
      }
    }

  }

}
