package org.adorsys.docusafe.rest.config;

import org.adorsys.docusafe.spring.annotation.UseDocusafeCachedTransactional;
import org.adorsys.docusafe.spring.annotation.UseExtendedStoreConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * Created by peter on 12.11.18 09:00.
 */
@UseDocusafeCachedTransactional
@UseExtendedStoreConnection
public class DocusafeSpringConfigForTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(DocusafeSpringConfigForTest.class);
    public DocusafeSpringConfigForTest() {
        LOGGER.info("==========================================");
        LOGGER.debug("docusafe spring config supplies ExtendedStoreConnection and CachedTransactionalDocumentSafeService as spring beans.");
    }
}