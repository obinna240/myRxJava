package examples;

import rx.Emitter;
import rx.Observable;

import static utils.Utils.println;
import static utils.Utils.sleep;

public class Example4 {


    public static void main(String[] args) throws Exception {


    /*

    The Observable class exposes a large number of methods that allow you to interact with the items and events being
    emitted by an Observable object.

    Most of these methods return another Observable which allows these 'operators' to be chained together.

    Some examples of operators are...

    doOnNext() - Runs the function passed as an argument when an item is received
    doOnCompleted() - Runs the function passed as an argument when a Completion Event is received
    doOnError() - Runs the function passed as an argument when an Error Event is received
    doOnSubscribe() - Runs the function passed as an argument when a Subscribe Event is received
    doOnTerminate() - Runs the function passed as an argument when either and Completion or Error Event is received

     */

        Observable<String> observable = Observable.create(emitter -> {

                    sleep(2000);
                    println("Emitting Hello World...");
                    emitter.onNext("Hello World");
                    sleep(2000);
                    println("Emitting Hello World2...");
                    emitter.onNext("Hello World2");
                    emitter.onCompleted();
                },
                Emitter.BackpressureMode.BUFFER);

        println("Observable created");


        Observable<String> chainedObservable = observable
                .doOnError(ex -> println("doOnError got called " + ex.getMessage()))
                .doOnNext(item -> println("doOnNext got a item " + item))
                .doOnCompleted(() -> println("doOnCompleted got called"))
                .doOnTerminate(() -> println("doOnTerminate got called"))
                .doOnSubscribe(() -> println("doOnSubscribe got called"));


        println("Subscribing now");

        chainedObservable.subscribe(
                item -> println("Subscribe received Item " + item),
                error -> println("Subscribe received Error " + error.getMessage()),
                () -> println("Subscribe received Completed"));


        println("Subscribe complete");

        println("Finished");


        /*

        Note the ordering of the output here...

        Observable created
        Subscribing now
        doOnSubscribe got called
        Emitting Hello World...
        doOnNext got a item Hello World
        Subscribe received Item Hello World
        Emitting Hello World2...
        doOnNext got a item Hello World2
        Subscribe received Item Hello World2
        doOnCompleted got called
        doOnTerminate got called
        Subscribe received Completed
        Subscribe complete
        Finished



        Note how the "Hello World" item is emitted, triggers the doOnNext, and then arrives in the subscription handler
        BEFORE the "Hello World2" item is emitted, triggers the doOnNext, and arrives in the subscription handler.

        Note also how the operators execute in the order that they have been chained, but items and events which do
        not trigger a particular operator are simply passed onto the next operator. For example the Completed event
        passes through the doOnError and doOnNext operators without triggering any output, but the Completed event does
        trigger output when it passes through the onCompleted and onTerminated operators.


         */
    }

}
