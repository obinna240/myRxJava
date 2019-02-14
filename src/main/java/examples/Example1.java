package examples;

import rx.Emitter;
import rx.Observable;

import static utils.Utils.println;

public class Example1 {


    public static void main(String[] args) throws Exception {

        /*

        The main class used by rxJava is the Observable.

        Observables can 'emit' three types of event...

        1. An item (Object) of the type specified by the Observables generic type
        2. A 'Complete' event indicating that no more items will be emitted
        3. An 'Error' event containing an Exception (this also indicates that no more items will be emitted


        The Observable class provides a static method to make the creation of Observables easy...

         */

        Observable<String> observable = Observable.create(emitter -> {

                    println("Emitting Events...");
                    emitter.onNext("Hello World");
                    emitter.onCompleted();
                },
                Emitter.BackpressureMode.BUFFER);

        println("Finished");

        /*

        The code above will create an observable that will emit a String "Hello World" and then a completion
        event, WHEN IT IS SUBSCRIBED TO.

        The static create method is given a function that has an 'emitter'. Items can be emitted from the Observable
        using...

        emitter.onNext()
        emitter.onComplete()
        emitter.onError()

        Note that the code above when run does NOT write "Emitting Events...". For an observable to start emitting
        events, it must first be subscribed to.

        BackpressureMode.BUFFER indicates what rxJava should do when it encounters back pressure - i.e. when
        items are emitted faster that down stream operators can handle them. In this case we want rxJava to
        buffer any such items.

         */

    }

}
