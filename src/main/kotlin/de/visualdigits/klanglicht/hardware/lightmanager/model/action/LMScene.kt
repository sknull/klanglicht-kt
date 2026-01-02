package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.visualdigits.kotlin.twinkly.model.color.BlendMode
import de.visualdigits.kotlin.twinkly.model.color.RGBColor

@JsonIgnoreProperties("initialize", "group")
class LMScene(
    val name: String,
    val type: LMSceneType = LMSceneType.standard,
    var color: List<String>? = listOf(),
//    var factor: Double = 1.0,
    val steps: Int = 0, // only relevant for gradients

    val repeatable: Boolean? = true,
    val condition: LMCondition? = null,
    var actions: List<LMAction> = listOf(),

    val initialize: Boolean? = true
) {

    var group: LMSceneGroup? = null

    init {
        if (initialize == true) {
            val actionHybrid = actions
                .filterIsInstance<LMActionHybrid>()
                .firstOrNull()
            when (type) {
                LMSceneType.standard -> {
                    actions
                        .filterIsInstance<LMActionHybrid>()
                        .forEach { actionHybrid ->
                            if (actionHybrid.factor != 1.0) {
                                actionHybrid.originalHexColors = actionHybrid.hexColors
                                actionHybrid.hexColors = actionHybrid.hexColors.map { hc -> RGBColor(hc).multiply(actionHybrid.factor).web() }
                            }
                        }
                    color = if(color == null || color?.isEmpty() == true) {
                        actionHybrid?.hexColors ?: listOf()
                    } else {
                        color
                    }
                }
                LMSceneType.gradient -> {
                    val first = RGBColor(actionHybrid?.hexColors?.first()?:"#000000").multiply(actionHybrid?.factor?:1.0)
                    val last = RGBColor(actionHybrid?.hexColors?.last()?:"#000000").multiply(actionHybrid?.factor?:1.0)
                    val step = 1.0 / (steps - 1)
                    val hexColors = (0..<steps).map { f ->
                        first.fade(last, f * step, BlendMode.AVERAGE).web()
                    }
                    actionHybrid?.originalHexColors = actionHybrid.hexColors
                    actionHybrid?.hexColors = hexColors
                    color = hexColors
                }
            }
        }
    }

    override fun toString(): String {
        return "$name [$type]: ${color?.joinToString(",")}${condition?.let { c -> ", Condition: ${c.javaClass.simpleName}" }?: "" }${if (actions.isNotEmpty()) "\n    - ${actions.joinToString("\n    - ")}" else ""}"
    }
}
