package org.adorsys.docusafe.rest.types;

import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.encobject.types.OverwriteFlag;

/**
 * Created by peter on 25.02.19 16:17.
 */
public class MoveFromInbox {
    private DocumentFQN destFQN;
    private DocumentFQN inboxFQN;
    private OverwriteFlag overwriteFlag;

    public MoveFromInbox() {}

    public MoveFromInbox(DocumentFQN inboxFQN, DocumentFQN destFQN, OverwriteFlag overwriteFlag) {
        this.inboxFQN = inboxFQN;
        this.destFQN = destFQN;
        this.overwriteFlag = overwriteFlag;
    }

    public DocumentFQN getDestFQN() {
        return destFQN;
    }

    public DocumentFQN getInboxFQN() {
        return inboxFQN;
    }

    public OverwriteFlag getOverwriteFlag() {
        return overwriteFlag;
    }
}
