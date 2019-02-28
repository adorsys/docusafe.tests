package org.adorsys.docusafe.rest.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.adorsys.docusafe.business.types.MoveType;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.adorsys.docusafe.service.types.DocumentKeyID;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.adorsys.encobject.types.BucketName;
import org.adorsys.encobject.types.OverwriteFlag;
import springfox.documentation.spring.web.json.Json;

/**
 * Created by peter on 26.02.19 19:09.
 */
public class GsonAdapters {
    public static Gson gson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(BucketName.class, new BucketNameJsonAdapter())
                .registerTypeAdapter(DocumentKeyID.class, new DocumentKeyIDJsonAdapter())
                .registerTypeAdapter(UserID.class, new UserIDJsonAdapter())
                .registerTypeAdapter(ReadKeyPassword.class, new ReadKeyPasswordJsonAdapter())
                .registerTypeAdapter(DocumentFQN.class, new DocumentFQNJsonAdapter())
                .registerTypeAdapter(DocumentDirectoryFQN.class, new DocumentDirectoryFQNJsonAdapter())
                .registerTypeAdapter(DocumentContent.class, new DocumentContentJsonAdapter())
                .registerTypeAdapter(OverwriteFlag.class , new OverwriteFlagAdapter())
                .registerTypeAdapter(MoveType.class, new MoveTypeAdapter())

                .registerTypeAdapter(Json.class, new SpringfoxJsonToGsonAdapter())
                .create();

    }
}
