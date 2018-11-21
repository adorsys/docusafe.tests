package org.adorsys.docusafe.rest;

import org.adorsys.docusafe.cached.transactional.CachedTransactionalDocumentSafeService;
import org.adorsys.docusafe.cached.transactional.impl.CachedTransactionalDocumentSafeServiceImpl;
import org.adorsys.docusafe.spring.annotation.UseDocusafeSpringConfiguration;
import org.adorsys.docusafe.spring.factory.SpringCachedTransactionalDocusafeServiceFactory;
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

    @Bean
    WrapperA wrapperA(SpringCachedTransactionalDocusafeServiceFactory factory) {
        return new WrapperA(factory.getCachedTransactionalDocumentSafeServiceWithSubdir("folderA"));
    }

    @Bean
    WrapperB wrapperB(SpringCachedTransactionalDocusafeServiceFactory factory) {
        return new WrapperB(factory.getCachedTransactionalDocumentSafeServiceWithSubdir("folderB"));
    }

    public static interface CachedTransactionalDocumentSafeServiceWrapper {
        CachedTransactionalDocumentSafeService get();
    }

    public static class WrapperA implements CachedTransactionalDocumentSafeServiceWrapper {
        private final CachedTransactionalDocumentSafeService service;
        public WrapperA(CachedTransactionalDocumentSafeService service) {
            this.service = service;
        }

        @Override
        public CachedTransactionalDocumentSafeService get() {
            return service;
        }
    }

    public static class WrapperB implements CachedTransactionalDocumentSafeServiceWrapper {
        private final CachedTransactionalDocumentSafeService service;
        public WrapperB(CachedTransactionalDocumentSafeService service) {
            this.service = service;
        }

        @Override
        public CachedTransactionalDocumentSafeService get() {
            return service;
        }
    }
}
