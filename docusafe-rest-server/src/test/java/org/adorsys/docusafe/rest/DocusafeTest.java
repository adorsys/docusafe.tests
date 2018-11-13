package org.adorsys.docusafe.rest;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.rest.config.DocusafeSpringConfigForTest;
import org.adorsys.docusafe.spring.annotation.UseExtendedStoreConnection;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by peter on 12.11.18 08:53.
 */

/*
@RunWith(SpringRunner.class)
// @ComponentScan(basePackages = {"org.adorsys.docusafe"})
// @TestPropertySource(locations="file:/Volumes/ssd-data/data/source/adorsys/docusafe.tests/docusafe-rest-server/src/test/resources/application-test.yml")
// @PropertySource("file:/Volumes/ssd-data/data/source/adorsys/docusafe.tests/docusafe-rest-server/src/test/resources/application-test.yml")

@TestPropertySource(locations="classpath:application-test.yml")
@ContextConfiguration(classes={DocusafeSpringConfigForTest.class})
@UseExtendedStoreConnection
*/
public class DocusafeTest {
    /*
    @Autowired
    private ExtendedStoreConnection extendedStoreConnection;

    @Test
    public void a() {
        if (extendedStoreConnection == null) {
            throw new BaseException("extended store connection has not been injected");
        }
        extendedStoreConnection.listAllBuckets();
    }
    */
}
