package ru.gnivc.sender;

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
import ru.gnivc.sender.dto.response.DeploymentEvent;
import ru.gnivc.sender.service.TelegramSender;
import ru.gnivc.sender.service.listener.DeploymentListener;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeploymentListenerTest {
    @Mock
    private TelegramSender telegramSender;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private DeploymentListener deploymentListener;

    @BeforeEach
    void setUp() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        deploymentListener = new DeploymentListener(telegramSender, objectMapper, validator);
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

        DeploymentEvent validEvent = new DeploymentEvent(
                "test repo",
                "test user",
                "production",
                "http://test.com"
        );

        when(objectMapper.readValue(json, DeploymentEvent.class)).thenReturn(validEvent);

        deploymentListener.listen(json);

        verify(objectMapper, times(1)).readValue(json, DeploymentEvent.class);
        verify(telegramSender, times(1)).sendMessageToChat(argThat(message ->
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

        when(objectMapper.readValue(invalidJson, DeploymentEvent.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        deploymentListener.listen(invalidJson);

        verify(telegramSender, never()).sendMessageToChat(anyString());
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

        DeploymentEvent invalidEvent = new DeploymentEvent(
                null,
                "test user",
                "production",
                "http://test.com"
        );

        when(objectMapper.readValue(json, DeploymentEvent.class)).thenReturn(invalidEvent);

        deploymentListener.listen(json);

        verify(telegramSender, never()).sendMessageToChat(anyString());
    }
}
