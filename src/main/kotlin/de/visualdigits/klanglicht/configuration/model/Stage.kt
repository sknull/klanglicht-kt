package de.visualdigits.klanglicht.configuration.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.klanglicht.hardware.hybrid.model.HybridDeviceType
import de.visualdigits.klanglicht.hardware.hybrid.model.HybridScene
import java.io.File

class Stage(
    val name: String = "",
    val installationLat: Double = 0.0,
    val installationLon: Double = 0.0,
    val fadeDurationDefault: Long = 2000,
    val services: List<Service> = listOf(),
    val devices: Devices? = null
) {

    companion object {

        private val mapper = jacksonMapperBuilder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

        fun readValue(file: File): Stage {
            return mapper.readValue(file, Stage::class.java)
        }
    }

    private val serviceMap: Map<String, Service> = services.associate { s -> Pair(s.name, s) }

    fun initialHybridScene(): HybridScene {
        val gains = devices?.stage?.mapNotNull { sd ->
            when (sd.type) {
                HybridDeviceType.dmx -> devices.dmx?.dmxDevices?.get(sd.id)?.gain?:1.0
                HybridDeviceType.shelly -> devices.shellyMap[sd.id]?.gain?:1.0
                HybridDeviceType.twinkly -> devices.twinklyMap[sd.id]?.let { if (it.xledArray.isLoggedIn()) it.gain else null }
                else -> null
            }
        }?:listOf()
        val ids = devices?.stage?.map { it.id } ?: listOf()
        val hybridScene = HybridScene(
            stage = this,
            ids = ids,
            hexColors = listOf("000000"),
            gains = gains,
            turnOns = "false"
        )
        return hybridScene
    }

    fun getService(name: String): Service? = serviceMap[name]
}

