package org.adorsys.docusafe.rest.types;

import org.adorsys.docusafe.business.types.MoveType;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;

/**
 * Created by peter on 29.01.18 at 20:17.
 */
public class MoveToInboxOfUser {
    private UserID receivingUser;
    private DocumentFQN sourceFQN;
    private DocumentFQN inboxFQN;
    private MoveType moveType;

    public MoveToInboxOfUser() {
    }

    public MoveToInboxOfUser(UserID receivingUser, DocumentFQN sourceFQN, DocumentFQN inboxFQN, MoveType moveType) {
        this.receivingUser = receivingUser;
        this.sourceFQN = sourceFQN;
        this.inboxFQN = inboxFQN;
        this.moveType = moveType;
    }

    public UserID getReceivingUser() {
        return receivingUser;
    }

    public DocumentFQN getSourceFQN() {
        return sourceFQN;
    }

    public DocumentFQN getInboxFQN() {
        return inboxFQN;
    }

    public MoveType getMoveType() {
        return moveType;
    }
}
