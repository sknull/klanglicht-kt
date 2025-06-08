package de.visualdigits.klanglicht.lightmanager.model.html.page

class LMHtmlButton(
    val label: String,
    val href: String,
    val color: String
) {

    fun html(): String {
        val sb = StringBuilder()
        sb.append("<div class=\"button\"")
        if (color.contains(",")) {
            sb.append(" style=\"background: -moz-linear-gradient(left, $color); background: -webkit-linear-gradient(left, $color); background: linear-gradient(to right, $color);\"")
        } else {
            sb.append(" style=\"background-color: $color;\"")
        }
        sb.append("><input type=\"submit\" value=\"$label\" onclick=\"$href;\"/></div>\n")
        return sb.toString()
    }
}
