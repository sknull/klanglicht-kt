package de.visualdigits.kotlin.klanglicht.rest.lightmanager.service

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import de.visualdigits.kotlin.klanglicht.rest.lightmanager.model.html.LMHtmlZones
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@Disabled("only for local testing")
@ExtendWith(SpringExtension::class)
@SpringBootTest
class LightmanagerServiceTest @Autowired constructor(
    private val lightmanagerService: LightmanagerService
) {

     @Test
     fun testLightmanagerService() {
         val zones = lightmanagerService.zones()
         println(jacksonMapperBuilder().enable(SerializationFeature.INDENT_OUTPUT).build().writeValueAsString(zones))
     }
}
