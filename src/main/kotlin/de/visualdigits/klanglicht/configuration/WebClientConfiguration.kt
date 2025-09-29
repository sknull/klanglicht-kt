package de.visualdigits.klanglicht.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class WebClientConfiguration(
    prefs: ApplicationPreferences
) {

    private var urlLightmanager: String = prefs.stage?.getService("lmair")?.url?:""

    private var urlReceiver: String = prefs.stage?.getService("receiver")?.url?:""

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
