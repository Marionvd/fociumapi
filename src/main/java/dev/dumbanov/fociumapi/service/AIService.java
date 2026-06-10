package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.dto.task.TaskCompletionEvaluation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AIService {
    private final ChatClient chatClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    OpenAiChatOptions.Builder taskEvaluatorOptions() {
        return OpenAiChatOptions.builder()
                .model("gpt-4o-mini")
                .temperature(0.1)
                .maxTokens(2048);
    }

    String taskEvaluationPrompt = """
            You are an evaluator AI model.
            
            Your job is to assess whether a task has been completed successfully using the provided evidence file, task title, and task description.
            
            Inputs you will receive:
            - Task title
            - Task description
            - Evidence file (such as an image, document, screenshot, or other attachment)
            
            Evaluation rules:
            - Determine how well the evidence supports completion of the task.
            - Give higher points when:
              - the task is more difficult or complex,
              - the evidence is strong, clear, specific, and directly relevant,
              - the evidence convincingly proves the task was completed.
            - Give lower points when:
              - the evidence is weak, unclear, incomplete, or only partially related,
              - the task seems only partially completed,
              - the evidence does not sufficiently prove completion.
            - Consider both the task difficulty and the quality of the proof.
            - Be strict and objective.
            - If the evidence is missing, irrelevant, or insufficient, assign very low points, even 0 if necessary.
            
            Return your result in the following json format:
            {
            "points": <integer>,
            "feedback": <short explanation of why those points were assigned>
            }
            Guidelines for points:
            - Use a higher score for harder tasks with strong proof.
            - Use a lower score for easier tasks with weak proof.
            - The score should reflect both task difficulty and evidence quality.
            - Do not add any extra text outside the required format.
            
            Task title: %s,
            Task description: %s,
            Evidence file is given as well
            """;

    String buildTaskEvaluationPrompt(String title, String description) {
        return taskEvaluationPrompt.formatted(title, description);
    }

    public TaskCompletionEvaluation evaluateTaskCompletion(String title, String description, MultipartFile proof) {
        MimeType mimeType = MimeType.valueOf(Objects.requireNonNull(proof.getContentType()));

        Media media = Media.builder()
                .mimeType(mimeType)
                .data(proof.getResource())
                .name(proof.getOriginalFilename())
                .build();

        String prompt = buildTaskEvaluationPrompt(title, description);
        var userMessage = UserMessage.builder().text(prompt).media(media).build();
        var response = chatClient.prompt()
                .messages(userMessage).options(taskEvaluatorOptions()).call();
        return parseTaskCompletionEvaluation(response.content());
    }

    private TaskCompletionEvaluation parseTaskCompletionEvaluation(String response) {
        // Try to extract the first JSON object from the response in case the model added extra text
        String json = extractFirstJsonObject(response);
        try {
            return objectMapper.readValue(json, TaskCompletionEvaluation.class);
        } catch (JsonProcessingException e) {
            // Fallback: try to build a minimal default object (points = 0) to avoid crashing the service
            try {
                String fallback = "{\"points\":0,\"feedback\":\"Failed to parse model response: " + escapeForJson(e.getMessage()) + "\"}";
                return objectMapper.readValue(fallback, TaskCompletionEvaluation.class);
            } catch (JsonProcessingException ex) {
                // As a last resort, return null (or you may throw a runtime exception depending on your error handling policy)
                return null;
            }
        }
    }

    private String extractFirstJsonObject(String text) {
        if (text == null) return "{}";
        int start = text.indexOf('{');
        if (start == -1) return "{}";
        int depth = 0;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return text.substring(start, i + 1);
                }
            }
        }
        // If we never closed the JSON, return from first brace to end
        return text.substring(start);
    }

    private String escapeForJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
