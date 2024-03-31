package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionHybrid(
    comment: String? = null,
    val ids: String? = null,
    val hexColors: String? = null,
    val gains: String? = null,
) : LMAction(comment) {

    override fun toString(): String {
        return "[LMAir] $comment: ids=$ids hexColors=$hexColors"
    }

    override fun url(): String = "/v1/scenes/json/hybrid?ids=$ids&hexColors=$hexColors&gains=$gains&"
}

