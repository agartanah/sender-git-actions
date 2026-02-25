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
import ru.gnivc.webhook.notifier.dto.response.IssueCommentDto;
import ru.gnivc.webhook.notifier.service.TelegramSender;
import ru.gnivc.webhook.notifier.service.processor.IssueCommentListener;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueCommentListenerTest {
    @Mock
    private TelegramSender telegramSender;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private IssueCommentListener issueCommentListener;

    @BeforeEach
    void setUp() {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        issueCommentListener = new IssueCommentListener(telegramSender, validator, objectMapper);
    }

    @Test
    void positiveDataTest_ValidSendsMessage() throws Exception {
        String json = """
                {
                    "repositoryName": "test repo",
                    "author": "test user",
                    "eventMessage": "This is a test comment",
                    "eventUrl": "http://test.com"
                }
                """;

        IssueCommentDto validEvent = new IssueCommentDto(
                "test repo",
                "test user",
                "This is a test comment",
                "http://test.com"
        );

        when(objectMapper.readValue(json, IssueCommentDto.class)).thenReturn(validEvent);

        issueCommentListener.listen(json);

        verify(objectMapper, times(1)).readValue(json, IssueCommentDto.class);
        verify(telegramSender, times(1)).sendMessageToChat(argThat(message ->
                message.contains("🔥 Комментарий в test repo 🔥") &&
                        message.contains("Комментарий от: test user") &&
                        message.contains("«This is a test comment»") &&
                        message.contains("http://test.com")
        ));
    }

    @Test
    void negativeDataTest_DoesNotSendMessage() throws Exception {
        String invalidJson = "{invalid json}";

        when(objectMapper.readValue(invalidJson, IssueCommentDto.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});

        issueCommentListener.listen(invalidJson);

        verify(telegramSender, never()).sendMessageToChat(anyString());
    }

    @Test
    void unexpectedDataTest_NullData_DoesNotSendMessage() throws Exception {
        String json = """
                {
                    "author": "test user",
                    "This is a test comment",
                    "http://test.com"
                }
                """;

        IssueCommentDto invalidEvent = new IssueCommentDto(
                null,
                "test user",
                "This is a test comment",
                "http://test.com"
        );

        when(objectMapper.readValue(json, IssueCommentDto.class)).thenReturn(invalidEvent);

        issueCommentListener.listen(json);

        verify(telegramSender, never()).sendMessageToChat(anyString());
    }
}
