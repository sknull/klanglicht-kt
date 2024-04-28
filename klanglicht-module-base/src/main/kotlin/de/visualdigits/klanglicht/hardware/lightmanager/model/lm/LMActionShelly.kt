package de.visualdigits.klanglicht.hardware.lightmanager.model.lm

class LMActionShelly(
    val ids: List<String> = listOf(),
    val turnOn: Boolean?= null
) : LMAction() {

    override fun toString(): String {
        return "[Shelly] ids=$ids turnOn=$turnOn"
    }

    override fun url(): String = "/v1/scenes/json/shelly?ids=$ids&turnOn=$turnOn&"
}

