package de.visualdigits.klanglicht.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RequestLoggingFilterConfig {

    @Bean
    fun logFilter(): SimpleRequestLoggingFilter {
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
