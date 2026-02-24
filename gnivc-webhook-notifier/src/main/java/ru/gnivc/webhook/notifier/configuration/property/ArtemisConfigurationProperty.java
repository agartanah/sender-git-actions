package ru.gnivc.webhook.notifier.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jms.support.converter.MessageType;

@ConfigurationProperties(prefix = "ru.gnivc.webhooknotifier.artemis")
public class ArtemisConfigurationProperty {

    private String queueAddress;

    private String concurrency;

    public String getQueueAddress() {
        return queueAddress;
    }

    public void setQueueAddress(String queueAddress) {
        this.queueAddress = queueAddress;
    }

    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency;
    }
}
