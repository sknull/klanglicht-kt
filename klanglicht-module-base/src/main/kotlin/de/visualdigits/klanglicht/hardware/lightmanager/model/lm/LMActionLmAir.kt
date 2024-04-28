package de.visualdigits.klanglicht.hardware.lightmanager.model.lm

class LMActionLmAir(
    val comment: String? = null,
    val sceneIndex: Int? = null,
) : LMAction() {

    override fun toString(): String {
        return "[LMAir] $comment: $sceneIndex"
    }

    override fun url(): String = "/v1/scenes/json/lmair?sceneIndex=$sceneIndex&"
}

