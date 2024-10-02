package org.mongodb.kitchensink.service;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.mongodb.kitchensink.model.Member;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    private final ApplicationEventPublisher eventPublisher;

    public EventProducer(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(Member member) {
        eventPublisher.publishEvent(member);
    }
}

