package org.adorsys.docusafe.rest.types;

import org.adorsys.docusafe.rest.adapter.GsonAdapters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * Created by peter on 26.02.19 18:58.
 */
/*
@Configuration
public class GsonSwaggerConfig {

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(GsonAdapters.gson());
        return converter;
    }
}
*/