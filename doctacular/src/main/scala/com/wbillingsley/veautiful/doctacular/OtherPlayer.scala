package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.VDomContent


/**
  * A ListPathResource is one whose subpaths can be fully described using a list of strings `/x/y/z`
  * and therefore only needs the location's path list, not its hash map of search elements.
  */
trait ListPathResource {
    // We need to at least be able to put the deck on the screen
    // The deck name is passed through because the player might wish to integrate controls that do navigation
    // (which requires the deck name for the route)
    def view(name:String, subpath:List[String]):VDomContent

    /** A string to include in the path element, in place of "videos", "pages" */
    def kind:String    

    def defaultSubpath:List[String]
}

/**
  * A player for a kind of video. This allows us to treat videos as data, and we can incorporate as a video anything
  * for which we have a given player
  */
trait ListPathPlayer[T]:
  extension (v:T) {
    /** A string to include in the path element, in place of "videos", "pages" */
    def kind:String    

    // We need to at least be able to put the content on the screen
    // The name is passed through because the player might wish to integrate controls that do navigation
    // (which requires the deck name for the route)
    def view(name:String, subpath:List[String]):VDomContent

    def defaultSubpath:List[String]

  }

/**
  * To allow us to (effecitvely) store the video alongside its given player, we also define a PlayableVideo class
  * that captures these.
  */
class PlayableListPathResource[T : ListPathPlayer](content:T) extends ListPathResource {
    def view(name:String, subpath:List[String]) = content.view(name, subpath)

    def kind = content.kind

    def defaultSubpath:List[String] = content.defaultSubpath
}