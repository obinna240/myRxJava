package examples;

import rx.Emitter;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.currentTimeMillis;
import static utils.Utils.*;

public class Example14 {

    public static void main(String[] args) throws Exception {


        /*

        The previous example worked because the code in the map operator function was non-blocking.

        However, imagine that the code in the map operator function was making an IO call to another service that
        was taking five seconds to respond.

        The code below simulates this.

        Note the catastrophic affect on the performance. The 8 threads in our Computation Scheduler are all stuck in
        the sleep(5000) that is simulating our IO.


         */

        long startTime = currentTimeMillis();

        List<Observable<String>> observablesList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final int j = i;
            observablesList.add(
                    createObservable("Hello", 10)
                            .observeOn(Schedulers.computation())
                            .map(item -> {
                                // Simulate an IO call to somewhere!!!
                                sleep(5000);
                                return item + " World " + j + " on " + threadName();
                            }));
        }

        println("observables created");

        Observable<String> observableMerge = Observable.merge(observablesList);

        println("observableMerge created");

        println("Subscribing now");


        observableMerge.subscribe(
                item -> println("Subscribe received " + item),
                error -> println("Subscribe received Error " + error.getMessage()),
                () -> println("***** Subscribe received Completed in " + (currentTimeMillis() - startTime) + "ms *****"));


        println("Subscribe complete");

        println("Waiting for all events to be emitted");

        sleep(120000);

        println("Finished");



        /*

        Note how it now takes over a minute for the Complete event to arrive...

        ....
        Subscribe received Hello World 91 on RxComputationScheduler-4
        Subscribe received Hello World 92 on RxComputationScheduler-5
        Subscribe received Hello World 93 on RxComputationScheduler-6
        Subscribe received Hello World 94 on RxComputationScheduler-7
        Subscribe received Hello World 98 on RxComputationScheduler-3
        Subscribe received Hello World 96 on RxComputationScheduler-1
        Subscribe received Hello World 97 on RxComputationScheduler-2
        Subscribe received Hello World 99 on RxComputationScheduler-4
        ***** Subscribe received Completed in 65188ms *****
        Finished


         */
    }

    private static Observable<String> createObservable(String output, int delay) {
        return Observable.create(emitter ->
                        CompletableFuture.runAsync(() -> {
                            try {
                                sleep(delay);
                                println("Emitting " + output + "...");
                                emitter.onNext(output);
                                emitter.onCompleted();
                            } catch (Exception ex) {
                                emitter.onError(ex);
                            }
                        })
                , Emitter.BackpressureMode.BUFFER);
    }
}
