package ru.gnivc.webhook.notifier.configuration;

import jakarta.jms.Queue;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import ru.gnivc.webhook.notifier.configuration.property.ArtemisConfigurationProperty;

@Configuration
@EnableConfigurationProperties(ArtemisConfigurationProperty.class)
public class ArtemisConfiguration {
    private final ArtemisConfigurationProperty property;

    public ArtemisConfiguration(ArtemisConfigurationProperty property) {
        this.property = property;
    }

    @Bean
    public MappingJackson2MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public Queue myQueue() {
        return new ActiveMQQueue(property.getQueueAddress());
    }
}
