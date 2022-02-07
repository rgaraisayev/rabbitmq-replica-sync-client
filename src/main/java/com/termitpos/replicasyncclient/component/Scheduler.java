package com.termitpos.replicasyncclient.component;

import com.termitpos.replicasyncclient.model.constant.EntityNames;
import com.termitpos.replicasyncclient.service.ReplicaClientService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.termitpos.replicasyncclient.configuration.RabbitMQConfig.EXCHANGE_EX1;
import static com.termitpos.replicasyncclient.configuration.RabbitMQConfig.ROUTING_KEY_R1;


@Component
public class Scheduler {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final ReplicaClientService replicaClientService;

    public Scheduler(ReplicaClientService replicaClientService) {
        this.replicaClientService = replicaClientService;
    }

    @Scheduled(fixedDelay = 10_000)
    public void scheduleSales() {
        CountDownLatch latch = new CountDownLatch(2);
        executor.execute(() -> {
            replicaClientService.sendUpstreamData(EntityNames.saleRelatedEntitiesPart1, EXCHANGE_EX1, ROUTING_KEY_R1);
            latch.countDown();
        });
        executor.execute(() -> {
            replicaClientService.sendUpstreamData(EntityNames.saleRelatedEntitiesPart2, EXCHANGE_EX1, ROUTING_KEY_R1);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 15_000)
    public void scheduleOther() {
        CountDownLatch latch = new CountDownLatch(2);
        executor.execute(() -> {
            replicaClientService.sendUpstreamData(EntityNames.otherEntitiesPart1, EXCHANGE_EX1, ROUTING_KEY_R1);
            latch.countDown();
        });
        executor.execute(() -> {
            replicaClientService.sendUpstreamData(EntityNames.otherEntitiesPart2, EXCHANGE_EX1, ROUTING_KEY_R1);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
