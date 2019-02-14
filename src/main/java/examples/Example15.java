package examples;

import rx.Emitter;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.currentTimeMillis;
import static utils.Utils.*;

public class Example15 {

    public static void main(String[] args) throws Exception {


        /*

        The previous example showed the problems of performing blocking IO functions Observable chains.

        The code below is exactly the same as the previous example, except that we have changed swapped out
        the Computation Scheduler for the IO Scheduler, which is a scheduler with many more threads designed
        specifically for blocking IO type operations.

        Note the massive increase in the speed at which the Complete event arrives.

         */

        long startTime = currentTimeMillis();

        List<Observable<String>> observablesList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final int j = i;
            observablesList.add(
                    createObservable("Hello", 10)
                            .observeOn(Schedulers.io())
                            .map(item -> {
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

        sleep(60000);

        println("Finished");


        /*

        When you remove the 5000ms concurrent delay from the timings below, we can see that it has executed in 294ms

        Subscribe received Hello World 92 on RxIoScheduler-94
        Subscribe received Hello World 93 on RxIoScheduler-95
        Subscribe received Hello World 94 on RxIoScheduler-96
        Subscribe received Hello World 95 on RxIoScheduler-97
        Subscribe received Hello World 96 on RxIoScheduler-98
        Subscribe received Hello World 97 on RxIoScheduler-99
        Subscribe received Hello World 98 on RxIoScheduler-100
        Subscribe received Hello World 99 on RxIoScheduler-101
        ***** Subscribe received Completed in 5294ms *****
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
