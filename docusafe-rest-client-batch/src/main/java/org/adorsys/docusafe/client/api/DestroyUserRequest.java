package org.adorsys.docusafe.client.api;

/**
 * Created by peter on 26.10.18 12:15.
 */
public class DestroyUserRequest extends UserIDAndPasswordTupel {
    public DestroyUserRequest(String userID, String password) {
        super(userID, password);
    }

}