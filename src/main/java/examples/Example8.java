package examples;

import rx.Emitter;
import rx.Observable;

import java.util.concurrent.CompletableFuture;

import static utils.Utils.println;
import static utils.Utils.sleep;

public class Example8 {

    public static void main(String[] args) throws Exception {


    /*

    In this example we have moved the code that creates our Observables into a method which will return an
    Observable that will respond with the given output after the given delay (once subscribed to).

    One of the most commonly used operators is 'map'. Similar to the Java 8 stream API, the map operator will
    take a received item and apply a function to emit another item.

    In the example below, the map operator is converting the String emitted from our first operator to an Integer
    and then square it, before emitting the result as an Integer (note how observableInteger is of type
    Observable<Integer>).

     */

        Observable<String> observableString = createObservable("42", 2000);

        println("observableString created");

        Observable<Integer> observableInteger = observableString
                .doOnNext(item -> println("Item before map is of type "+item.getClass().getSimpleName()))
                .map(item -> Integer.valueOf(item) * Integer.valueOf(item))
                .doOnNext(item -> println("Item after map is of type "+item.getClass().getSimpleName()));

        println("observableInteger created");

        println("Subscribing now");

        observableInteger.subscribe(
                item -> println("Subscribe received Item " + item),
                error -> println("Subscribe received Error " + error.getMessage()),
                () -> println("Subscribe received Completed"));


        println("Subscribe complete");

        println("Waiting for all events to be emitted");

        sleep(3000);

        println("Finished");


        /*

        Note here that we subscribed to observableInteger, but the code inside observableString still emitted its items
        when we subscribed. This is because observableInteger was created by taking observableString and bolting a
        map operator into it.

        As we progress onto more complicated chains of operators, we will see that when we subscribe to an observable
        ALL chained Observables will emit their items.

        observableString created
        observableInteger created
        Subscribing now
        Subscribe complete
        Waiting for all events to be emitted
        Emitting 42...
        Item before map is of type String
        Item after map is of type Integer
        Subscribe received Item 1764
        Subscribe received Completed
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
