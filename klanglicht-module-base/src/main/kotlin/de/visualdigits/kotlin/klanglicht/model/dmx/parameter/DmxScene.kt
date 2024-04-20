package de.visualdigits.kotlin.klanglicht.model.dmx.parameter

import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.model.preferences.Preferences
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class DmxScene(
    val name: String,
    val parameterSet: List<ParameterSet>
) : Fadeable<DmxScene> {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private val mapper = jacksonMapperBuilder().build()

        fun load(sceneFile: File): DmxScene {
            return mapper.readValue(sceneFile, DmxScene::class.java)
        }
    }

    override fun toString(): String {
        return name
    }

    /**
     * Writes the given scene into the internal dmx frame of the interface.
     */
    override fun write(preferences: Preferences?, write: Boolean, transitionDuration: Long) {
        // first collect all frame data for the dmx frame to avoid lots of costly write operations to a serial interface
        parameterSet
            .sortedBy { it.baseChannel }
            .forEach { parameterSet ->
                val baseChannel = parameterSet.baseChannel
                val bytes = (preferences?.getDmxFixture(baseChannel)?.map { channel ->
                    (parameterSet.parameterMap[channel.name] ?: 0).toByte()
                } ?: listOf()).toByteArray()
                preferences?.setDmxData(baseChannel, bytes)
            }
        if (write) {
            log.debug("Writing dmx scene {}", this)
            preferences?.writeDmxData()
        }
    }

    override fun clone(): DmxScene {
        return DmxScene(name, parameterSet.map { it.clone() })
    }

    override fun fade(
        other: Any,
        factor: Double
    ): DmxScene {
        return if (other is DmxScene) {
            DmxScene(
                name = "Frame $factor",
                parameterSet = parameterSet
                    .sortedBy { it.baseChannel }
                    .zip(other.parameterSet.sortedBy { it.baseChannel })
                    .map { (paramsFrom, paramsTo) ->
                        paramsFrom.fade(paramsTo, factor)
                    }
            )
        } else throw IllegalArgumentException("Cannot not fade another type")
    }
}
