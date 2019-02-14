package examples;

import rx.Emitter;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.currentTimeMillis;
import static utils.Utils.*;

public class Example13 {

    public static void main(String[] args) throws Exception {


    /*

    When an rxJava operator is handling an event emitted by an observable, it needs a thread to run the function
    that has been passed to the operator. rxJava has a number of different schedulers that it can use - see the
    rxJava documentation for more information.

    The most commonly used scheduler in the muServiceBase is the Computation Scheduler. This Scheduler has a small
    number of threads (typically one per processor core) that are designed to run non-blocking code through to
    completion without yielding.

    The code below creates 100 observables and delivers their items through a merge operation.

    Note the use of the observeOn operator to tell rxJava to use the Computation Scheduler for the map operation.

     */

        long startTime = currentTimeMillis();

        List<Observable<String>> observablesList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final int j = i;
            observablesList.add(
                    createObservable("Hello", 10)
                            .observeOn(Schedulers.computation())
                            .map(item -> item + " World " + j + " on " + threadName()));
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

        sleep(10000);

        println("Finished");


        /*

        Note the speed with which the operation completes and the fact that all the work was done by just 8 threads

        ....
        Subscribe received Hello World 87 on RxComputationScheduler-8
        Subscribe received Hello World 88 on RxComputationScheduler-1
        Subscribe received Hello World 90 on RxComputationScheduler-3
        Subscribe received Hello World 96 on RxComputationScheduler-1
        Subscribe received Hello World 91 on RxComputationScheduler-4
        Subscribe received Hello World 92 on RxComputationScheduler-5
        Subscribe received Hello World 93 on RxComputationScheduler-6
        Subscribe received Hello World 94 on RxComputationScheduler-7
        Subscribe received Hello World 95 on RxComputationScheduler-8
        Subscribe received Hello World 97 on RxComputationScheduler-2
        Subscribe received Hello World 99 on RxComputationScheduler-4
        Subscribe received Hello World 98 on RxComputationScheduler-3
        ***** Subscribe received Completed in 303ms *****


         */
    }

    private static Observable<String> createObservable(String output, int delay) {
        return Observable.create(emitter ->
                        CompletableFuture.runAsync(() -> {
                            try {
                                sleep(delay);
                                emitter.onNext(output);
                                emitter.onCompleted();
                            } catch (Exception ex) {
                                emitter.onError(ex);
                            }
                        })
                , Emitter.BackpressureMode.BUFFER);
    }

}
