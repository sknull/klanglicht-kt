package de.visualdigits.klanglicht.hardware.yamahaadvantage.model


import com.fasterxml.jackson.annotation.JsonProperty

class SoundProgramList(
    @JsonProperty("response_code") val responseCode: Int = 0,
    @JsonProperty("sound_program_list") val soundProgramList: List<String> = listOf()
)
