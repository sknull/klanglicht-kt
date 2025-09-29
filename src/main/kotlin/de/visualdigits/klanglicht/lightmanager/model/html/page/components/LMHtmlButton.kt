package de.visualdigits.klanglicht.lightmanager.model.html.page.components

class LMHtmlButton(
    val label: String,
    val href: String,
    val color: String,
    val square: Boolean = false
): LMHtml {

    override fun html(indent: Int): String {
        val sindent = "  ".repeat(indent)
        val sb = StringBuilder()
        sb.append("$sindent<div class=\"button${if(square) " square" else ""}\"")
        if (color.contains(",")) {
            sb.append(" style=\"background: -moz-linear-gradient(left, $color); background: -webkit-linear-gradient(left, $color); background: linear-gradient(to right, $color);\"")
        } else {
            sb.append(" style=\"background-color: $color;\"")
        }
        sb.append("><input type=\"submit\" value=\"$label\" onclick=\"$href;\"/></div>\n")
        return sb.toString()
    }
}
