package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.page

abstract class LMHtml {

    protected fun renderButton(value: String, url: String, color: String): String {
        val sb = StringBuilder()
        sb.append("<div class=\"button\"")
        if (color.contains(",")) {
            sb.append(" style=\"background: -moz-linear-gradient(left, $color); background: -webkit-linear-gradient(left, $color); background: linear-gradient(to right, $color);\"")
        } else {
            sb.append(" style=\"background-color: $color;\"")
        }
        sb.append("><input type=\"submit\" value=\"$value\" onclick=\"$url;\"/></div>\n")
        return sb.toString()
    }
}
