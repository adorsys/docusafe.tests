package de.adorsys.docusafe;

import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DFSCredentials;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentContent;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceImpl;
import de.adorsys.dfs.connection.api.types.ListRecursiveFlag;
import de.adorsys.dfs.connection.impl.amazons3.AmazonS3DFSConnection;
import de.adorsys.docusafe.business.DocumentSafeService;
import de.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import de.adorsys.docusafe.business.types.DSDocument;
import de.adorsys.docusafe.business.types.DocumentDirectoryFQN;
import de.adorsys.docusafe.business.types.DocumentFQN;
import de.adorsys.docusafe.service.api.keystore.types.ReadKeyPassword;
import de.adorsys.docusafe.service.api.types.UserID;
import de.adorsys.docusafe.service.api.types.UserIDAuth;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class UserMigratingService {

    private final DocumentSafeService documentSafeService;
    private final SimpleDatasafeService datasafeService;

    public UserMigratingService(AmazonS3DFSConnection connection, DFSCredentials credentials) {
        documentSafeService = new DocumentSafeServiceImpl(connection);
        datasafeService = new SimpleDatasafeServiceImpl(credentials);
    }

    int migrate(Set<UserID> usersToMigrate, Set<String> migrateOnlyUsernames, Set<String> usernamesToSkip,
                 String userGenericPassword) {
        int countMigrated = 0;
        for (UserID user: usersToMigrate) {
            if (usernamesToSkip.contains(user.getValue())
                    || (!migrateOnlyUsernames.isEmpty() && !migrateOnlyUsernames.contains(user.getValue()))) {
                log.info("SKIPPING Migration of Docusafe user '{}'", user.getValue());
                continue;
            }

            log.info("Migrating Docusafe user '{}'", user.getValue());
            UserIDAuth authDocu = new UserIDAuth(user, new ReadKeyPassword(userGenericPassword));
            createDatasafeUser(authDocu);
            List<DocumentFQN> documents = documentSafeService.list(
                    authDocu,
                    new DocumentDirectoryFQN(""),
                    ListRecursiveFlag.TRUE
            );

            for (DocumentFQN doc : documents) {
                copyDocusafeToDatasafeDocument(authDocu, doc);
            }

            log.info("Copied {} users' files '{}'", documents.size(), user.getValue());
            log.info("Done migrating user '{}'", user.getValue());
            ++countMigrated;
        }

        return countMigrated;
    }

    private void createDatasafeUser(UserIDAuth auth) {
        datasafeService.createUser(datasafeAuth(auth));
        log.info("Created Datasafe user '{}'", auth.getUserID().getValue());
    }

    private void copyDocusafeToDatasafeDocument(UserIDAuth auth, DocumentFQN doc) {
        DSDocument document = documentSafeService.readDocument(auth, doc);
        datasafeService.storeDocument(
                datasafeAuth(auth),
                new de.adorsys.datasafe.simple.adapter.api.types.DSDocument(
                        new de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN(document.getDocumentFQN().getValue()),
                        new DocumentContent(document.getDocumentContent().getValue())
                )
        );

        // validate copied file
        de.adorsys.datasafe.simple.adapter.api.types.DSDocument stored = datasafeService.readDocument(
                datasafeAuth(auth),
                new de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN(document.getDocumentFQN().getValue())
        );

        if (!stored.getDocumentFQN().getDocusafePath().equals(doc.getValue())) {
            log.error("Path mismatch: '{}' for user '{}'", doc, auth.getUserID().getValue());
            throw new IllegalStateException("Path mismatch");
        }

        if (!Arrays.equals(stored.getDocumentContent().getValue(), document.getDocumentContent().getValue())) {
            log.error("Content mismatch: '{}' for user '{}'", doc, auth.getUserID().getValue());
            throw new IllegalStateException("Content mismatch");
        }
    }

    private de.adorsys.datasafe.encrypiton.api.types.UserIDAuth datasafeAuth(UserIDAuth auth) {
        return new de.adorsys.datasafe.encrypiton.api.types.UserIDAuth(
                auth.getUserID().getValue(),
                auth.getReadKeyPassword().getValue()
        );
    }
}
