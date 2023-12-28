package de.visualdigits.kotlin.klanglicht.rest.common.configuration

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HttpsConfiguration {

    @Bean
    fun servletContainer(@Value("\${server.http.port}") httpPort: Int): ServletWebServerFactory {
        val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
        connector.port = httpPort
        val tomcat = TomcatServletWebServerFactory()
        tomcat.addAdditionalTomcatConnectors(connector)
        return tomcat
    } //    @Bean
    //    public ServletWebServerFactory servletContainer() {
    //        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
    //
    //            @Override
    //            protected void postProcessContext(Context context) {
    //                SecurityConstraint securityConstraint = new SecurityConstraint();
    //                securityConstraint.UserConstraint = "CONFIDENTIAL";
    //                SecurityCollection collection = new SecurityCollection();
    //                collection.addPattern("/*");
    //                securityConstraint.addCollection(collection);
    //                context.addConstraint(securityConstraint);
    //            }
    //        };
    //        tomcat.addAdditionalTomcatConnectors(redirectConnector());
    //        return tomcat;
    //    }
    //
    //    private Connector redirectConnector() {
    //        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    //        connector.Scheme = "http";
    //        connector.Port = 80;
    //        connector.Secure = false;
    //        connector.RedirectPort = 443;
    //        return connector;
    //    }
}
