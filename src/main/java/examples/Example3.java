package examples;

import rx.Emitter;
import rx.Observable;

import static utils.Utils.println;
import static utils.Utils.sleep;


public class Example3 {

    public static void main(String[] args) throws Exception {

        /*

        Let's modify our observable so that it generates an error. We can catch the error and emit it as an
        Error event.

         */

        Observable<String> observable = Observable.create(emitter -> {

                    try {
                        sleep(2000);
                        println("Emitting Events...");
                        emitter.onNext("Hello World");
                        throw new Exception("Something went wrong!");
                    } catch (Exception ex) {
                        emitter.onError(ex);
                    }
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

        As before, note the execution order shown by the output...

        Observable created
        Subscribing now
        Emitting Events...
        Received Item Hello World
        Received Error Something went wrong!
        Subscribe complete
        Finished

        Note how the subscribe() call still executes synchronously.

        Note also how the "Hello World2" it is never received by the subscription - this is because it is emitted
        after the Error event and so is ignored.

         */

    }
}
