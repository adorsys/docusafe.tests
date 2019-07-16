package de.adorsys.docusafe.rest.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.adorsys.dfs.connection.api.types.BucketName;
import de.adorsys.dfs.connection.api.types.connection.*;
import de.adorsys.docusafe.business.types.DocumentDirectoryFQN;
import de.adorsys.docusafe.business.types.DocumentFQN;
import de.adorsys.docusafe.business.types.MoveType;
import de.adorsys.docusafe.rest.adapter.docusafe.*;
import de.adorsys.docusafe.rest.adapter.datasafe.*;
import de.adorsys.docusafe.service.api.keystore.types.KeyID;
import de.adorsys.docusafe.service.api.keystore.types.ReadKeyPassword;
import de.adorsys.docusafe.service.api.types.DocumentContent;
import de.adorsys.docusafe.service.api.types.UserID;
import springfox.documentation.spring.web.json.Json;

import java.net.URL;

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
                .registerTypeAdapter(AmazonS3AccessKey.class, new AmazonS3AccessKeyJsonAdapter())
                .registerTypeAdapter(AmazonS3Region.class, new AmazonS3RegionJsonAdapter())
                .registerTypeAdapter(AmazonS3RootBucketName.class, new AmazonS3RootBucketNameJsonAdapter())
                .registerTypeAdapter(AmazonS3SecretKey.class, new AmazonS3SecretKeyJsonAdapter())
                .registerTypeAdapter(FilesystemRootBucketName.class, new FilesystemRootBucketNameJsonAdapter())
                .registerTypeAdapter(URL.class, new URLJsonAdapter())

                .registerTypeAdapter(de.adorsys.datasafe.encrypiton.api.types.UserID.class, new DatasafeUserIDJsonAdapter())
                .registerTypeAdapter(de.adorsys.datasafe.encrypiton.api.types.keystore.ReadKeyPassword.class, new DatasafeReadKeyPasswordJsonAdapter())
                .registerTypeAdapter(de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN.class, new DatasafeDocumentFQNJsonAdapter())
                .registerTypeAdapter(de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN.class, new DatasafeDocumentDirectoryFQNJsonAdapter())
                .registerTypeAdapter(de.adorsys.datasafe.simple.adapter.api.types.DocumentContent.class, new DatasafeDocumentContentJsonAdapter())

                .registerTypeAdapter(Json.class, new Swagger2JsonWorkaround())
                .create();
/*
IF YOU WONDER ABOUT SWAGGER, PLEASE SEE Swagger2JsonWorkaround
 */
    }
}
