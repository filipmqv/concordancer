object Concordancer {

  val replaceNewLineChars : String => String = (text) =>
    text.replaceAll("""\n""", " ")

  val removePagesTags : String => String = (text) =>
    text.replaceAll("""<span class="pagenum"[^>]*>((?!<\/span).)*<\/span>""", "")

  // extract all <p> ... </p>
  val extractParagraphs : String => String = (text) => {
    val paragraphRegex = "<p[^>]*>((?!<\\/p).)*<\\/p>".r
    paragraphRegex.findAllIn(text).mkString(" ")
  }

  val removeTagsInParagraphs : String => String = (text) =>
    text.replaceAll("""<(?!\/?(?=>|\s.*>))\/?.*?>""", "")

  val removeMultipleWhiteChars : String => String = (text) =>
    text.replaceAll("""\s{2,}""", " ")

  val prepareBook : String => String = replaceNewLineChars andThen removePagesTags andThen extractParagraphs andThen
    removeTagsInParagraphs andThen removeMultipleWhiteChars

  def findConcordances(searchedWord: String, text: String, neighbours: Int = 5): List[String] = {
    val concordanceRegex = ("""(?:\S+\s+){0,""" + neighbours + """}(\S)*\b(?:""" +
      searchedWord + """)\b(\S)*(?:\s+\S+){0,""" + neighbours + """}""").r
    concordanceRegex.findAllIn(text).toList
  }
}
