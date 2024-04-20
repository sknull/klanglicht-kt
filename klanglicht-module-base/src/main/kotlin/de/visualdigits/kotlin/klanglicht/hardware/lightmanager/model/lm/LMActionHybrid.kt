package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.lm

class LMActionHybrid(
    val ids: List<String> = listOf(),
    val hexColors: List<String> = listOf(),
    val gains: List<Double> = listOf(),
) : LMAction() {

    override fun toString(): String {
        return "[Hybrid] ids=$ids hexColors=$hexColors"
    }

    override fun url(): String = "/v1/scenes/json/hybrid?ids=$ids&hexColors=$hexColors&gains=$gains&"
}

