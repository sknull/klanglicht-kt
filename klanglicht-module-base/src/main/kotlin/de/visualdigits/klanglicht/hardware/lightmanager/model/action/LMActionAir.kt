package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMActionAir(
    val comment: String? = null,
    val sceneIndex: Int? = null,
) : LMAction() {

    override fun toString(): String {
        return "[LMAir] $comment: $sceneIndex"
    }

    override fun url(): String = "/v1/scenes/json/lmair?sceneIndex=$sceneIndex&"
}

