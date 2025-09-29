package de.visualdigits.klanglicht.resources.controller

import de.visualdigits.klanglicht.resources.service.ResourceService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ResourceController(
    val resourceService: ResourceService
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/resources/**")
    fun resource(request: HttpServletRequest, response: HttpServletResponse) {
        return resourceService.resource(request, response)
    }
}
