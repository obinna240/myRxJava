package examples;

import rx.Emitter;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.CompletableFuture;

import static utils.Utils.*;

public class Example17 {

    public static void main(String[] args) throws Exception {


        /*

        One way to fix the problem presented in Example 16 is to capture the value of the ThreadLocal
        object into a non-ThreadLocal object in the outer context and then restore it back into the
        ThreadLocal in the inner context via a closure.

        In the code below we copy the value of the ThreadLocal in the outer context in 'localSuffix' and
        then set it back into ThreadLocal in the inner context.



         */


        println("suffix in outer context = " + suffix.get());

        String localSuffix = suffix.get();

        Observable<String> observable = createObservable("Hello ", 10)
                .observeOn(Schedulers.computation())
                .map(item -> {
                    suffix.set(localSuffix);
                    println("suffix in inner context = " + suffix.get());
                    return item + suffix.get();}
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

        We can see that this has now resolved the issue

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
