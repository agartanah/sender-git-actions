package ru.gnivc.webhooknotifier.configuration;

import jakarta.jms.Queue;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import ru.gnivc.webhooknotifier.configuration.property.ArtemisConfigurationProperty;

@Configuration
public class ArtemisConfiguration {
    @Bean
    public MappingJackson2MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(ArtemisConfigurationProperty.TARGET_TYPE);
        converter.setTypeIdPropertyName(ArtemisConfigurationProperty.TYPE_ID);
        return converter;
    }

    @Bean
    public Queue myQueue() {
        return new ActiveMQQueue(ArtemisConfigurationProperty.QUEUE_ADDRESS);
    }
}
