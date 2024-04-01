package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionLmAir(
    val sceneIndex: Int? = null,
) : LMAction() {

    override fun toString(): String {
        return "[LMAir] $sceneIndex"
    }

    override fun url(): String = "/v1/scenes/json/lmair?sceneIndex=$sceneIndex&"
}

