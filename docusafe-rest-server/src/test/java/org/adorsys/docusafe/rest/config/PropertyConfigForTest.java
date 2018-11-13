package org.adorsys.docusafe.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by peter on 13.11.18 15:24.
 */
@Configuration
public class PropertyConfigForTest {
    @Bean
    public String basedir(FileProperty properties) {
        return properties.getBaseDir();
    }


}
