package de.visualdigits.kotlin.klanglicht.hardware.lightmanager.model.json

import com.fasterxml.jackson.annotation.JsonProperty


class ActuatorProperties(
    var ntype: NType? = null,
    var index: Int? = null,
    var system: Int? = null,
    @JsonProperty("bemerkung") val comment: String? = null,
    var ip: String? = null, // since 10.6.4
    var mac: String? = null, // since 10.6.4
    @JsonProperty("typ") var typ: String? = null, // since 10.6.4
    @JsonProperty("paramon") var paramOn: String? = null,
    @JsonProperty("paramoff") var paramOff: String? = null,
    var sequences: Int? = null,
    @JsonProperty("btnnameon") var buttonNameOn: String? = null,
    @JsonProperty("btnnameoff") var buttonNameOff: String? = null,
    @JsonProperty("deviceid") var deviceId: String? = null,
    var marker: String? = null,
    var url: String? = null,
    var url2: String? = null,
    var sunset: Boolean? = null,
    @JsonProperty("httptype") var httpType: Int? = null,
    @JsonProperty("httptype2") var httpType2: Int? = null,
    @JsonProperty("ntypenew") var ntypeNew: Int? = null // since 10.7.2
)
