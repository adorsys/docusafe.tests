package org.adorsys.docusafe.rest;

import org.adorsys.docusafe.spring.annotation.UseDocusafeCachedTransactional;
import org.adorsys.docusafe.spring.annotation.UseExtendedStoreConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;


/**
 * Created by peter on 02.10.18.
 */

@Configuration
@UseDocusafeCachedTransactional
@UseExtendedStoreConnection

public class DocusafeSpringConfiguration {
    private final static Logger LOGGER = LoggerFactory.getLogger(DocusafeSpringConfiguration.class);
    public DocusafeSpringConfiguration() {
        LOGGER.debug("docusafe spring config supplies ExtendedStoreConnection and CachedTransactionalDocumentSafeService as spring beans.");
    }
}
