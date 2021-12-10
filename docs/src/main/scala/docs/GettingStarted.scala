package docs

import com.wbillingsley.veautiful.html.VHtmlNode

def gettingStarted:VHtmlNode = Common.markdown(
  """
    |# Getting started
    |
    |Congratulations, you've made the decision to write a project using Veautiful, Doctacular, VSlides, Scatter, or
    |some of our other wonderful componentry!
    |
    |(Well, we can hope. You've read this far at least!)
    |
    |## Quickstart
    |
    |If you'd like to get started quickly, we have some starter projects up on GitHub:
    |
    |(To do: publish sample repositories)
    |
    |Or, if you're in the mood to look through something more complicated, there are some published course sites
    |that have been built with Veautiful:
    |
    |* [Thinking about Programming](https://theintelligentbook.com/thinkingaboutprogramming) - live slides and 
    |  programmable game environments that go alongside a computational thinking course.
    |* [Circuits Up](https://theintelligentbook.com/circuitsup) - embedded circuit simulations in a little course
    |  that teaches computer architecture from electronics up.
    |* [The Adventures of Will Scala](https://theintelligentbook.com/willscala) - a simpler site (mostly video
    |  and text) that goes alongside my undergraduate Scala course.
    | 
    |## Manual install
    |
    |Veautiful is published for Scala 3 (dotty), currently for version 3.0.0-M3 (the Scala 3 developer preview).
    | 
    |While Veautiful is in snapshot (probably until Scala 3 final is released), the easiest way to get it is via jitpack.
    |If you're using SBT, you can get the core library using:
    |
    |```scala
    |resolvers += "jitpack" at "https://jitpack.io"
    |libraryDependencies ++= Seq(
    |  "com.github.wbillingsley.veautiful" %%% "veautiful" % "master-SNAPSHOT",
    |)
    |```
    |
    |but you might want some of the extras too, such as the Router, Doctacular, or VSlides.
    |
    |""".stripMargin)

