package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.VHtmlNode

/**
  * To show a deck, we want to be able to produce widgets for when the deck is played
  */
trait DeckResource {
    // We need to at least be able to put the deck on the screen
    // The deck name is passed through because the player might wish to integrate controls that do navigation
    // (which requires the deck name for the route)
    def defaultView(deckName:String):VHtmlNode

    // Optionally, we might be able to directly play the deck full-screen
    def fullScreenPlayer: Option[(String, Int) => VHtmlNode]
}

/**
  * A player for a kind of video. This allows us to treat videos as data, and we can incorporate as a video anything
  * for which we have a given player
  */
trait DeckPlayer[T]:
  extension (v:T) {
    // We need to at least be able to put the deck on the screen
    // The deck name is passed through because the player might wish to integrate controls that do navigation
    // (which requires the deck name for the route)
    def defaultView(deckName:String):VHtmlNode

    // Optionally, we might be able to directly play the deck full-screen
    def fullScreenPlayer: Option[(String, Int) => VHtmlNode]
  }

/**
  * To allow us to (effecitvely) store the video alongside its given player, we also define a PlayableVideo class
  * that captures these.
  */
class PlayableDeck[T : DeckPlayer](deck:T) extends DeckResource {
    // We need to at least be able to put the deck on the screen
    def defaultView(name:String) = deck.defaultView(name)

    // Optionally, we might be able to directly play the deck full-screen
    def fullScreenPlayer = deck.fullScreenPlayer
}