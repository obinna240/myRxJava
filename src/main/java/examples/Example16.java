package examples;

import rx.Emitter;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.CompletableFuture;

import static utils.Utils.*;

public class Example16 {

    public static void main(String[] args) throws Exception {


        /*

        Variables of type ThreadLocal are objects that can store differing values depending on the
        thread that they are accessed from. They are not part of rxJava, but are used by some frameworks
        (including Spring v4).

        They are included in these examples as they cause a specific problem when used in conjunction
        with rxJava (as they are in the muServiceBase).

        When Spring receives an incoming Web Request, it fires up a thread and calls the appropriate
        controller with a static ThreadLocal variable in scope. Inside the ThreadLocal variable, Spring
        stores away a load of context information relating to the web request that the controller is trying
        to Service.

        Spring makes the assumption that the Web Request will be handled by classic imperative style code -
        in other words, the thread that it has called the controller with will run around accessing various
        services and make various blocking IO calls, before eventually returning an HttpResponse object
        to the Spring framework ON THE SAME THREAD.

        The idea behind storing all the context information relating to the Web Request in a static
        ThreadLocal variable, is that, because it is on the Heap, it can be accessed from anywhere in
        the call stack, and will always have the context relating to the thread that is handling this
        Web Request.

        And indeed, some other parts of the Spring Framework assume that it will be there and leverage the
        information contained in it.

        But, as we have seen in the previous examples, rxJava used a functional reactive pattern were
        the initial thread that is handling the Web Request simply sets up all the Observables and chained
        operations, and then calls subscribe().

        All the rest of the code is executed on an assortment of rxJava Schedulers.

        The code below illustrates the problem. 'suffix' is a static ThreadLocal variable that
        contains the word that we want to print after "Hello".

        In the outer context - on the main execution thread - it has a value of "World".

        But within the map operator - on the computation thread - it has a value of null.

         */


        println("suffix in outer context = " + suffix.get());

        Observable<String> observable = createObservable("Hello ", 10)
                .observeOn(Schedulers.computation())
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

        sleep(8000);

        println("Finished");


        /*

        Note below how the ThreadLocal value is not available in the inner context.

        suffix in outer context = World
        observable created
        Subscribing now
        Subscribe complete
        Waiting for all events to be emitted
        Emitting Hello ...
        suffix in inner context = null
        Subscribe received Hello null
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
