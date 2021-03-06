package org.adorsys.docusafe.client;

import de.adorsys.common.exceptions.BaseException;
import de.adorsys.common.exceptions.BaseExceptionHandler;
import de.adorsys.common.utils.HexUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by peter on 27.02.18 at 09:49.
 */
public class Main {
    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            error();
        }
        String action = args[0];

        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        DocumentsafeRestClient client = new DocumentsafeRestClient();
        if (action.equals("-cu")) {
            if (args.length != 3) {
                error();
            }
            String USER_ID = args[1];
            String PASSWORD = args[2];
            client.createUser(USER_ID, PASSWORD);
        }
        if (action.equals("-du")) {
            if (args.length != 3) {
                error();
            }
            String USER_ID = args[1];
            String PASSWORD = args[2];
            client.destroyUser(USER_ID, PASSWORD);
        }

        if (action.equals("-ws")) {
            if (args.length != 5) {
                error();
            }
            String USER_ID = args[1];
            String PASSWORD = args[2];
            String localfile = args[3];
            String remotefile = args[4];
            client.writeDocumentStream(USER_ID, PASSWORD, remotefile, getAsInputStream(localfile), new File(localfile).length());
        }
        if (action.equals("-wss")) {
            if (args.length != 5) {
                error();
            }
            String USER_ID = args[1];
            String PASSWORD = args[2];
            String localfile = args[3];
            String remotefile = args[4];
            client.writeDocumentStream(USER_ID, PASSWORD, remotefile, new SlowInputStream(getAsInputStream(localfile), 1, 1024 * 1024), new File(localfile).length());
        }
        if (action.equals("-wb")) {
            if (args.length != 5) {
                error();
            }
            String USER_ID = args[1];
            String PASSWORD = args[2];
            String localfile = args[3];
            String remotefile = args[4];
            client.writeDocument(USER_ID, PASSWORD, remotefile, getAsBytes(localfile));
        }

        if (action.equals("-rs")) {
            if (args.length != 5) {
                error();
            }
            String USER_ID = args[1];
            String PASSWORD = args[2];
            String remotefile = args[3];
            String localfile = args[4];
            client.readDocumentStream(USER_ID, PASSWORD, remotefile, localfile);
        }
        if (action.equals("-rb")) {
            if (args.length != 5) {
                error();
            }
            String USER_ID = args[1];
            String PASSWORD = args[2];
            String remotefile = args[3];
            String localfile = args[4];
            client.readDocument(USER_ID, PASSWORD, remotefile, localfile);
        }
    }

    public static InputStream getAsInputStream(String filename) {
        try {
            return new FileInputStream(new File(filename));
        } catch (Exception e) {
            LOGGER.error("file:" + new File(filename).getAbsoluteFile());
            throw BaseExceptionHandler.handle(e);
        }
    }

    public static byte[] getAsBytes(String filename) {
        try {
            return FileUtils.readFileToByteArray(new File(filename));
        } catch (Exception e) {
            LOGGER.error("file:" + new File(filename).getAbsoluteFile());
            throw BaseExceptionHandler.handle(e);
        }
    }

    public static void showInputStream(InputStream inputStream) {
        try {
            LOGGER.debug("ok, receive an inputstream");
            int available = 0;
            int limit = 100;
            while ((available = inputStream.available()) > 0) {
                int min = Math.min(limit, available);
                byte[] bytes = new byte[min];
                int read = inputStream.read(bytes, 0, min);
                if (read != min) {
                    throw new BaseException("expected to read " + min + " bytes, but read " + read + " bytes");
                }
                LOGGER.debug("READ " + min + " bytes:" + HexUtil.convertBytesToHexString(bytes));
            }
            LOGGER.debug("finished reading");
        } catch (Exception e) {
            throw BaseExceptionHandler.handle(e);
        }
    }

    private static void error() {
        LOGGER.debug("Pass params: -cu  <user> <password>                                 # create user");
        LOGGER.debug("Pass params: -du  <user> <password>                                 # destroy user");
        LOGGER.debug("Pass params: -ws  <user> <password> <local  file>   <remote file>   # writes localfile as a stream to remotefile");
        LOGGER.debug("Pass params: -wss <user> <password> <local  file>   <remote file>   # writes localfile as a slow stream to remotefile");
        LOGGER.debug("Pass params: -rs  <user> <password> <remote file>   <local  file>   # reads remotefile as a stream to localfile");
        LOGGER.debug("Pass params: -wb  <user> <password> <local  file>   <remote file>   # writes localfile as blob to remotefile");
        LOGGER.debug("Pass params: -rb  <user> <password> <remote file>   <local  file>   # reads remotefile as blob to localfile");
        System.exit(1);
    }

}
