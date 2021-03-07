package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.VHtmlNode
import com.wbillingsley.veautiful.templates.VSlides


enum Medium:
  case Video(f: () => VideoResource)
  case Deck(f: () => VSlides)
  case Page(f: () => VHtmlNode)

/*
 * This is simplified from Impressory, where we had adjective, noun, topic
 */
case class Alternative(descriptor:String, item: Medium)