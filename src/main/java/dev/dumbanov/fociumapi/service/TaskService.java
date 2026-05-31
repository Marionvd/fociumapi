package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.dto.task.TaskCreateInput;
import dev.dumbanov.fociumapi.dto.task.TaskUpdateInput;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Task;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
	private final TaskRepository taskRepository;

	public List<Task> getProfileTasks(Profile profile) {
		return taskRepository.findAllByProfile(profile);
	}
	public Task getTask(UUID uuid) {
		if(!taskRepository.existsById(uuid)) {
			throw new BadRequestException("Task not found.");
		}
		return taskRepository.findById(uuid).get();
	}

	public Task createTask(Profile profile, TaskCreateInput input) {
		Task task = new Task();
		task.setTitle(input.title());
		task.setDescription(input.description());
		task.setDeadline(input.deadline());
		task.setProfile(profile);
		return taskRepository.save(task);
	}

	public Task updateTask(Profile profile, UUID taskUuid, TaskUpdateInput input) {
		Task task = taskRepository.findTaskByUuidAndProfile_Uuid(taskUuid, profile.getUuid())
				.orElseThrow(() -> new BadRequestException("Task not found."));

		if (input.title() != null) {
			if (input.title().isBlank()) {
				throw new BadRequestException("Task title cannot be blank.");
			}
			task.setTitle(input.title());
		}

		if (input.description() != null) {
			task.setDescription(input.description());
		}

		if (input.deadline() != null) {
			task.setDeadline(input.deadline());
		}

		if (input.awardedPoints() != null) {
			task.setAwardedPoints(input.awardedPoints());
		}

		if (input.aiFeedback() != null) {
			task.setAiFeedback(input.aiFeedback());
		}

		return taskRepository.save(task);
	}

	public void deleteTask(Profile profile, UUID taskUuid) {
		Task task = taskRepository.findTaskByUuidAndProfile_Uuid(taskUuid, profile.getUuid())
				.orElseThrow(() -> new BadRequestException("Task not found."));

		taskRepository.delete(task);
	}
}
