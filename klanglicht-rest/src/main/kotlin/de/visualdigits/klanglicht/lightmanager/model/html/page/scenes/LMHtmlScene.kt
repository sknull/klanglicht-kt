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
        val href = "request('${prefs.baseUrl}:${prefs.port}/v1/scenes/json/control?name=${scene.name}')"
        val color = scene.color.joinToString(",")
        return LMHtmlButton(label, href, color).html(indent + 1)
    }
}
