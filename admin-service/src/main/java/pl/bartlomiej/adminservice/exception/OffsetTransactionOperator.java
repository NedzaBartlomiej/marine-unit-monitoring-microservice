package pl.bartlomiej.adminservice.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class OffsetTransactionOperator {

    public static <T> void performOffsetTransaction(final T createdAdmin,
                                                    List<Consumer<T>> performFunctions,
                                                    List<Consumer<T>> compensationFunctions) {
        log.debug("Offset transaction of user creation process has been initiated.");
        try {
            log.debug("Performing transactional user creation processes.");
            performFunctions.forEach(pf -> pf.accept(createdAdmin));
        } catch (RuntimeException e) {
            log.error("Something go wrong in the user creation transaction, deleting created user.", e);
            compensationFunctions.forEach(cf -> cf.accept(createdAdmin));
            throw new RuntimeException(e);
        }
    }
}
