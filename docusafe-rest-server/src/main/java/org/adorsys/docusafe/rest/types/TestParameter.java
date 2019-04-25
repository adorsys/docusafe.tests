package org.adorsys.docusafe.rest.types;


import org.adorsys.docusafe.service.api.types.UserID;

/**
 * Created by peter on 15.08.18 at 16:04.
 */
public class TestParameter {
    public TestAction testAction;
    public DocusafeLayer docusafeLayer;
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
                ", userid=" + userid +
                ", numberOfDocuments=" + numberOfDocuments +
                ", sizeOfDocument=" + sizeOfDocument +
                ", documentsPerDirectory=" + documentsPerDirectory +
                '}';
    }
}
