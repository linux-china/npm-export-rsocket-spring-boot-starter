package org.mvnsearch.boot.npm.export.rsocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * npm rsocket export auto configuration
 *
 * @author linux_china
 */
@Configuration
public class NpmRSocketExportAutoConfiguration {

    @Bean
    public NpmRSocketExportController npmExportController() {
        return new NpmRSocketExportController();
    }
}
