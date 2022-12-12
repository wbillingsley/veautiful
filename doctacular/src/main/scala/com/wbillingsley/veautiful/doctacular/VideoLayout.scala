package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.VDomContent

class VideoLayout(site:Site) {

  var videoDimensions:(Int, Int) = (640, 480)
  
  def renderVideo(site:Site, name:String, video:VideoResource):VDomContent = {
    val (w, h) = videoDimensions
    site.renderPage(video.embeddedPlayer(w, h))
  }


}
