package org.milan.test;

import java.util.concurrent.atomic.LongAdder;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.milan.test.entity.MyEntity;
import org.milan.test.service.MyEntityService;
import org.milan.test.service.PauseChannelService;

import io.quarkus.runtime.ShutdownEvent;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CounterConsumer {

    private final LongAdder consumerCounter = new LongAdder();

    @Inject
    Logger logger;

    @Inject
    PauseChannelService service;

    @Inject
    MyEntityService myEntityService;

    @Incoming("counter")
    // @Blocking // Work
    // @Blocking(ordered = false) // Does not work
    @Blocking(value = "my-custom-pool", ordered = false) // Work
    // @Blocking(value = "my-custom-pool") // Work
    @Transactional
    public void consume(Integer counter) throws InterruptedException {

        consumerCounter.increment();
        logger.infof("Next consumerCounter value: %s", consumerCounter.intValue());
        try {
            logger.info("Received counter: " + counter);
            MyEntity a = myEntityService.findById(Long.valueOf(1));
            logger.infof("Entity before update: %s", a.getCounter());
            a.setCounter(counter);

            // Thread.sleep(100);
            logger.info("Finished processing: " + counter);
        } finally {
            consumerCounter.decrement();
            logger.infof("consumerCounter after decrement: %s", consumerCounter.intValue());
        }
    }

    public void onStop(@Observes ShutdownEvent event) throws InterruptedException {

        logger.info("Received ShutdownEvent - Quarkus is stopping, pausing all Kafka channels first ...");
        service.pauseAll();
        logger.info("All Kafka channels are paused");

        // Thread.sleep(500);

        long start = System.currentTimeMillis();
        long timeout = 10000;

        logger.infof("Received ShutdownEvent - Quarkus is stopping, check if there are any processing messages ...");

        while (consumerCounter.intValue() > 0 && System.currentTimeMillis() - start < timeout) {
            logger.infof("consumerCounter is bigger than 0: %s, going to sleep 100ms", consumerCounter.intValue());
            Thread.sleep(100);
        }
        logger.infof("onStop processing is finished");
    }

}