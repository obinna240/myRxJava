package examples;

import rx.Emitter;
import rx.Observable;

import java.util.concurrent.CompletableFuture;

import static utils.Utils.println;
import static utils.Utils.sleep;

public class Example7 {


    public static void main(String[] args) throws Exception {


    /*

    So far all the Observables we have created have executed synchronously when we subscribe to them - there has only
    been one thread at work.

    When we subscribed, the subscribe method call blocked, whilst all the code in our initial Observable, and all the
    functions provided to our operators executed. Only once a Completed or Error event was received did the subscribe
    finish and we printed "Subscribe complete".

    However, in the muServiceBase, most Observables are wrapping asynchronous events that may emit events long after
    subscribe has happened.

    In the code below we have formed a closure over the emitter so that a separate thread can emit events after the
    subscription has happened.

     */

        Observable<String> observable = Observable.create(emitter ->
                        CompletableFuture.runAsync(() -> {
                            try {
                                sleep(2000);
                                println("Emitting Hello World...");
                                emitter.onNext("Hello World");
                                emitter.onCompleted();
                            } catch (Exception ex) {
                                emitter.onError(ex);
                            }
                        }),
                Emitter.BackpressureMode.BUFFER);

        println("Observable created");

        println("Subscribing now");

        observable.subscribe(
                item -> println("Subscribe received Item " + item),
                error -> println("Subscribe received Error " + error.getMessage()),
                () -> println("Subscribe received Completed"));


        println("Subscribe complete");

        println("Waiting for all events to be emitted");

        sleep(5000);

        println("Finished");


        /*

        Note the ordering of the events below - now the subscription complete immediately and we have to delay the
        exiting of the program to wait for the events to be emitted.

        Subscribing now
        Subscribe complete
        Waiting for all events to be emitted
        Emitting Hello World...
        Subscribe received Item Hello World
        Subscribe received Completed
        Finished


         */
    }
}
