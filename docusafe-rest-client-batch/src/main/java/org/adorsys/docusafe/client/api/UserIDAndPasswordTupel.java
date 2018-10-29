package org.adorsys.docusafe.client.api;

/**
 * Created by peter on 26.10.18 12:15.
 */
public class UserIDAndPasswordTupel {
    public String userID;
    public String readKeyPassword;

    public UserIDAndPasswordTupel(String userID, String readKeyPassword) {
        this.userID = userID;
        this.readKeyPassword = readKeyPassword;
    }
}
