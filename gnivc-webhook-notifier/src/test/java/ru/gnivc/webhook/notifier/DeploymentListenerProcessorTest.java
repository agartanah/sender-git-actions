package ru.gnivc.webhook.notifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gnivc.webhook.notifier.dto.response.DeploymentDto;
import ru.gnivc.webhook.notifier.service.TelegramSenderService;
import ru.gnivc.webhook.notifier.service.processor.DeploymentListenerProcessor;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeploymentListenerProcessorTest {
    @Mock
    private TelegramSenderService telegramSenderService;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private DeploymentListenerProcessor deploymentListenerProcessor;

    @BeforeEach
    void setUp() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        deploymentListenerProcessor = new DeploymentListenerProcessor(telegramSenderService, validator, objectMapper);
    }

    @Test
    void positiveDataTest_ValidSendsMessage() throws Exception {
        String json = """
                {
                    "repositoryName": "test repo",
                    "author": "test user",
                    "eventMessage": "production",
                    "eventUrl": "http://test.com"
                }
                """;

        DeploymentDto validEvent = new DeploymentDto(
                "test repo",
                "test user",
                "production",
                "http://test.com"
        );

        when(objectMapper.readValue(json, DeploymentDto.class)).thenReturn(validEvent);

        deploymentListenerProcessor.listen(json);

        verify(objectMapper, times(1)).readValue(json, DeploymentDto.class);
        verify(telegramSenderService, times(1)).sendMessageToChat(argThat(message ->
                message.contains("🔥 Деплой 🔥") &&
                        message.contains("Репозиторий: test repo") &&
                        message.contains("Состояние: production") &&
                        message.contains("Сделал деплой: test user") &&
                        message.contains("http://test.com")
        ));
    }

    @Test
    void negativeDataTest_DoesNotSendMessage() throws Exception {
        String invalidJson = "{invalid json}";

        when(objectMapper.readValue(invalidJson, DeploymentDto.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        deploymentListenerProcessor.listen(invalidJson);

        verify(telegramSenderService, never()).sendMessageToChat(anyString());
    }

    @Test
    void unexpectedDataTest_NullData_DoesNotSendMessage() throws Exception {
        String json = """
                {
                    "author": "test user",
                    "eventMessage": "production",
                    "eventUrl": "http://test.com"
                }
                """;

        DeploymentDto invalidEvent = new DeploymentDto(
                null,
                "test user",
                "production",
                "http://test.com"
        );

        when(objectMapper.readValue(json, DeploymentDto.class)).thenReturn(invalidEvent);

        deploymentListenerProcessor.listen(json);

        verify(telegramSenderService, never()).sendMessageToChat(anyString());
    }
}
