"use strict";

/*
 * An example of using VDeck from JavaScript
 */
console.debug("Loaded vdeckExample.js!");

(function() {

  let deck = new DeckBuilder(1280, 720)
      .markdownSlide(`
        # V-Deck from JavaScript
        
        This is a slide deck loaded from JavaScript
      `).withClass("center middle")
      .markdownSlides(`
      # Creating a deck
      
      Decks are created using a \`DeckBuilder\`. This gives us an easy object to call functions on to add slides.
      For example
      
      ~~~js
      let deck = new DeckBuilder(1280, 720)
      .markdownSlide(\`
        # V-Deck from JavaScript
        
        This is a slide deck loaded from JavaScript
      \`).withClass("center middle")
      ~~~
      
      ---
      
      ## Rendering the deck
      
      Once we've built a deck, we render it into a selected node on the page using a CSS selector, normally on
      the element's id:
      
      ~~~scala
      deck.render("#vdeck-render")
      ~~~
      
      ---
      
      ## Embedding inside Veautiful
      
      When loading from JavaScript, we use a separate \`root node\`. The parent node inserts a \`div\` for the deck
      to render into, and a \`script\` element to load the deck. For example, the one in this page is loaded using:
      
      ~~~scala
      <.div(^.key := "vslide-example1", ^.cls := "resizable",
        <.div(^.attr("id") := "vdeck-render"),
        <("script")(^.attr("src") := "vdeckExample.js")
      )
      ~~~
      
      In this case, it's important we give the surrounding \`div\` a \`key\` so that the node will be replaced rather
      than re-used if we move to a page that has a different \`div\`.
      `)

  deck.render("#vdeck-render")
})()

