package ru.gnivc.webhook.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.gnivc.webhook.receiver.configuration.KafkaConfig;

@EnableConfigurationProperties(KafkaConfig.class)
@SpringBootApplication
public class WebhookReceiverApplication {
    public static void main(String[] args){
        SpringApplication.run(WebhookReceiverApplication.class, args);
    }
}