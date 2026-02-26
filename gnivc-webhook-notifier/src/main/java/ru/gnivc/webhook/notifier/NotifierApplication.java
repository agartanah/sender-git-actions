package ru.gnivc.webhook.notifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.gnivc.webhook.notifier.configuration.property.TelegramProperty;

@EnableConfigurationProperties(TelegramProperty.class)
@SpringBootApplication
public class NotifierApplication {
    public static void main(String[] args){
        SpringApplication.run(NotifierApplication.class, args);
    }
}