package de.visualdigits.kotlin.klanglicht.rest.hybrid.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.json.JsonMapper
import java.io.File
import java.io.IOException
import java.nio.file.Paths

class HybridStage(
    val name: String? = null,
    val devices: List<Device> = listOf()
) {

    @JsonIgnore
    val devicesById: MutableMap<String, Device> = HashMap()
    
    fun getDeviceById(id: String): Device? {
        return devicesById[id]
    }

    private fun intitialize() {
        for (device in devices!!) {
            devicesById[device.id!!] = device
        }
    }

    companion object {
        val MAPPER: JsonMapper = JsonMapper()
        protected var theOne: HybridStage? = null
        @Synchronized
        fun load(klanglichtDir: File, stageSetupName: String): HybridStage? {
            if (theOne == null) {
                val preferencesFile: File =
                    Paths.get(klanglichtDir.absolutePath, "hybrid", "$stageSetupName.json").toFile() 
                try {
                    theOne = MAPPER.readValue<HybridStage>(preferencesFile, HybridStage::class.java)
                    theOne!!.intitialize()
                } catch (e: IOException) {
                    throw IllegalStateException("Could not read hybrid stage file: $preferencesFile", e)
                }
            }
            return theOne
        }

        fun instance(): HybridStage? {
            return theOne
        }
    }
}
