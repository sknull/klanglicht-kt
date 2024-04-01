package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionLmYamahaAvantage(
    val command: String? = null,
    val program: String? = null,
) : LMAction() {

    override fun toString(): String {
        return "[YamahaAvantage] command=$command program=$program"
    }

    override fun url(): String = "/v1/scenes/json/yamahaAvantage?command=$command&program=$program&"
}

