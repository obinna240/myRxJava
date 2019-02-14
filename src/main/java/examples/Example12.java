package examples;

import rx.Emitter;
import rx.Observable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static utils.Utils.println;
import static utils.Utils.sleep;

public class Example12 {

    public static void main(String[] args) throws Exception {

    /*

    The Merge operator is another handy operator for combining emitted items, in this case by emitting all the items
    from all the merged observables from a single observables.

     */

        Observable<String> o1 = createObservable("Hello", 2000);
        Observable<String> o2 = createObservable("World", 4000);
        Observable<String> o3 = createObservable("How", 1000);
        Observable<String> o4 = createObservable("Are", 3000);
        Observable<String> o5 = createObservable("You", 5000);
        Observable<String> o6 = Observable.just("?");

        println("observables created");


        Observable<String> observableMerge = Observable.merge(
                Arrays.asList(o1, o2, o3, o4, o5, o6));

        println("observableZip created");

        println("Subscribing now");

        observableMerge.subscribe(
                item -> println("Subscribe received " + item),
                error -> println("Subscribe received Error " + error.getMessage()),
                () -> println("Subscribe received Completed"));


        println("Subscribe complete");

        println("Waiting for all events to be emitted");

        sleep(8000);

        println("Finished");


        /*

        Here note how the subscription received the events in the same order that they are emitted.

        Note also that the ? is received by the subscribe before the subscribe completes - this is because it has
        been emitted using Observable.just()

        observables created
        observableZip created
        Subscribing now
        Subscribe received ?
        Subscribe complete
        Waiting for all events to be emitted
        Emitting How...
        Subscribe received How
        Emitting Hello...
        Subscribe received Hello
        Emitting Are...
        Subscribe received Are
        Emitting World...
        Subscribe received World
        Emitting You...
        Subscribe received You
        Subscribe received Completed
        Finished



        As a general rule if your mapping function is returning an Observable, you probably want to be using flatMap.

        Otherwise you want the map operator.

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
