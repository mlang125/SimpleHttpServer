package server;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PooledRequestScheduler implements RequestScheduler {


    private static Executor pool;

    public PooledRequestScheduler(int threads) {
        pool = Executors.newFixedThreadPool(threads);
    }

    @Override
    public void schedule(RequestProcessor requestProcessor) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                requestProcessor.process();
            }
        };
        pool.execute(runnable);


    }


}
