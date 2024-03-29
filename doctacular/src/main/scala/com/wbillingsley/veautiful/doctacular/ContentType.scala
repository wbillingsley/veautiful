package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.VHtmlContent

enum Medium:
  case Video[T](video: () => T)(using val player:VideoPlayer[T])
  case Deck[T](deck: () => T)(using val player:DeckPlayer[T])
  case OtherListPath[T](content: () => T)(using val player:ListPathPlayer[T])
  case Page(f: () => VHtmlContent)

/*
 * This is simplified from Impressory, where we had adjective, noun, topic
 */
case class Alternative(descriptor:String, item: Medium)