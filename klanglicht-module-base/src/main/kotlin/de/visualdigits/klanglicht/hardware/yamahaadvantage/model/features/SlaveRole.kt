package de.visualdigits.klanglicht.hardware.yamahaadvantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

class SlaveRole(
    @JsonProperty("surround_pair_l_or_r") val surroundPairLOrR: Boolean = false,
    @JsonProperty("surround_pair_lr") val surroundPairLr: Boolean = false,
    @JsonProperty("subwoofer_pair") val subwooferPair: Boolean = false
)
