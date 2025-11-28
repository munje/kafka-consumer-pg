package org.milan.test.service;

import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.ChannelRegistry;
import io.smallrye.reactive.messaging.PausableChannel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PauseChannelService {

    private static final Logger LOG = Logger.getLogger(PauseChannelService.class);

    @Inject
    ChannelRegistry registry;

    public void pauseAll() {
        LOG.info("Pausing all pausable incoming channels");
        registry.getIncomingNames().forEach(channelName -> {
            PausableChannel pausable = registry.getPausable(channelName);
            if (pausable != null) {
                pausable.pause();
                LOG.infof("Channel is paused: %s", channelName);
            } else {
                LOG.debugf("Channel %s is not pausable (no PausableChannel)", channelName);
            }
        });
    }

    public void resumeAll() {
        LOG.info("Resuming all pausable incoming channels");
        registry.getIncomingNames().forEach(channelName -> {
            PausableChannel pausable = registry.getPausable(channelName);
            if (pausable != null) {
                pausable.resume();
                LOG.infof("Channel is resumed: %s", channelName);
            } else {
                LOG.debugf("Channel %s is not pausable (no PausableChannel)", channelName);
            }
        });
    }
}