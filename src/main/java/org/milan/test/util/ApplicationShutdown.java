//package org.milan.test.util;
//
//import org.jboss.logging.Logger;
//import org.milan.test.service.PauseChannelService;
//
//import io.quarkus.runtime.ShutdownEvent;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.enterprise.event.Observes;
//import jakarta.inject.Inject;
//
//@ApplicationScoped
//public class ApplicationShutdown {
//
//    @Inject
//    Logger logger;
//
//    @Inject
//    PauseChannelService service;
//
//    public void onStop(@Observes ShutdownEvent event) throws InterruptedException {
//        logger.info("Received ShutdownEvent - Quarkus is stopping, pausing all Kafka channels first ...");
//
//        int sleepTime = 2000;
//
//        service.pauseAll();
//        logger.infof("Kafka channels are paused, starting sleep for %s ms ...", sleepTime);
//
//        Thread.sleep(sleepTime);
//
//        logger.info("Observed ShutdownEvent is completed, Quarkus shutdown will continue");
//    }
//}