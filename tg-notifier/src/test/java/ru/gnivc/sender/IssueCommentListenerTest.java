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
import ru.gnivc.sender.dto.response.IssueCommentEvent;
import ru.gnivc.sender.service.TelegramSender;
import ru.gnivc.sender.service.listener.IssueCommentListener;

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
        issueCommentListener = new IssueCommentListener(telegramSender, objectMapper, validator);
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

        IssueCommentEvent validEvent = new IssueCommentEvent(
                "test repo",
                "test user",
                "This is a test comment",
                "http://test.com"
        );

        when(objectMapper.readValue(json, IssueCommentEvent.class)).thenReturn(validEvent);

        issueCommentListener.listen(json);

        verify(objectMapper, times(1)).readValue(json, IssueCommentEvent.class);
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

        when(objectMapper.readValue(invalidJson, IssueCommentEvent.class))
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

        IssueCommentEvent invalidEvent = new IssueCommentEvent(
                null,
                "test user",
                "This is a test comment",
                "http://test.com"
        );

        when(objectMapper.readValue(json, IssueCommentEvent.class)).thenReturn(invalidEvent);

        issueCommentListener.listen(json);

        verify(telegramSender, never()).sendMessageToChat(anyString());
    }
}
