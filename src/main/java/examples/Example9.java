package examples;

import rx.Emitter;
import rx.Observable;

import java.util.concurrent.CompletableFuture;

import static utils.Utils.println;
import static utils.Utils.sleep;

public class Example9 {

    public static void main(String[] args) throws Exception {


    /*

    The map operator is very handy when we want to bolt a synchronous mapping function onto an observable
    to change any items that it emits into something else.

    However, what if the mapping function itself is asynchronous?

    In the previous example we used a mapping function to change a String into an Integer. But what if the square
    function was some piece of code that ran asynchronously and returned you an Observable<Integer> that would only
    emit the result once it had calculated it?

    The createSquareObservable method below does exactly this... so lets look at how we can use it do square our input.

    We find that if we try to use it in a map operator we end up with a type of Observable<Observable<Integer>> -
    we have an Observable which will emit another Observable, which will emit an Integer, which is not very useful.

    The solution is to use the flatMap operator. This will use the item emitted by the Observable that
    createSquareObservable gave us as the result of the mapping function, rather that the Observable itself.

     */

        Observable<String> observableString = createObservable("42", 2000);

        println("observableString created");


        // Wrong - NOTE THE RETURN TYPE IF WE TRY TO USE MAP!!!
        Observable<Observable<Integer>> BADobservableInteger = observableString
                .doOnNext(item -> println("Item before map is of type " + item.getClass().getSimpleName()))
                .map(item -> createSquareObservable(item, 2000))
                .doOnNext(item -> println("Item after map is of type " + item.getClass().getSimpleName()));


        // Correct - Using flatMap produces the required result
        Observable<Integer> observableInteger = observableString
                .doOnNext(item -> println("Item before map is of type " + item.getClass().getSimpleName()))
                .flatMap(item -> createSquareObservable(item, 2000))
                .doOnNext(item -> println("Item after map is of type " + item.getClass().getSimpleName()));


        println("observableInteger created");

        println("Subscribing now");

        observableInteger.subscribe(
                item -> println("Subscribe received Item " + item),
                error -> println("Subscribe received Error " + error.getMessage()),
                () -> println("Subscribe received Completed"));


        println("Subscribe complete");

        println("Waiting for all events to be emitted");

        sleep(5000);

        println("Finished");


        /*

        We can see that now we use flatMap we get the expected result.

        observableString created
        observableInteger created
        Subscribing now
        Subscribe complete
        Waiting for all events to be emitted
        Emitting 42...
        Item before map is of type String
        Emitting 42 * 42...
        Item after map is of type Integer
        Subscribe received Item 1764
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

    private static Observable<Integer> createSquareObservable(String num, int delay) {
        return Observable.create(emitter ->
                        CompletableFuture.runAsync(() -> {
                            try {
                                sleep(delay);
                                println("Emitting " + num + " * " + num + "...");
                                emitter.onNext(Integer.valueOf(num) * Integer.valueOf(num));
                                emitter.onCompleted();
                            } catch (Exception ex) {
                                emitter.onError(ex);
                            }
                        })
                , Emitter.BackpressureMode.BUFFER);
    }
}
