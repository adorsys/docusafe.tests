package org.adorsys.docusafe.rest.config;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Created by peter on 13.11.18 15:12.
 */
@Component
@ConfigurationProperties(prefix = "docusafe.storeconnection.filesystem")
@Validated
public class FileProperty {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileProperty.class);
    @Nullable
    private String basedir;

    public String getBaseDir() {
        LOGGER.debug("basedir:" + basedir);
        return basedir;
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }
}