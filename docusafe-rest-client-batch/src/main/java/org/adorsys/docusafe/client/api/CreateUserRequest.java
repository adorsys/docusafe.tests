package org.adorsys.docusafe.client.api;

/**
 * Created by peter on 27.02.18 at 09:46.
 */
public class CreateUserRequest extends UserIDAndPasswordTupel {
    public CreateUserRequest(String userID, String password) {
        super(userID, password);
    }

}
