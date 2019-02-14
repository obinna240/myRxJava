package examples;

import rx.Emitter;
import rx.Observable;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static utils.Utils.println;
import static utils.Utils.sleep;

public class Example11 {

    public static void main(String[] args) throws Exception {


    /*

    FlatMap is useful if we want to take the event emitted by one Observable and combine it with the
    with an event emitted from another Observable. But what if we have a large number of Observables that we
    want to combine the results from?

    The following code sets up a number of Observables that are running concurrently and then uses the zip operator
    to combine the results together.

    Note the use of Observable.just() - this will create a simple Observable which (on subscription) will immediately
    emit whatever object is specified, followed by a Completed event.

     */

        Observable<String> o1 = createObservable("Hello", 2000);
        Observable<String> o2 = createObservable("World", 4000);
        Observable<String> o3 = createObservable("How", 1000);
        Observable<String> o4 = createObservable("Are", 3000);
        Observable<String> o5 = createObservable("You", 5000);
        Observable<String> o6 = Observable.just("?");

        println("observables created");


        Observable<String> observableZip = Observable.zip(
                Arrays.asList(o1, o2, o3, o4, o5, o6),
                results -> Arrays.stream(results)
                        .map(result -> (String) result)
                        .collect(Collectors.joining(" ")));

        println("observableZip created");

        println("Subscribing now");

        observableZip.subscribe(
                item -> println("Subscribe received " + item),
                error -> println("Subscribe received Error " + error.getMessage()),
                () -> println("Subscribe received Completed"));


        println("Subscribe complete");

        println("Waiting for all events to be emitted");

        sleep(8000);

        println("Finished");


        /*

        Note the order the events are emitted in, but that we can zip them back together in the desired order
        because the array or observables maps one to one with the array of emitted items.

        observables created
        observableZip created
        Subscribing now
        Subscribe complete
        Waiting for all events to be emitted
        Emitting How...
        Emitting Hello...
        Emitting Are...
        Emitting World...
        Emitting You...
        Subscribe received Hello World How Are You ?
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
