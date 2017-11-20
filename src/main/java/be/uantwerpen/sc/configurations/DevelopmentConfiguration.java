package be.uantwerpen.sc.configurations;

import org.h2.server.web.WebServlet;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Development Configuration
 * TODO check use
 */
@Profile("dev")
@Configuration
@Import(EmbeddedServletContainerAutoConfiguration.class)
public class DevelopmentConfiguration
{
    @Bean
    ServletRegistrationBean h2servletRegistration()
    {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/h2console/*");
        return registrationBean;
    }
}
