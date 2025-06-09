package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBColor

@JsonIgnoreProperties("initialize")
class LMScene(
    val name: String,
    var color: List<String> = listOf(),
    val type: LMSceneType = LMSceneType.custom,
    val steps: Int = 0, // only relevant for gradients

    val condition: LMCondition? = null,
    var actions: List<LMAction> = listOf(),

    val  initialize: Boolean = true
) {

    init {
        if (initialize) {
            when(type) {
                LMSceneType.gradient -> {
                    if (color.size >= 2) {
                        val first = RGBColor(color.first())
                        val last = RGBColor(color.last())
                        val step = 1.0 / (steps - 1)
                        val hexColors = (0 ..  steps - 1).map { f ->
                            first.fade(last, f * step, BlendMode.AVERAGE).hex()
                        }
                        color = hexColors.map { c -> "#$c" }
                        actions = listOf(LMActionHybrid(hexColors = hexColors))
                    }
                }
                LMSceneType.custom -> {
                    when (val action = actions.firstOrNull()) {
                        is LMActionHybrid -> {
                            color = action.hexColors.map { c -> "#$c" }
                        }
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return "$name [$type]: ${color.joinToString(",")}${if (condition != null) ", Condition: ${condition?.javaClass?.simpleName}" else ""}${if (actions.isNotEmpty()) "\n    - ${actions.joinToString("\n    - ")}" else ""}"
    }
}
