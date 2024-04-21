package de.visualdigits.kotlin.klanglicht.rest.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application")
@ConfigurationPropertiesScan
class ApplicationPreferences {
    var name: String = ""
    var theme: String = ""
    var fadeDurationDefault: Long = 0
    var ownUrl: String = ""
}
