package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.VHtmlNode

/**
  * To play a video, we want to be able to produce an embedded player
  */
trait VideoResource {
  def embeddedPlayer(width:Int, height:Int):VHtmlNode
}

/**
  * A player for a kind of video. This allows us to treat videos as data, and we can incorporate as a video anything
  * for which we have a given player
  */
trait VideoPlayer[T]:
  extension (v:T) def embeddedPlayer(width:Int, height:Int):VHtmlNode

/**
  * To allow us to (effecitvely) store the video alongside its given player, we also define a PlayableVideo class
  * that captures these.
  */
class PlayableVideo[T : VideoPlayer](video:T) extends VideoResource {
  def embeddedPlayer(width:Int, height:Int) = video.embeddedPlayer(width:Int, height:Int)
}