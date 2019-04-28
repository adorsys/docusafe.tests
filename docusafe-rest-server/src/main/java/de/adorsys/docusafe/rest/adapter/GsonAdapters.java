package de.adorsys.docusafe.rest.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.adorsys.dfs.connection.api.types.BucketName;
import de.adorsys.docusafe.business.types.DocumentDirectoryFQN;
import de.adorsys.docusafe.business.types.DocumentFQN;
import de.adorsys.docusafe.business.types.MoveType;
import de.adorsys.docusafe.service.api.keystore.types.KeyID;
import de.adorsys.docusafe.service.api.keystore.types.ReadKeyPassword;
import de.adorsys.docusafe.service.api.types.DocumentContent;
import de.adorsys.docusafe.service.api.types.UserID;
import springfox.documentation.spring.web.json.Json;

/**
 * Created by peter on 26.02.19 19:09.
 */
public class GsonAdapters {
    public static Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(BucketName.class, new BucketNameJsonAdapter())
                .registerTypeAdapter(KeyID.class, new KeyIDJsonAdapter())
                .registerTypeAdapter(UserID.class, new UserIDJsonAdapter())
                .registerTypeAdapter(ReadKeyPassword.class, new ReadKeyPasswordJsonAdapter())
                .registerTypeAdapter(DocumentFQN.class, new DocumentFQNJsonAdapter())
                .registerTypeAdapter(DocumentDirectoryFQN.class, new DocumentDirectoryFQNJsonAdapter())
                .registerTypeAdapter(DocumentContent.class, new DocumentContentJsonAdapter())
                .registerTypeAdapter(MoveType.class, new MoveTypeAdapter())

                .registerTypeAdapter(Json.class, new SpringfoxJsonToGsonAdapter())
                .create();

    }
}
