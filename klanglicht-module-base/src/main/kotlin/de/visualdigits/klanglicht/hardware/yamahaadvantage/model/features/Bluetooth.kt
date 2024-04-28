package de.visualdigits.klanglicht.hardware.yamahaadvantage.model.features


import com.fasterxml.jackson.annotation.JsonProperty

class Bluetooth(
    @JsonProperty("update_cancelable") val updateCancelable: Boolean = false,
    @JsonProperty("tx_connectivity_type_max") val txConnectivityTypeMax: Int = 0
)
