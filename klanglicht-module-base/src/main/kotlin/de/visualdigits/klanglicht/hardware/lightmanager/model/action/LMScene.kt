package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMScene(
    val name: String,
    val color: List<String> = listOf(),

    val condition: LMCondition? = null,
    val actions: List<LMAction> = listOf()
) {

    override fun toString(): String {
        return "$name: ${color.joinToString(",")}${if (condition != null) ", Condition: ${condition?.javaClass?.simpleName}" else ""}${if (actions.isNotEmpty()) "\n    - ${actions.joinToString("\n    - ")}" else ""}"
    }
}
