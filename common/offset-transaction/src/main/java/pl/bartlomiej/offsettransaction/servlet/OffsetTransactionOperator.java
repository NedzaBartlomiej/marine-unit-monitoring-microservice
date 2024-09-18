package pl.bartlomiej.offsettransaction.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public class OffsetTransactionOperator {

    private static final Logger log = LoggerFactory.getLogger(OffsetTransactionOperator.class);

    public static <PerfT, CompT> void performOffsetConsumerTransaction(final PerfT perfFuncArg,
                                                                       final CompT compFuncArg,
                                                                       final Consumer<PerfT> performAction,
                                                                       final Consumer<CompT> compensationAction) {
        performOffsetTransaction(
                () -> {
                    performAction.accept(perfFuncArg);
                    return null;
                },
                compFuncArg,
                compensationAction
        );
    }

    public static <PerfT, PerfR, CompT> PerfR performOffsetFunctionTransaction(final PerfT perfFuncArg,
                                                                               final CompT compFuncArg,
                                                                               final Function<PerfT, PerfR> performAction,
                                                                               final Consumer<CompT> compensationAction) {
        return performOffsetTransaction(
                () -> performAction.apply(perfFuncArg),
                compFuncArg,
                compensationAction
        );
    }

    private static <PerfR, CompT> PerfR performOffsetTransaction(Supplier<PerfR> performAction,
                                                                 final CompT compFuncArg,
                                                                 final Consumer<CompT> compensationAction) {
        log.debug("Offset transaction has been initiated.");
        try {
            log.debug("Performing transactional processes.");
            return performAction.get();
        } catch (RuntimeException e) {
            log.error("Something went wrong in the transaction, invoking compensation functions.", e);
            compensationAction.accept(compFuncArg);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}