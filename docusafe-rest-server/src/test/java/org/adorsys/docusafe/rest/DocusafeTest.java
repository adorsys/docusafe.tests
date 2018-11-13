package org.adorsys.docusafe.rest;

import org.adorsys.docusafe.rest.config.FileProperty;
import org.adorsys.docusafe.rest.config.PropertyConfigForTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by peter on 12.11.18 08:53.
 */
// @ComponentScan(basePackages = {"org.adorsys.docusafe"})
// @TestPropertySource(locations="file:/Volumes/ssd-data/data/source/adorsys/docusafe.tests/docusafe-rest-server/src/test/resources/application-test.yml")
// @PropertySource("file:/Volumes/ssd-data/data/source/adorsys/docusafe.tests/docusafe-rest-server/src/test/resources/application-test.yml")
// @SpringBootTest
// @SpringBootApplication(exclude = {RestApplication.class})
// @UseExtendedStoreConnection
// @TestPropertySource(locations="classpath:application-test.yml")
// @ImportAutoConfiguration(DocusafeSpringConfigForTest.class)
// @TestPropertySource(locations="classpath:application-test.yml")
// @ContextConfiguration(classes={DocusafeSpringConfiguration.class})
// @ActiveProfiles("test")

@RunWith(SpringRunner.class)
// @TestPropertySource(locations="classpath:application.properties")
@ContextConfiguration(classes={FileProperty.class, PropertyConfigForTest.class})

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

    @Autowired
    String basedir;


    @Test
    public void a() {
        System.out.println("read from propertyfile:" + basedir);
//         Assert.assertNotNull(basedir);
    }
}
