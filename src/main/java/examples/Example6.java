package examples;

import rx.Emitter;
import rx.Observable;
import utils.MutableString;

import static utils.Utils.println;
import static utils.Utils.sleep;

public class Example6 {

    public static void main(String[] args) throws Exception {


    /*

    If an operator throws an exception, rxJava will normally catch the exception and convert it into an
    Error event that will be passed onto he next operator.

    See the call to doOnNext() below.

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
                .doOnNext(item -> {
                    throw new RuntimeException("Boom!");
                })
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

        The Error is not reported by the doOnError operator because it was not generated until after the doOnError
        operator had already functioned.

        Note how the exception thrown by the doOnNext Function is converted into an Error event, which is
        reported by the subscriber.

        Observable created
        Subscribing now
        doOnSubscribe got called
        Emitting Hello World...
        doOnTerminate got called
        Subscribe received Error Boom!
        Emitting Hello World2...
        Subscribe complete
        Finished

        Not also that, although the Observable we created tried to emit "Hello World2", it is never handled by any of
        the operators or the subscribe handlers. This is because the Observable we subscribed to has emitted and error
        and therefore will not emit any further items or events.

         */
    }

}
