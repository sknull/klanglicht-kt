package de.visualdigits.klanglicht.hardware.lightmanager.model.json.deserializer

import com.fasterxml.jackson.databind.util.StdConverter
import de.visualdigits.klanglicht.hardware.lightmanager.model.lm.LMParams
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LMParamsInitializer : StdConverter<LMParams, LMParams>() {

    override fun convert(params: LMParams): LMParams {
        params.dateTime = LocalDateTime.parse(
                (params.date + LocalDateTime.now().year).toString() + " " + params.time,
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            )
        return params
    }
}
