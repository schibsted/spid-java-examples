package no.spid.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * This is an example webapp where you can log in, purchase products, and log
 * out. When logged the user information from SPiD (/me) will be displayed.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class DirectPaymentExampleApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DirectPaymentExampleApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DirectPaymentExampleApplication.class, args);
    }
}
