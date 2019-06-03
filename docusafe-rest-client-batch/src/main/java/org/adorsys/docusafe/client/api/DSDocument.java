package org.adorsys.docusafe.client.api;

import java.util.HashMap;

/**
 * Created by peter on 27.02.18 at 12:48.
 */
public class DSDocument {
    String documentFQN;
    String documentContent;

    public String getDocumentFQN() {
        return documentFQN;
    }

    public void setDocumentFQN(String documentFQN) {
        this.documentFQN = documentFQN;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }


    @Override
    public String toString() {
        return "DSDocument{" +
                "documentFQN='" + documentFQN + '\'' +
                ", hexBinaryContent='" + documentContent + '\'' +
                '}';
    }
}
