package de.visualdigits.klanglicht.hardware.lightmanager.model.action

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.klanglicht.hardware.lightmanager.model.json.deserializer.BooleanArrayDeserializer
import de.visualdigits.klanglicht.hardware.lightmanager.model.json.deserializer.LMParamsInitializer
import de.visualdigits.klanglicht.hardware.lightmanager.model.json.deserializer.NumericBooleanDeserializer
import java.io.IOException
import java.time.LocalDateTime

@JsonDeserialize(converter = LMParamsInitializer::class)
class LMParams(
    @JsonProperty("auth enabled") @JsonDeserialize(using = NumericBooleanDeserializer::class) val authEnabled: Boolean? = null,
    val time: String? = null,
    val date: String? = null,
    @JsonIgnore var dateTime: LocalDateTime? = null,
    val weekday: String? = null,
    @JsonProperty("is dst") @JsonDeserialize(using = NumericBooleanDeserializer::class)val dst: Boolean? = null,
    @JsonProperty("marker state") @JsonDeserialize(using = BooleanArrayDeserializer::class) val markerState: BooleanArray = booleanArrayOf(),
    val ssid: String? = null,
    @JsonProperty("led off") @JsonDeserialize(using = NumericBooleanDeserializer::class) val ledOff: Boolean? = null,
    @JsonProperty("last update") val lastUpdate: String? = null,
    @JsonProperty("firmware ver") val firmwareVer: String? = null,
    @JsonProperty("mac addr") val macAddr: String? = null,
    @JsonDeserialize(using = NumericBooleanDeserializer::class) val busy: Boolean? = null,
    @JsonProperty("master ip") val masterIp: String? = null,
    val lon: Double? = null,
    val lat: Double? = null,
    @JsonProperty("mode 433") @JsonDeserialize(using = NumericBooleanDeserializer::class) val mode433: Boolean? = null,
    @JsonProperty("mode 868") @JsonDeserialize(using = NumericBooleanDeserializer::class) val mode868: Boolean? = null,
    @JsonDeserialize(using = NumericBooleanDeserializer::class) val mpfs: Boolean? = null
) {

    companion object {

        private val mapper = jacksonMapperBuilder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()

        fun load(json: String): LMParams {
            val mmParams: LMParams
            mmParams = try {
                mapper.readValue(json, LMParams::class.java)
            } catch (e: IOException) {
                throw IllegalStateException("Could not unmarshall file: $json", e)
            }
            return mmParams
        }
    }
}
