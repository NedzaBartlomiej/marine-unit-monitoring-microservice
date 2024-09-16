package pl.bartlomiej.adminservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class OffsetTransactionOperator {

    public static <PerfT, CompT> void performOffsetTransaction(final PerfT perfFuncArg,
                                                               final CompT compFuncArg,
                                                               final List<Consumer<PerfT>> performFunctions,
                                                               final List<Consumer<CompT>> compensationFunctions) {
        log.debug("Offset transaction has been initiated.");
        try {
            log.debug("Performing transactional processes.");
            performFunctions.forEach(pf -> pf.accept(perfFuncArg));
        } catch (RuntimeException e) {
            log.error("Something go wrong in the transaction, invoking compensation functions.", e);
            compensationFunctions.forEach(cf -> cf.accept(compFuncArg));
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}