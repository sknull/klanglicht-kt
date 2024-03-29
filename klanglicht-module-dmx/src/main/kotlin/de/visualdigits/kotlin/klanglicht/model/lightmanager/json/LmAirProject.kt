package de.visualdigits.kotlin.klanglicht.model.lightmanager.json

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.io.InputStream
import java.util.SortedMap

class LmAirProject(
    val settings: Settings? = null,
    val marker: Map<String, Marker>? = null,
    val devices: Map<String, Device>? = null,
    val scenes: List<Scene> = listOf(),
    val actuators: List<Actuator> = listOf()
) {

    @JsonIgnore val scenesMap: Map<Int, Scene> = determineScenes()
    @JsonIgnore val actuatorsMap: Map<Int, Actuator> = determineActuators()

    private fun determineScenes(
        scenes: List<Scene> = this.scenes,
        scenesMap: SortedMap<Int, Scene> = sortedMapOf()
    ): Map<Int, Scene> {
        scenes.forEach { s ->
            scenesMap[s.properties?.index!!] = s
            determineScenes(s.children, scenesMap)
        }
        return scenesMap
    }

    private fun determineActuators(
        actuators: List<Actuator> = this.actuators,
        actuatorsMap: SortedMap<Int, Actuator> = sortedMapOf()
    ): Map<Int, Actuator> {
        actuators.forEach { actuator ->
            actuatorsMap[actuator.properties?.index!!] = actuator
            scenes.forEach { scene ->
                if (scene.containsActuator(actuator.properties.index!!)) {
                    actuator.usedByScenes.add(scene)
                }
            }
            determineActuators(actuator.children, actuatorsMap)
        }
        return actuatorsMap
    }

    companion object {
        private val mapper = jacksonObjectMapper()

        fun unmarshall(file: File): LmAirProject = mapper.readValue(file, LmAirProject::class.java)

        fun unmarshall(ins: InputStream): LmAirProject = mapper.readValue(ins, LmAirProject::class.java)

        fun unmarshall(s: String): LmAirProject = mapper.readValue(s, LmAirProject::class.java)

        fun unmarshall(bytes: ByteArray): LmAirProject = mapper.readValue(bytes, LmAirProject::class.java)
    }
}
