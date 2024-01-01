package de.visualdigits.kotlin.klanglicht.rest.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RequestLoggingFilterConfig {

    @Bean
    open fun logFilter(): SimpleRequestLoggingFilter {
        val filter = SimpleRequestLoggingFilter()
        filter.setBeforeMessagePrefix("Request [")
        filter.setIncludeQueryString(true)
        filter.setIncludeClientInfo(true)
        filter.setIncludeHeaders(true)
//        filter.setIncludePayload(true);
//        filter.setMaxPayloadLength(10000);
        return filter
    }
}
