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
    public Integer threadsPerUser;
    public Integer numberOfThreads;
    public Integer sizeOfDocument;
    public Integer documentsPerDirectory;

    @Override
    public String toString() {
        return "TestParameter{" +
                "testcase=" + testcase +
                ", docusafeLayer=" + docusafeLayer +
                ", cacheType=" + cacheType +
                ", userid=" + userid +
                ", threadsPerUser=" + threadsPerUser +
                ", numberOfThreads=" + numberOfThreads +
                ", sizeOfDocument=" + sizeOfDocument +
                ", documentsPerDirectory=" + documentsPerDirectory +
                '}';
    }
}
