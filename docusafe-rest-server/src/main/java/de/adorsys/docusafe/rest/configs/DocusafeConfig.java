package de.adorsys.docusafe.rest.configs;

import de.adorsys.docusafe.cached.transactional.CachedTransactionalDocumentSafeService;
import de.adorsys.docusafe.spring.annotation.UseDocusafeSpringConfiguration;
import de.adorsys.docusafe.spring.factory.SpringCachedTransactionalDocusafeServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by peter on 21.11.18 09:15.
 */
@Configuration
@UseDocusafeSpringConfiguration
public class DocusafeConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(DocusafeConfig.class);

    @Bean
    CachedTransactionalDocumentSafeService cachedTransactionalDocumentSafeService(SpringCachedTransactionalDocusafeServiceFactory factory) {
        LOGGER.info("return a new " + CachedTransactionalDocumentSafeService.class.getName());
        return factory.getCachedTransactionalDocumentSafeServiceWithSubdir(null);
    }
}