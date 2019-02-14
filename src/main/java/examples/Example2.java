package examples;

import rx.Emitter;
import rx.Observable;

import static utils.Utils.println;
import static utils.Utils.sleep;

public class Example2 {

    public static void main(String[] args) throws Exception {

        /*

        Let's take our Observable from Example1 and add a delay before we emit the events to make
        it clear what happens when we subscribe.

        Then let's subscribe to our observable. The subscribe method takes three functions to handle Items
        being emitted, Error events and Completion events respectively.

         */

        Observable<String> observable = Observable.create(emitter -> {

                    sleep(2000);
                    println("Emitting Events...");
                    emitter.onNext("Hello World");
                    emitter.onCompleted();
                    emitter.onNext("Hello World2");
                },
                Emitter.BackpressureMode.BUFFER);

        println("Observable created");

        println("Subscribing now");

        observable.subscribe(
                item -> println("Received Item " + item),
                error -> println("Received Error " + error.getMessage()),
                () -> println("Received Completed"));


        println("Subscribe complete");

        println("Finished");

        /*

        Note the execution order shown by the output...

        Observable created
        Subscribing now
        Emitting Events...
        Received Item Hello World
        Received Completed
        Subscribe complete
        Finished

        Note how the observable only starts emitting events once the subscribe() call is made.

        Note also how the subscribe() call executes synchronously - it does not complete until the Completed
        event is received from the observable.

        Finally, note how the "Hello World2" it is never received by the subscription - this is because it is emitted
        after the Completion event and so is ignored.

         */

    }

}
