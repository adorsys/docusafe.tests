package de.adorsys.docusafe.rest.types;

import de.adorsys.docusafe.service.api.types.UserID;

import java.util.Arrays;

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
    public DynamicClientInfo dynamicClientInfo;
    public Boolean createDeterministicDocuments;

    @Override
    public String toString() {
        return "TestParameter{" +
                "testAction=" + testAction +
                ", docusafeLayer=" + docusafeLayer +
                ", userid=" + userid +
                ", numberOfDocuments=" + numberOfDocuments +
                ", sizeOfDocument=" + sizeOfDocument +
                ", documentsPerDirectory=" + documentsPerDirectory +
                ", documentsToRead=" + Arrays.toString(documentsToRead) +
                ", dynamicClientInfo=" + dynamicClientInfo +
                ", createDeterministicDocuments=" + createDeterministicDocuments +
                '}';
    }
}
