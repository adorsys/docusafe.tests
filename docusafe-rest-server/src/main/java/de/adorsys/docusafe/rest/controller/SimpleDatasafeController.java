package de.adorsys.docusafe.rest.controller;


import de.adorsys.datasafe.encrypiton.api.types.UserID;
import de.adorsys.datasafe.encrypiton.api.types.UserIDAuth;
import de.adorsys.datasafe.encrypiton.api.types.keystore.ReadKeyPassword;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.DSDocument;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN;
import de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN;
import de.adorsys.datasafe.simple.adapter.api.types.ListRecursiveFlag;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/datasafe")
public class SimpleDatasafeController {
        private final static String APPLICATION_JSON = "application/json";

        private final static Logger LOGGER = LoggerFactory.getLogger(SimpleDatasafeController.class);

        @Autowired
        private SimpleDatasafeService service;

        /**
         * USER
         * ===========================================================================================
         */
        @RequestMapping(
                value = "/internal/user",
                method = {RequestMethod.PUT},
                consumes = {APPLICATION_JSON},
                produces = {APPLICATION_JSON}
        )
        public void createUser(@RequestBody UserIDAuth userIDAuth) {
            LOGGER.debug("create user " + userIDAuth.getUserID());
            service.createUser(userIDAuth);
        }

        @RequestMapping(
                value = "/internal/user",
                method = {RequestMethod.DELETE}
        )
        public void destroyUser(@RequestHeader("userid") String userid,
                                @RequestHeader("password") String password) {
            LOGGER.info("************************************************");
            LOGGER.debug("delete user " + userid);
            service.destroyUser(new UserIDAuth(new UserID(userid), new ReadKeyPassword(password)));
        }

        @RequestMapping(
                value = "/internal/user/{UserID}",
                method = {RequestMethod.GET}
        )
        public
        @ResponseBody
        ResponseEntity<Boolean> userExists(@PathVariable("UserID") String userIDString) {
            UserID userID = new UserID(userIDString);
            LOGGER.debug("get user exists: " + userID);
            if (!service.userExists(userID)) {
                LOGGER.debug(userID + " does not exist");
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            LOGGER.debug(userID + " exists");
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        }




        /**
         * DOCUMENT
         * ===========================================================================================
         */

        /**
         * -- byte orientiert --
         */
        @ApiOperation(value="creates a document", notes = "The DocumentContent is a string but must be in HEX Digits, et \"AFFE\" is valid, but \"Hello\" isnt.")
        @RequestMapping(
                value = "/document",
                method = {RequestMethod.PUT},
                consumes = {APPLICATION_JSON}
        )
        public void storeDocument(@RequestHeader("userid") String userid,
                                  @RequestHeader("password") String password,
                                  @RequestBody DSDocument dsDocument) {
            UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
            service.storeDocument(userIDAuth, dsDocument);
        }

        @RequestMapping(
                value = "/document",
                method = {RequestMethod.GET},
                produces = {APPLICATION_JSON}
        )
        public
        @ResponseBody
        ResponseEntity<DSDocument> readDocument(@RequestHeader("userid") String userid,
                                                @RequestHeader("password") String password,
                                                @RequestParam("documentFQN") String documentFQNString
        ) {
            DocumentFQN documentFQN = new DocumentFQN(documentFQNString);
            LOGGER.debug("get document request arrived " + documentFQN);
            UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
            if (!service.documentExists(userIDAuth, documentFQN)) {
                LOGGER.debug("document " + documentFQN + " does not exist");
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            LOGGER.debug("document " + documentFQN + " exists");
            return new ResponseEntity<>(service.readDocument(userIDAuth, documentFQN), HttpStatus.OK);
        }


        /**
         * -- content art unabhängig --
         */
        @ApiOperation(value="deletes a document or a whole path", notes = "If the documentFQN ends with a slash (/) then it is assumend to delete a path with the name of the documentFQN. Otherwise it is expected to be a single Documents path")
        @RequestMapping(
                value = "/document",
                method = {RequestMethod.DELETE},
                produces = {APPLICATION_JSON}
        )
        public void destroyDocument(@RequestHeader("userid") String userid,
                                    @RequestHeader("password") String password,
                                    @RequestParam("documentFQN") String pathToDelete
        ) {
            LOGGER.debug("destroy document request arrived");
            UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
            if (pathToDelete.endsWith("/")) {
                DocumentDirectoryFQN documentDirectoryFQN = new DocumentDirectoryFQN(pathToDelete.substring(0,pathToDelete.length()-1));
                LOGGER.debug("destroy document folder " + documentDirectoryFQN);
                service.deleteFolder(userIDAuth, documentDirectoryFQN);
            } else {
                service.deleteDocument(userIDAuth, new DocumentFQN(pathToDelete));
            }
            LOGGER.debug("destroy document request finished");
        }


        @RequestMapping(
                value = "/document/list",
                method = {RequestMethod.GET},
                produces = {APPLICATION_JSON}
        )
        public ResponseEntity listDocuments(@RequestHeader("userid") String userid,
                                            @RequestHeader("password") String password,
                                            @RequestParam("documentDirectoryFQN") String documentDirectoryFQNString,
                                            @RequestParam("listRecursiveFlag") ListRecursiveFlag listRecursiveFlag) {
            UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
            DocumentDirectoryFQN documentDirectoryFQN = new DocumentDirectoryFQN(documentDirectoryFQNString);
            return new ResponseEntity<>(service.list(userIDAuth, documentDirectoryFQN, listRecursiveFlag), HttpStatus.OK);
        }

}
