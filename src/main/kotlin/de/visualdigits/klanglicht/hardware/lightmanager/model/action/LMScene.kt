package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBColor
import org.jetbrains.kotlin.util.prefixIfNot

@JsonIgnoreProperties("initialize", "groupName")
class LMScene(
    val name: String,
    val type: LMSceneType = LMSceneType.standard,
    var color: List<String> = listOf(),
    var factor: Double = 1.0,
    val steps: Int = 0, // only relevant for gradients

    val repeatable: Boolean = true,
    val condition: LMCondition? = null,
    var actions: List<LMAction> = listOf(),

    val initialize: Boolean = true
) {

    var group: LMSceneGroup? = null

    init {
        if (initialize) {
            val actionHybrid = actions
                .filterIsInstance<LMActionHybrid>()
                .firstOrNull()
            if (actionHybrid != null) {
                val colors = if (factor != 1.0) {
                    val hexColors = actionHybrid.hexColors.map { hc -> RGBColor(hc).multiply(factor).web() }
                    actions = listOf(LMActionHybrid(ids = actionHybrid.ids, originalHexColors = actionHybrid.hexColors, hexColors = hexColors, gains = actionHybrid.gains))
                    hexColors
                } else {
                    actionHybrid.hexColors.map { hc -> hc.prefixIfNot("#") }
                }
                if (type == LMSceneType.gradient && colors.size >= 2) {
                    val first = RGBColor(colors.first())
                    val last = RGBColor(colors.last())
                    val step = 1.0 / (steps - 1)
                    val hexColors = (0..<steps).map { f ->
                        first.fade(last, f * step, BlendMode.AVERAGE).web()
                    }
                    color = hexColors
                    actions = listOf(LMActionHybrid(ids = actionHybrid.ids, originalHexColors = actionHybrid.hexColors, hexColors = hexColors, gains = actionHybrid.gains))
                } else if (color.isEmpty()) {
                    color = colors
                }
            }
        }
    }

    override fun toString(): String {
        return "$name [$type]: ${color.joinToString(",")}${condition?.let { c -> ", Condition: ${c.javaClass.simpleName}" }?: "" }${if (actions.isNotEmpty()) "\n    - ${actions.joinToString("\n    - ")}" else ""}"
    }
}
