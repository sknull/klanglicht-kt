package de.visualdigits.klanglicht.hardware.lightmanager.model.action

class LMActionTwinkly(
    val command: String,
    val moodsIndex: Int = -2,
    val effectIndex: Int = -2
) : LMAction("Twinkly") {

    override fun toString(): String {
        return "[Twinkly] "
    }

    override fun url(): String = "/v1/scenes/json/twinkly?command=$command&moodsIndex=$moodsIndex&effectIndex=$effectIndex&"
}
