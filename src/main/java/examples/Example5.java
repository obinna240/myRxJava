package examples;

import rx.Emitter;
import rx.Observable;
import utils.MutableString;

import static utils.Utils.println;
import static utils.Utils.sleep;

public class Example5 {


    public static void main(String[] args) throws Exception {


    /*

    So far we have been using Observables that have a generic type of String. As such, the items that these observables
    emit are immutable. We can of course emit a mutable object and use onNext to modify it. See the call to doOnNext()
    below.

    However, the approach below is not recommended. A better approach is to emit immutable items and then use the map()
    operator to create new immutable objects to pass to the next operator. (More on this in example 8).

     */

        Observable<MutableString> observable = Observable.create(emitter -> {

                    sleep(2000);
                    println("Emitting Hello World...");
                    emitter.onNext(new MutableString("Hello World"));
                    sleep(2000);
                    println("Emitting Hello World2...");
                    emitter.onNext(new MutableString("Hello World2"));
                    emitter.onCompleted();
                },
                Emitter.BackpressureMode.BUFFER);

        println("Observable created");


        Observable<MutableString> chainedObservable = observable
                .doOnError(ex -> println("doOnError got called " + ex.getMessage()))
                .doOnNext(item -> item.setString(item.getString().toUpperCase()))
                .doOnCompleted(() -> println("doOnCompleted got called"))
                .doOnTerminate(() -> println("doOnTerminate got called"))
                .doOnSubscribe(() -> println("doOnSubscribe got called"));


        println("Subscribing now");

        chainedObservable.subscribe(
                item -> println("Subscribe received Item " + item.getString()),
                error -> println("Subscribe received Error " + error.getMessage()),
                () -> println("Subscribe received Completed"));


        println("Subscribe complete");

        println("Finished");


        /*

        Note how the doOnNext function mutates the MutableString as it passes down the chain.

        Observable created
        Subscribing now
        doOnSubscribe got called
        Emitting Hello World...
        Subscribe received Item HELLO WORLD
        Emitting Hello World2...
        Subscribe received Item HELLO WORLD2
        doOnCompleted got called
        doOnTerminate got called
        Subscribe received Completed
        Subscribe complete
        Finished

         */
    }

}


