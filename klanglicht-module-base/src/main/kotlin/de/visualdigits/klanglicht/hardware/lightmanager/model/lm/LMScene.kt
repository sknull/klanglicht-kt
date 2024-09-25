package de.visualdigits.klanglicht.hardware.lightmanager.model.lm

class LMScene(
    var name: String,
    var color: List<String> = listOf(),

    var condition: LMCondition? = null,
    var actions: List<LMAction> = listOf()
) {

    override fun toString(): String {
        return "$name: ${color.joinToString(",")}${if (condition != null) ", Condition: ${condition?.javaClass?.simpleName}" else ""}${if (actions.isNotEmpty()) "\n    - ${actions.joinToString("\n    - ")}" else ""}"
    }
}
