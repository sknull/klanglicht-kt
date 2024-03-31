package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionLmYamahaAvantage(
    comment: String? = null,
    val command: String? = null,
    val program: String? = null,
) : LMAction(comment) {

    override fun toString(): String {
        return "[YamahaAvantage] $comment: command=$command program=$program"
    }

    override fun url(): String = "/v1/scenes/json/yamahaAvantage?command=$command&program=$program&"
}

