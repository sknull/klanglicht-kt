package de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html

import de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm.LMScene
import de.visualdigits.kotlin.klanglicht.rest.configuration.ConfigHolder


class LMHtmlScene(
    val scene: LMScene
) : HtmlRenderable {

    override fun toHtml(configHolder: ConfigHolder): String {
        return toHtml(configHolder, "")
    }

    fun toHtml(configHolder: ConfigHolder, group: String): String {
//        val url = configHolder.preferences?.getService("lmair")?.url
        val url = configHolder.preferences?.ownUrl
        val sb = StringBuilder()
        sb.append("      <div class=\"button\"")
        if (scene.color?.isNotEmpty() == true) {
            if (scene.color?.contains(",") == true) {
                sb.append(" style=\"background: -moz-linear-gradient(left, ")
                    .append(scene.color)
                    .append("); background: -webkit-linear-gradient(left, ")
                    .append(scene.color)
                    .append("); background: linear-gradient(to right, ")
                    .append(scene.color)
                    .append(");\"")
            }
            else {
                sb.append(" style=\"background-color: ")
                    .append(scene.color)
                    .append(";\"")
            }
        }
        val label = normalizeLabel(group)
        sb.append("><input type=\"submit\" value=\"")
            .append(label)
            .append("\" onclick=\"request('")
            .append(url)
            .append("/v1/scenes/json/control?name=")
//            .append("/control?scene=")
            .append(scene.name)
            .append("');\"/></div>\n")
        return sb.toString()
    }

    /**
     * Removes the group name from the label (if matches).
     *
     * @param group The current group this scene belongs to.
     *
     * @return String
     */
    private fun normalizeLabel(group: String): String {
        var label = scene.name
        if (label?.lowercase()?.startsWith(group.lowercase()) == true) {
            label = label.substring(group.length).trim { it <= ' ' }
        }
        return label?:""
    }
}
