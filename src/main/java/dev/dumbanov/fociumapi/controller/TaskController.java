package dev.dumbanov.fociumapi.controller;

import dev.dumbanov.fociumapi.dto.task.TaskCreateInput;
import dev.dumbanov.fociumapi.dto.task.TaskOutput;
import dev.dumbanov.fociumapi.dto.task.TaskUpdateInput;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.service.AIService;
import dev.dumbanov.fociumapi.service.AwarderService;
import dev.dumbanov.fociumapi.service.ProofValidationService;
import dev.dumbanov.fociumapi.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final AIService aiService;
    private final AwarderService awarderService;
    private final ProofValidationService proofValidationService;



    @PostMapping
    public ResponseEntity<?> createTask(@AuthenticationPrincipal Profile profile, @Valid @RequestBody TaskCreateInput input) {
        try {
            var task = taskService.createTask(profile, input);
            return ResponseEntity.created(URI.create("/api/tasks/" + task.getUuid())).body(TaskOutput.of(task));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{taskUuid}")
    public ResponseEntity<?> updateTask(@AuthenticationPrincipal Profile profile, @PathVariable UUID taskUuid, @RequestBody TaskUpdateInput input) {
        try {
            var task = taskService.updateTask(profile, taskUuid, input);
            return ResponseEntity.ok(TaskOutput.of(task));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{taskUuid}")
    public ResponseEntity<?> deleteTask(@AuthenticationPrincipal Profile profile, @PathVariable UUID taskUuid) {
        try {
            taskService.deleteTask(profile, taskUuid);
            return ResponseEntity.ok(Map.of("message", "Task deleted successfully."));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{taskUuid}")
    public ResponseEntity<?> getTask(@AuthenticationPrincipal Profile profile, @PathVariable UUID taskUuid) {
        try {
            var task = taskService.getTask(taskUuid);
            return ResponseEntity.ok(TaskOutput.of(task));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{taskUuid}/complete")
    public ResponseEntity<?> completeTask(@AuthenticationPrincipal Profile profile, @PathVariable UUID taskUuid, MultipartFile file) {
        try {
            var task = taskService.getTask(taskUuid);

            proofValidationService.validateProofTimestamp(file, task);

            var res = aiService.evaluateTaskCompletion(task.getTitle(), task.getDescription(), file);

            awarderService.awardTaskCompletion(res, task, profile);
            return ResponseEntity.ok(Map.of("evaluation",res));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

