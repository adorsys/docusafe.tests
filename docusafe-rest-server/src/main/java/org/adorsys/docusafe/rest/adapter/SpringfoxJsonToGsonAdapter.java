package org.adorsys.docusafe.rest.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.adorsys.cryptoutils.exceptions.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springfox.documentation.spring.web.json.Json;

import java.lang.reflect.Type;

/** workaround, to run swagger2 with gson
 * THANX TO naXa
 * https://stackoverflow.com/questions/30219946/springfoxswagger2-does-not-work-with-gsonhttpmessageconverterconfig
 */

public class SpringfoxJsonToGsonAdapter implements JsonSerializer<Json> {
    private final static Logger LOGGER = LoggerFactory.getLogger(SpringfoxJsonToGsonAdapter.class);

    @Override
    public JsonElement serialize(Json json, Type type, JsonSerializationContext context) {
        LOGGER.debug("PARSE:" + json.value());
        String s = petersSubstitution(json.value());
        final JsonParser parser = new JsonParser();
        JsonElement e = parser.parse(s);
        return e;
    }

    private String petersSubstitution(String value) {
        LOGGER.debug("before:" + value);
        String a = "\"UserIDAuth\":{\"type\":\"object\",\"properties\":{\"readKeyPassword\":{\"$ref\":\"#/definitions/ReadKeyPassword\"},\"userID\":{\"$ref\":\"#/definitions/UserID\"}}}";
        String b = "\"UserIDAuth\":{\"type\":\"object\",\"properties\":{\"readKeyPassword\":{\"type\":\"string\"},\"userID\":{\"type\":\"string\"}}}";
        String c = "\"ReadKeyPassword\":{\"type\":\"object\",\"properties\":{\"typeName\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}";
        String d = "\"ReadKeyPassword\":{\"type\":\"string\"}";
        String e = "\"UserID\":{\"type\":\"object\",\"properties\":{\"typeName\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}";
        String f = "\"UserID\":{\"type\":\"string\"}";
        String g = "\"DocumentFQN\":{\"type\":\"object\",\"properties\":{\"documentDirectory\":{\"$ref\":\"#/definitions/DocumentDirectoryFQN\"},\"plainNameWithoutPath\":{\"$ref\":\"#/definitions/DocumentFQN\"},\"typeName\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}";
        String h = "\"DocumentFQN\":{\"type\":\"string\"}";
        String i = "\"DocumentContent\":{\"type\":\"object\",\"properties\":{\"value\":{\"type\":\"string\",\"format\":\"byte\"}}}";
        String j = "\"DocumentContent\":{\"type\":\"string\"}";
        if (value.indexOf(c) == -1) {
            throw new BaseException("expected to find " + a);
        }
        if (value.indexOf(e) == -1) {
            throw new BaseException("expected to find " + a);
        }
        if (value.indexOf(g) == -1) {
            throw new BaseException("expected to find " + a);
        }
        if (value.indexOf(i) == -1) {
            throw new BaseException("expected to find " + a);
        }
        value = value.replace(c,d);
        value = value.replace(e,f);
        value = value.replace(g,h);
        value = value.replace(i,j);
        value = value.replace(e,f);
        LOGGER.debug("after :" + value);
        return value;
    }
}
