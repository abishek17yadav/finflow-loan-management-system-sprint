package com.finflow.admin.service;

import com.finflow.admin.dto.LoanMessage;
import com.finflow.admin.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoanMessageListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumeMessage(LoanMessage message) {
        log.info("#### Notification: Received New Loan Application Message ####");
        log.info("Loan ID: {}", message.getId());
        log.info("User: {}", message.getUsername());
        log.info("Amount: {}", message.getAmount());
        log.info("Purpose: {}", message.getPurpose());
        log.info("Status: {}", message.getStatus());
        log.info("############################################################");
    }
}
