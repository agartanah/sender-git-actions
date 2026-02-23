package ru.gnivc.webhookproducer.configuration.property;

import org.springframework.jms.support.converter.MessageType;

public class ArtemisConfigurationProperty {
    public static final String QUEUE_ADDRESS = "git-actions-notify";
    public static final MessageType TARGET_TYPE = MessageType.TEXT;
    public static final String TYPE_ID = "_type";
}
