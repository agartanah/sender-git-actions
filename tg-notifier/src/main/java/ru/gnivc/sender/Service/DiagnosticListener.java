package ru.gnivc.sender.Service;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticListener {
    private final Controllers.TelegramSender telegramSender;

    public DiagnosticListener(Controllers.TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    @KafkaListener(topics = "#{'git-notifications'}", groupId = "diagnostic-group")
    public void diagnose(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        System.out.println("🔍 DIAGNOSTIC LISTENER:");
        System.out.println("Topic: " + topic);
        System.out.println("Partition: " + partition);
        System.out.println("Offset: " + offset);
        System.out.println("Message: " + message);
        System.out.println("Length: " + message.length());

        //String message = String.format(
        //        "🔥 New Message in GITHUB 🔥\n" +
        //                "Repository: %s\n" +
        //                "Branch: %s\n" +
        //                "Author: %s\n" +
        //                "Commit: %s",
        //        event.repositoryName(),
        //        event.branch(),
        //        event.author(),
        //        event.commitMessage()
        //);

        telegramSender.sendMessageToChat(message);
    }
}