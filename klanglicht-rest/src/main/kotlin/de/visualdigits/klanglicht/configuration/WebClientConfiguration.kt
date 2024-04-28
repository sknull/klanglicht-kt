package de.visualdigits.klanglicht.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class WebClientConfiguration {

    @Value("\${application.services.lmair.url}")
    private var urlLightmanager: String = ""

    @Value("\${application.services.receiver.url}")
    private var urlReceiver: String = ""

    private fun webClient(baseUrl: String): WebClient {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build()
    }

    @Bean
    fun webClientLightmanager(): WebClient {
        return webClient(urlLightmanager)
    }

    @Bean
    fun webClientReceiver(): WebClient {
        return webClient(urlReceiver)
    }
}
