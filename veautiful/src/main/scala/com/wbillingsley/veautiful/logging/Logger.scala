package com.wbillingsley.veautiful.logging

import scala.scalajs.js.Date
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

class Logger(val name:String) {

  def trace(msg: => String):Unit = Logger.log(this, Logger.Trace, msg)
  def debug(msg: => String):Unit = Logger.log(this, Logger.Debug, msg)
  def info(msg: => String):Unit = Logger.log(this, Logger.Info, msg)
  def warn(msg: => String):Unit = Logger.log(this, Logger.Warning, msg)
  def error(msg: => String):Unit = Logger.log(this, Logger.Error, msg)

}

@JSExportTopLevel("Logger")
object Logger {

  def getLogger[T](c:Class[T]) = new Logger(c.getName)

  sealed trait Level extends Ordered[Level] {
    val num:Int
    def compare(other:Level):Int = this.num - other.num
  }

  case object Trace extends Level { val num = 1 }
  case object Debug extends Level { val num = 2 }
  case object Info extends Level { val num = 3 }
  case object Warning extends Level { val num = 4 }
  case object Error extends Level { val num = 5 }

  var currentLevel:Level = Info

  @JSExport
  def setTrace():Unit = currentLevel = Trace

  @JSExport
  def setDebug():Unit = currentLevel = Debug

  @JSExport
  def setInfo():Unit = currentLevel = Info

  @JSExport
  def setWarn():Unit = currentLevel = Warning

  @JSExport
  def setError():Unit = currentLevel = Error

  def log(logger:Logger, level:Level, msg: => String): Unit = {
    if (level >= currentLevel) {
      val d = new Date().toLocaleTimeString()
      println(s"$level ${logger.name} $d: $msg")
    }
  }

}
