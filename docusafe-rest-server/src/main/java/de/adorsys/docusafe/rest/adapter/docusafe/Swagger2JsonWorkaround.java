package de.adorsys.docusafe.rest.adapter.docusafe;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.adorsys.common.exceptions.BaseException;
import de.adorsys.dfs.connection.api.types.connection.*;
import de.adorsys.docusafe.business.types.DocumentDirectoryFQN;
import de.adorsys.docusafe.business.types.DocumentFQN;
import de.adorsys.docusafe.service.api.keystore.types.ReadKeyPassword;
import de.adorsys.docusafe.service.api.types.DocumentContent;
import de.adorsys.docusafe.service.api.types.UserID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.spring.web.json.Json;

import java.lang.reflect.Type;
import java.net.URL;

/** workaround, to run swagger2 with gson
 * THANX TO naXa
 * https://stackoverflow.com/questions/30219946/springfoxswagger2-does-not-work-with-gsonhttpmessageconverterconfig
 */
public class Swagger2JsonWorkaround implements JsonSerializer<Json> {
    private final static Logger LOGGER = LoggerFactory.getLogger(Swagger2JsonWorkaround.class);

    @Override
    public JsonElement serialize(Json json, Type type, JsonSerializationContext context) {
        LOGGER.debug("PARSE:" + json.value());
        String value = json.value();
        value = replaceClassAsString(value, ReadKeyPassword.class);
        value = replaceClassAsString(value, DocumentFQN.class);
        value = replaceClassAsString(value, DocumentDirectoryFQN.class);
        value = replaceClassAsString(value, UserID.class);
        value = replaceClassAsString(value, DocumentContent.class);
        value = replaceClassAsString(value, AmazonS3AccessKey.class);
        value = replaceClassAsString(value, AmazonS3SecretKey.class);
        value = replaceClassAsString(value, AmazonS3Region.class);
        value = replaceClassAsString(value, AmazonS3RootBucketName.class);
        value = replaceClassAsString(value, FilesystemRootBucketName.class);
        value = replaceClassAsString(value, URL.class);
        final JsonParser parser = new JsonParser();
        JsonElement e = parser.parse(value);
        return e;
    }

    private String replaceClassAsString(String all, Class clazz) {
        String tokenToReplace = findTokenToReplace(all, clazz);
        LOGGER.debug("token to replace is " + tokenToReplace);

        String tokenToReplaceWith = "\"" + clazz.getSimpleName() + "\":" + "{\"type\":\"string\"}";
        LOGGER.debug("token to replace with is " + tokenToReplaceWith);

        return all.replace(tokenToReplace, tokenToReplaceWith);
    }

    private String findTokenToReplace(String all, Class clazz) {
        String tokenToSearchFor = "\"" + clazz.getSimpleName() + "\"" + ":{";
        int index = all.indexOf(tokenToSearchFor);
        if (index == -1) {
            throw new BaseException("did not find token " + tokenToSearchFor);
        }
        char braceOpen = "{".charAt(0);
        char braceClose = "}".charAt(0);

        int open = 0;
        int close = 0;
        for (int j = index; j < all.length(); j++) {
            char c = all.charAt(j);
            if (c == braceOpen) {
                open++;
            }
            if (c == braceClose) {
                close++;
            }
            if (open > 0 && open == close) {
                return all.substring(index, j + 1);
            }
        }
        throw new BaseException("did not find token " + tokenToSearchFor);
    }
}
