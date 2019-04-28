package de.adorsys.docusafe.rest.types;

import de.adorsys.docusafe.business.types.DocumentFQN;
import de.adorsys.docusafe.business.types.MoveType;
import de.adorsys.docusafe.service.api.types.UserID;

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
