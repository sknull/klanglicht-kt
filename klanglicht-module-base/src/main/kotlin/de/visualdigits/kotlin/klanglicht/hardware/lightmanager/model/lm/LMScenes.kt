package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

import java.util.Locale

class LMScenes(
    val name: String? = null
) {

    val scenes: MutableMap<String, MutableList<LMScene>> = mutableMapOf()

    override fun toString(): String {
        return "$name\n" + scenes.toMap().map { e -> "  ${e.key}\n    ${e.value.joinToString("\n    ")}" }.joinToString("\n")
    }

    fun add(scene: LMScene) {
        var group: String? = "common"
        val attributes = LMNamedAttributes(scene.name, "group", "color")
        if (attributes.matched()) {
            val name = attributes.name
            if (name?.isNotEmpty() == true) {
                scene.name = name
            }
            val g = attributes["group"]
            if (g.isNotEmpty()) {
                group = g
            }
            scene.color = attributes["color"]
        }
        if ("hidden" != group) {
            group
                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                ?.let { name ->
                    var s = scenes[name]
                    if (s == null) {
                        s = mutableListOf()
                        scenes[name] = s
                    }
                    s.add(scene)
                }
        }
    }
}
