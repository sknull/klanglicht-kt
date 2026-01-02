package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties("originalHexColors")
class LMActionHybrid(
    val ids: List<String> = listOf(),
    var hexColors: List<String> = listOf(),
    var factor: Double = 1.0,
    var originalHexColors: List<String>? = null,
    val gains: List<Double> = listOf(),
) : LMAction("Hybrid") {

    override fun toString(): String {
        return "[Hybrid] ids=$ids hexColors=$hexColors"
    }

    override fun url(): String = "/v1/scenes/json/hybrid?ids=$ids&hexColors=$hexColors&gains=$gains&"
}

