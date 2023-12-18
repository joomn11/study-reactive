package com.study.book.chapter13;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

public class PublisherProbeTest {

    @Test
    void test1() {
        PublisherProbe<String> probe = PublisherProbe.of(PublisherProbeTestExample.supplyStandbyPower());

        StepVerifier
            .create(PublisherProbeTestExample.processTask(PublisherProbeTestExample.supplyMainPower(), probe.mono()))
            .expectNextCount(1)
            .verifyComplete();

        probe.assertWasSubscribed();
        probe.assertWasRequested();
        probe.assertWasNotCancelled();
    }
}
