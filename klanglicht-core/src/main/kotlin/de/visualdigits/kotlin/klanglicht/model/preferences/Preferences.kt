package de.visualdigits.kotlin.klanglicht.model.preferences

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.model.dmx.DmxInterface
import de.visualdigits.kotlin.klanglicht.model.dmx.DmxInterfaceDummy
import de.visualdigits.kotlin.klanglicht.model.fixture.Channel
import de.visualdigits.kotlin.klanglicht.model.fixture.Fixtures
import de.visualdigits.kotlin.klanglicht.model.hybrid.HybridDevice
import java.io.File
import java.nio.file.Paths


@JsonIgnoreProperties("klanglichtDir", "dmxInterface", "fixtures", "serviceMap", "stageMap")
data class Preferences(
    val name: String = "",
    val services: List<Service> = listOf(),
    val shelly: List<ShellyDevice> = listOf(),
    val stage: List<HybridDevice> = listOf(),
    val dmx: Dmx = Dmx()
) {

    var klanglichtDir: File = File(".")

    var dmxInterface: DmxInterface = DmxInterfaceDummy()

    /** contains the list of channels for a given base dmx channel. */
    var fixtures: Map<Int, List<Channel>> = mapOf()

    var serviceMap: Map<String, Service> = mapOf()

    var stageMap: Map<String, HybridDevice> = mapOf()

    fun init(klanglichtDir: File) {
        this.klanglichtDir = klanglichtDir

        val dmxFixtures = Fixtures.load(klanglichtDir)
        fixtures = dmx.devices.mapNotNull { stageFixture ->
            dmxFixtures.getFixture(stageFixture.manufacturer, stageFixture.model)
                ?.let { fixture ->
                    stageFixture.fixture = fixture
                    fixture.channelsForMode(stageFixture.mode).let { channels -> Pair(stageFixture.baseChannel, channels) }?:null
                }
        }.toMap()

        serviceMap = services.associateBy { it.name }

        stageMap = stage.associateBy { it.id }

        val dmxInterface = DmxInterface.load(dmx.interfaceType)
        dmxInterface.open(dmx.port)
        this.dmxInterface = dmxInterface
    }

    companion object {

        private val mapper = jacksonMapperBuilder().build()

        var preferences: Preferences? = null

        fun load(
            klanglichtDir: File,
            preferencesFileName: String = "preferences.json"
        ): Preferences {
            if (preferences == null) {
                val prefs = mapper.readValue(
                    Paths.get(klanglichtDir.canonicalPath, "preferences", preferencesFileName).toFile(),
                    Preferences::class.java
                )
                prefs.init(klanglichtDir)
                preferences = prefs
            }

            return preferences!!
        }
    }
}
