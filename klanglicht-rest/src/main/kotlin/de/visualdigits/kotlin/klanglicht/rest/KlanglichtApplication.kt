package de.visualdigits.kotlin.klanglicht.rest.kotlin.klanglicht.rest

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class])
object KlanglichtApplication {

    private var context: ConfigurableApplicationContext? = null

    @JvmStatic
    fun main(args: Array<String>) {
        context = SpringApplication.run(KlanglichtApplication::class.java, *args)
    }

    fun restart() {
        val args = context!!.getBean(
            ApplicationArguments::class.java
        )
        val thread = Thread {
            context!!.close()
            context = SpringApplication.run(KlanglichtApplication::class.java, *args.sourceArgs)
        }
        thread.isDaemon = false
        thread.start()
    }
}
