package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionLmAir(
    comment: String? = null,
    val sceneIndex: Int? = null,
) : LMAction(comment) {

    override fun toString(): String {
        return "[LMAir] $comment: $sceneIndex"
    }

    override fun url(): String = "/v1/scenes/json/lmair?sceneIndex=$sceneIndex&"
}

