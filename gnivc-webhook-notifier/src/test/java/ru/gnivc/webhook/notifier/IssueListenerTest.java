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
import ru.gnivc.webhook.notifier.dto.response.IssueDto;
import ru.gnivc.webhook.notifier.service.TelegramSender;
import ru.gnivc.webhook.notifier.service.processor.IssueListener;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueListenerTest {
    @Mock
    private TelegramSender telegramSender;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private IssueListener issueListener;

    @BeforeEach
    void setUp() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        issueListener = new IssueListener(telegramSender, objectMapper, validator);
    }

    @Test
    void positiveDataTest_ValidSendsMessage() throws Exception {
        String json = """
                {
                    "repositoryName": "test repo",
                    "author": "test author",
                    "eventMessage": {
                        "action": "opened",
                        "issueTitle": "test title",
                        "issueNumber": 1
                    },
                    "eventUrl": "http://test.com"
                }
                """;

        IssueDto validEvent = new IssueDto(
                "test repo",
                "test author",
                new IssueDto.IssueMessage("opened", "test title", 1),
                "http://test.com"
        );

        when(objectMapper.readValue(json, IssueDto.class)).thenReturn(validEvent);

        issueListener.listen(json);

        verify(objectMapper, times(1)).readValue(json, IssueDto.class);
        verify(telegramSender, times(1)).sendMessageToChat(argThat(message ->
                message.contains("🔥 Проблема test title #1 : Открыта 🔥") &&
                        message.contains("Репозиторий: test repo") &&
                        message.contains("Совершил действие: test author") &&
                        message.contains("http://test.com")
        ));
    }

    @Test
    void negativeDataTest_DoesNotSendMessage() throws Exception {
        String invalidJson = "{invalid json}";
        when(objectMapper.readValue(invalidJson, IssueDto.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        issueListener.listen(invalidJson);

        verify(telegramSender, never()).sendMessageToChat(anyString());
    }

    @Test
    void unexpectedDataTest_NullData_DoesNotSendMessage() throws Exception {
        String json = """
                {
                    "author": "test author",
                    "eventMessage": {
                        "action": "opened",
                        "issueNumber": 1
                    },
                    "eventUrl": "http://test.com"
                }
                """;

        IssueDto validEvent = new IssueDto(
                null,
                "test author",
                new IssueDto.IssueMessage("opened", null, 1),
                "http://test.com"
        );

        when(objectMapper.readValue(json, IssueDto.class)).thenReturn(validEvent);

        issueListener.listen(json);

        verify(telegramSender, never()).sendMessageToChat(anyString());
    }
}