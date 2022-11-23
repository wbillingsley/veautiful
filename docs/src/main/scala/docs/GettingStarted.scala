package docs

import com.wbillingsley.veautiful.html.VDomNode

def gettingStarted:VDomNode = Common.markdown(
  """
    |# Getting started
    |
    |Veautiful (the core UI toolkit), veautiful-templates (a collection of useful stuff like a router and slide decks), and 
    |doctacular (a system for publishing docs or teaching sites) are published to Maven Central.
    |
    |```
    |"com.wbillingsley" %%% "veautiful" % "0.3-M1" // just the core toolkit, latest milestone
    |"com.wbillingsley" %%% "veautiful" % "0.3-SNAPSHOT" // just the core toolkit, latest snapshot
    |```
    |
    |or
    |
    |```
    |"com.wbillingsley" %%% "doctacular" % "0.3-M1" // also includes some components and the site system, latest milestone
    |"com.wbillingsley" %%% "doctacular" % "0.3-SNAPSHOT" // also includes some components and the site system, latest snapshot
    |```
    |
    |## Here's some I made earlier
    |
    |I use Veautiful in a number of interactive open educational resources I've built
    |
    |* [Thinking about Programming](https://theintelligentbook.com/thinkingaboutprogramming) - live slides and 
    |  programmable game environments that go alongside a computational thinking course.
    |* [Circuits Up](https://theintelligentbook.com/circuitsup) - embedded circuit simulations in a little course
    |  that teaches computer architecture from electronics up.
    |* [The Adventures of Will Scala](https://theintelligentbook.com/willscala) - a simpler site (mostly video
    |  and text) that goes alongside my undergraduate Scala course.
    |* [Supercollaborative](https://theintelligentbook.com/supercollaborative) - for a software studio course, includes
    |  a git simulation built into the decks in the version control week.
    | 
    |""".stripMargin)

