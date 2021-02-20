package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.VHtmlComponent

trait VideoResource {
  def embeddedPlayer:VHtmlComponent
}

trait VideoPlayer[T] {
  def embeddedPlayer:VHtmlComponent
}

class PlayableVideo[T : VideoPlayer](video:T) extends VideoResource {
  def embeddedPlayer = summon[VideoPlayer[T]].embeddedPlayer
}