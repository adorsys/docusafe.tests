package org.adorsys.docusafe.rest.types;

import org.adorsys.docusafe.business.types.UserID;

/**
 * Created by peter on 15.08.18 at 16:04.
 */
public class TestParameter {
    public TestCase testcase;
    public DocusafeLayer docusafeLayer;
    public CacheType cacheType;
    public UserID userid;
    public Integer numberOfDocuments;
    public Integer sizeOfDocument;
    public Integer documentsPerDirectory;

    @Override
    public String toString() {
        return "TestParameter{" +  "\n" +
                "testcase=" + testcase + "\n" +
                "docusafeLayer=" + docusafeLayer + "\n" +
                "cacheType=" + cacheType + "\n" +
                "userid=" + userid.getValue() + "\n" +
                "numberOfDocuments=" + numberOfDocuments + "\n" +
                "sizeOfDocument=" + sizeOfDocument + "\n" +
                "documentsPerDirectory=" + documentsPerDirectory + "\n" +
                "}" +  "\n";
    }
}
