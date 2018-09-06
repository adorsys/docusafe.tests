package org.adorsys.docusafe.rest.types;

import org.adorsys.docusafe.business.types.UserID;

/**
 * Created by peter on 15.08.18 at 16:04.
 */
public class TestParameter {
    public TestAction testAction;
    public DocusafeLayer docusafeLayer;
    public CacheType cacheType;
    public UserID userid;
    public Integer numberOfDocuments;
    public Integer sizeOfDocument;
    public Integer documentsPerDirectory;
    public DocumentInfo[] documentsToRead;

    @Override
    public String toString() {
        return "TestParameter{" +
                "testAction=" + testAction +
                ", docusafeLayer=" + docusafeLayer +
                ", cacheType=" + cacheType +
                ", userid=" + userid +
                ", numberOfDocuments=" + numberOfDocuments +
                ", sizeOfDocument=" + sizeOfDocument +
                ", documentsPerDirectory=" + documentsPerDirectory +
                '}';
    }
}
