package org.junit.internal.requests;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.InvalidOrderingException;
import org.junit.runner.manipulation.Ordering;

/** @since 4.13
 * Including changing on Ordering on the constructor so the request can be order by different
 * order defined in the code
 *
 * */
public class OrderingRequest extends MemoizingRequest {
    private final Request request;
    private final Ordering ordering;

    public OrderingRequest( Request request,  Ordering ordering) {
        final Request  RequestFinal =  request;
        final Ordering orderingFinal = ordering;
        this.ordering = ordering;
        this.request = new Request() {
            @Override
            public Runner getRunner() {
                try {
                    Runner runner = RequestFinal.getRunner();
                    orderingFinal.apply(runner);
                    return runner;
                } catch (InvalidOrderingException e) {
                    return new ErrorReportingRunner(orderingFinal.getClass(), e);
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
