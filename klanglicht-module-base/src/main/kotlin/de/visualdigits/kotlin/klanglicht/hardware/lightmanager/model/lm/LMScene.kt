package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMScene(
    var name: String? = null,
    var color: String? = null,

    var actions: List<LMAction> = listOf()
) {

    override fun toString(): String {
        return "$name: $color${if (actions.isNotEmpty()) "\n    - ${actions.joinToString("\n    - ")}" else ""}"
    }
}
