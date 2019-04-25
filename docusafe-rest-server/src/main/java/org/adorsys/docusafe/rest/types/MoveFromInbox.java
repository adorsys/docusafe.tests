package org.adorsys.docusafe.rest.types;


import org.adorsys.docusafe.business.types.DocumentFQN;

/**
 * Created by peter on 25.02.19 16:17.
 */
public class MoveFromInbox {
    private DocumentFQN destFQN;
    private DocumentFQN inboxFQN;

    public MoveFromInbox() {}

    public MoveFromInbox(DocumentFQN inboxFQN, DocumentFQN destFQN) {
        this.inboxFQN = inboxFQN;
        this.destFQN = destFQN;
    }

    public DocumentFQN getDestFQN() {
        return destFQN;
    }

    public DocumentFQN getInboxFQN() {
        return inboxFQN;
    }
}
