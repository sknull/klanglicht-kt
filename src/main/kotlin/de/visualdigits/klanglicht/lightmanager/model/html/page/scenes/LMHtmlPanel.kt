package de.visualdigits.klanglicht.lightmanager.model.html.page.scenes

import de.visualdigits.klanglicht.lightmanager.model.html.page.components.LMHtml

class LMHtmlPanel(
    val bgColor: String
): LMHtml {

    override fun html(indent: Int): String {
        val sindent = "  ".repeat(indent)
        return "$sindent<div class=\"circle\" style=\"background-color:$bgColor\"></div> <!-- circle -->\n"
    }
}
