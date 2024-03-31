package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionShelly(
    comment: String? = null,
    val ids: String? = null,
    val turnOn: Boolean?= null
) : LMAction(comment) {

    override fun toString(): String {
        return "[LMAir] $comment: ids=$ids turnOn=$turnOn"
    }

    override fun url(): String = "/v1/scenes/json/shelly?ids=$ids&turnOn=$turnOn&"
}

