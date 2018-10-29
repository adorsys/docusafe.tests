package org.adorsys.docusafe.rest.configuration;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import org.adorsys.docusafe.business.impl.WithCache;
import org.adorsys.docusafe.spring.annotation.UseExtendedStoreConnection;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by peter on 26.10.18 11:51.
 */
@UseExtendedStoreConnection
@Configuration
public class UseDocusafeServiceConfiguration {
    private final static Logger LOGGER = LoggerFactory.getLogger(UseDocusafeServiceConfiguration.class);

    @Bean
    public DocumentSafeService docusafeService(
            ExtendedStoreConnection extendedStoreConnection,
            @Value("${docusafe.cache:true}") Boolean withCache
    ) {
        if (extendedStoreConnection == null) {
            throw new BaseException("Injection did not work");
        }
        LOGGER.debug("create documentSafeService");
        DocumentSafeService documentSafeService = new DocumentSafeServiceImpl(withCache ? WithCache.TRUE : WithCache.FALSE, extendedStoreConnection);
        return documentSafeService;
    }
}