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
    public StaticClientInfo staticClientInfo;
    public DynamicClientInfo dynamicClientInfo;

    public static class StaticClientInfo {
        public Integer numberOfThreads;
        public Integer numberOfRepeats;

        @Override
        public String toString() {
            return "StaticClientInfo{" +
                    "numberOfThreads=" + numberOfThreads +
                    ", numberOfRepeats=" + numberOfRepeats +
                    '}';
        }
    };

    public static class DynamicClientInfo {
        public Integer threadNumber;
        public Integer repetitionNumber;

        @Override
        public String toString() {
            return "DynamicClientInfo{" +
                    "threadNumber=" + threadNumber +
                    ", repetitionNumber=" + repetitionNumber +
                    '}';
        }
    }

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
                ", staticClientInfo=" + staticClientInfo +
                ", dynamicClientInfo=" + dynamicClientInfo +
                '}';
    }
}
