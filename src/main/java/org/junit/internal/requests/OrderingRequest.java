package org.junit.internal.requests;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.InvalidOrderingException;
import org.junit.runner.manipulation.Ordering;

/** @since 4.13
 * Including chaning on Ordering on the constructor so the request can be order by different
 * order defined in the code
 *
 * */
public class OrderingRequest extends MemoizingRequest {
    private final Request request;
    private final Ordering ordering;

    public OrderingRequest(final Request request, final Ordering ordering) {

        this.ordering = ordering;
        this.request = new Request() {
            @Override
            public Runner getRunner() {
                try {
                    Runner runner = request.getRunner();
                    ordering.apply(runner);
                    return runner;
                } catch (InvalidOrderingException e) {
                    return new ErrorReportingRunner(ordering.getClass(), e);
                }
            }


        };
    }
    @Override
    protected Runner createRunner() {
        Runner runner = request.getRunner();
        try {
            ordering.apply(runner);
        } catch (InvalidOrderingException e) {
            return new ErrorReportingRunner(ordering.getClass(), e);
        }
        return runner;
    }
}
