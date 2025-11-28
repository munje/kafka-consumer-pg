package org.milan.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class CounterProducer {

    @Inject
    Logger logger;

    private AtomicInteger counter = new AtomicInteger(0);

    @Inject
    @Channel("counter-out")
    Emitter<Integer> priceEmitter;

    @Inject
    Vertx vertx;

    public void onStart(@Observes StartupEvent event) {
        vertx.setPeriodic(100L, id -> {
            runTask();
        });
    }

    private void runTask() {
        counter.incrementAndGet();
        priceEmitter.send(Message.of(counter.get())
                .withAck(() -> {
                    logger.infov("Send to counter-out topic: {0}", counter.get());
                    return CompletableFuture.completedFuture(null);
                })
                .withNack(throwable -> {
                    logger.errorv("Cannot send to counter-out topic. Message: {0}", counter.get());
                    return CompletableFuture.completedFuture(null);
                }));
    }
}