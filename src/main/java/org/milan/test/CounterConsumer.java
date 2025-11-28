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
    @Blocking
    // @Blocking(ordered = false)
    // @Blocking(value = "my-custom-pool")
    @Transactional
    public void consume(Integer counter) throws InterruptedException {
        logger.info("Received counter: " + counter);
        MyEntity a = myEntityService.findById(Long.valueOf(1));
        logger.infof("Entity before update: %s", a.getCounter());
        a.setCounter(counter);

        // Thread.sleep(1000);
        logger.info("Finished processing: " + counter);
    }
}