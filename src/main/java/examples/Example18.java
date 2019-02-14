package examples;

import rx.Emitter;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.CompletableFuture;

import static utils.Utils.*;

public class Example18 {

    public static void main(String[] args) throws Exception {


        /*

        The other way of resolving the issue presented in example 16 is to create our own operator
        to restore value of suffix from the outer context into the inner context when ever we need it.
        rxJava provides the capability for developers to create their own operators and execute them
        using the lift() operator.

        The class SuffixContextOperator is an example of just such an operator. When it is instantiated it
        copies the value of suffix in the local Context and stores it as localSuffix. Since the code
        'new SuffixContextOperator()' is executed in the outer context this captures the String "World".

        When the SuffixContextOperator is executed to handle and event being emitted down the chain of
        Observables OnCompleted(), OnError() or OnNext() will be executed which will restore localSuffix into
        suffix in the current context and then simply emit the received event on down the chain.

        This technique is used heavily in the muServiceBase. See the class RequestContextStashOperator
        for an example of it's implementation.

         */


        println("suffix in outer context = " + suffix.get());

        Observable<String> observable = createObservable("Hello ", 10)
                .observeOn(Schedulers.computation())
                .lift(new SuffixContextOperator())
                .map(item -> {
                            println("suffix in inner context = " + suffix.get());
                            return item + suffix.get();
                        }
                );

        println("observable created");

        println("Subscribing now");

        observable.subscribe(
                item -> println("Subscribe received " + item),
                error -> println("Subscribe received Error " + error.getMessage()),
                () -> println("Subscribe received Completed"));


        println("Subscribe complete");

        println("Waiting for all events to be emitted");

        sleep(10000);

        println("Finished");


        /*

        We can see this also resolve the problem...

        suffix in outer context = World
        observable created
        Subscribing now
        Subscribe complete
        Waiting for all events to be emitted
        Emitting Hello ...
        suffix in inner context = World
        Subscribe received Hello World
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

class SuffixContextOperator implements Observable.Operator<String, String> {

    private String localSuffix = suffix.get();

    @Override
    public Subscriber<? super String> call(Subscriber<? super String> subscriber) {

        return new Subscriber<String>() {

            @Override
            public void onCompleted() {
                suffix.set(localSuffix);
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                suffix.set(localSuffix);
                subscriber.onError(e);
            }

            @Override
            public void onNext(String s) {
                suffix.set(localSuffix);
                subscriber.onNext(s);
            }
        };
    }
}
