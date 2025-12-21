package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMActionYamahaAvantage(
    val command: String? = null,
    val program: String? = null,
    val enable: Boolean? = null,
) : LMAction("Yamaha Avantage") {

    override fun toString(): String {
        return "[YamahaAvantage] command=$command program=$program enable=$enable"
    }

    override fun url(): String = "/v1/scenes/json/yamahaAvantage?command=$command&program=$program&enable=$enable&"
}

