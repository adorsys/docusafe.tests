package de.adorsys.docusafe;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.collect.Streams;
import de.adorsys.dfs.connection.api.complextypes.BucketDirectory;
import de.adorsys.dfs.connection.api.service.api.DFSConnection;
import de.adorsys.dfs.connection.impl.amazons3.AmazonS3DFSConnection;
import de.adorsys.docusafe.service.api.types.UserID;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

import static de.adorsys.docusafe.Reflectional.getReflectionally;

@Slf4j
public class DocusafeUsersFinder {

    public Set<UserID> getUsers(DFSConnection connection) {
        log.info("Searching for users");
        AmazonS3DFSConnection dfsConnection = (AmazonS3DFSConnection) connection;
        AmazonS3 s3 = getReflectionally("connection", dfsConnection);
        BucketDirectory dir = getReflectionally("amazonS3RootBucket", dfsConnection);

        Set<String> users = Streams.stream(S3Objects.withPrefix(s3, dir.getContainer(), dir.getName()).iterator())
                .map(S3ObjectSummary::getKey)
                .filter(it -> it.contains("/bp-"))
                .map(it -> it.split("/bp-")[1].split("/")[0])
                .collect(Collectors.toSet());

        // TODO: validate user somehow?
        log.info("Found {} Docusafe users to import", users.size());

        return users.stream().map(UserID::new).collect(Collectors.toSet());
    }
}
