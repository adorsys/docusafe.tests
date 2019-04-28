package de.adorsys.docusafe.rest.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peter on 07.11.18 16:13.
 */
public aspect AspectWrapperDocumentSafe {
    private final static Logger LOGGER = LoggerFactory.getLogger(AspectWrapperDocumentSafe.class);
    pointcut cachedTx(): execution(* *..CachedTransactionalDocumentSafeService.*(..));
    pointcut tx():       execution(* *..TransactionalDocumentSafeService.*(..));
    pointcut nonTx():    execution(* *..NonTransactionalDocumentSafeService.*(..));
    pointcut plain():    execution(* *..DocumentSafeService.*(..));
    pointcut conn():     execution(* *..ExtendedStoreConnection.*(..));
    Object around(): cachedTx() || tx() || nonTx() || plain() || conn() {
        LOGGER.info(String.format("============================================= \"%s\"", thisJoinPointStaticPart.getSignature()));
        long start = System.currentTimeMillis();
        Object result = null;
        RuntimeException throwable = null;

        try {
            result = proceed();
        } catch (RuntimeException t) {
            throwable = t;
        }
        long end = System.currentTimeMillis();
        if (throwable != null) {
            LOGGER.info(String.format("============================================= \"%s\" time: %d ms finished with exception: %s message: %s", thisJoinPointStaticPart.getSignature(),
                    (end - start),
                    throwable.getClass(), throwable.getMessage()));
            throw throwable;
        }
        LOGGER.info(String.format("============================================= \"%s\" time: %d ms finished", thisJoinPointStaticPart.getSignature(),
                (end - start)));
        return result;
    }
}
