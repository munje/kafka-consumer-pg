package org.milan.test;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.milan.test.entity.MyEntity;
import org.milan.test.service.MyEntityService;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CounterConsumer {

    @Inject
    Logger logger;

    @Inject
    MyEntityService myEntityService;

    @Incoming("counter")
    @Blocking                                               // Work
    // @Blocking(ordered = false)                           // Does not work
    // @Blocking(value = "my-custom-pool", ordered = false) // Work
    // @Blocking(value = "my-custom-pool")                  // Work
    @Transactional
    public void consume(Integer counter) throws InterruptedException {
        logger.info("Received counter: " + counter);
        MyEntity a = myEntityService.findById(Long.valueOf(1));
        logger.infof("Entity before update: %s", a.getCounter());
        a.setCounter(counter);

        // simulate processing ...
        // Thread.sleep(100);
        logger.info("Finished processing: " + counter);
    }
}