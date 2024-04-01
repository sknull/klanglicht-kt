package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionHybrid(
    val ids: String? = null,
    val hexColors: String? = null,
    val gains: String? = null,
) : LMAction() {

    override fun toString(): String {
        return "[Hybrid] ids=$ids hexColors=$hexColors"
    }

    override fun url(): String = "/v1/scenes/json/hybrid?ids=$ids&hexColors=$hexColors&gains=$gains&"
}

