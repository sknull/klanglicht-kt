package de.visualdigits.klanglicht.lightmanager.model.html.page.scenes

import de.visualdigits.klanglicht.configuration.ApplicationPreferences
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMScene
import de.visualdigits.klanglicht.hardware.lightmanager.model.action.LMSceneGroup
import de.visualdigits.klanglicht.lightmanager.model.html.page.components.LMHtml
import de.visualdigits.klanglicht.lightmanager.model.html.page.components.LMHtmlButton

class LMHtmlScene(
    val prefs: ApplicationPreferences,
    val sceneGroup: LMSceneGroup,
    val scene: LMScene
) : LMHtml {

    override fun html(indent: Int): String {
        val label = if (scene.name.lowercase().startsWith(sceneGroup.name.lowercase())) {
            scene.name.substring(sceneGroup.name.length).trim { it <= ' ' }
        } else scene.name
        var html = LMHtmlButton(
            label = label,
            href = "request('${prefs.baseUrl}:${prefs.port}/v1/scenes/json/control?name=${scene.name}')",
            color = scene.color.joinToString(",")
        ).html(indent + 1)
        if (sceneGroup.name == "Custom") {
            html += LMHtmlButton(
                label = "DEL",
                href = "request('${prefs.baseUrl}:${prefs.port}/v1/scenes/json/delete?name=${scene.name}','DELETE')",
                color = "",
                square = true
            ).html(indent + 1)
        }
        return html
    }
}
