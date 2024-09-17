package pl.bartlomiej.adminservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class OffsetTransactionOperator { // todo - refactor duplication in the methods

    public static <PerfT, CompT> void performOffsetConsumerTransaction(final PerfT perfFuncArg,
                                                                       final CompT compFuncArg,
                                                                       final Consumer<PerfT> performFunction,
                                                                       final Consumer<CompT> compensationFunction) {
        log.debug("Offset transaction has been initiated.");
        try {
            log.debug("Performing transactional processes.");
            performFunction.accept(perfFuncArg);
        } catch (RuntimeException e) {
            log.error("Something go wrong in the transaction, invoking compensation functions.", e);
            compensationFunction.accept(compFuncArg);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public static <PerfT, PerfR, CompT> PerfR performOffsetFunctionTransaction(final PerfT perfFuncArg,
                                                                               final CompT compFuncArg,
                                                                               final Function<PerfT, PerfR> performFunction,
                                                                               final Consumer<CompT> compensationFunction) {
        log.debug("Offset transaction has been initiated.");
        try {
            log.debug("Performing transactional processes.");
            return performFunction.apply(perfFuncArg);
        } catch (RuntimeException e) {
            log.error("Something go wrong in the transaction, invoking compensation functions.", e);
            compensationFunction.accept(compFuncArg);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}