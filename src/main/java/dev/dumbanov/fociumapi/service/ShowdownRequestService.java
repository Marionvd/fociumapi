package dev.dumbanov.fociumapi.service;

import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.entity.Showdown;
import dev.dumbanov.fociumapi.entity.ShowdownRequest;
import dev.dumbanov.fociumapi.entity.types.ShowdownRequestStatus;
import dev.dumbanov.fociumapi.exception.BadRequestException;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import dev.dumbanov.fociumapi.repository.ShowdownRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowdownRequestService {
    private final ShowdownRequestRepository showdownRequestRepository;
    private final ProfileRepository profileRepository;
    private final ShowdownService showdownService;

    public ShowdownRequest sendRequest(Profile sender, UUID receiverUuid, int durationMinutes) {
        if (sender.getUuid().equals(receiverUuid)) {
            throw new BadRequestException("You cannot challenge yourself.");
        }

        Profile receiver = profileRepository.findById(receiverUuid)
                .orElseThrow(() -> new BadRequestException("Opponent profile not found."));

        boolean hasPending = showdownRequestRepository
                .findByReceiverAndStatusOrderByCreatedAtDesc(receiver, ShowdownRequestStatus.PENDING)
                .stream()
                .anyMatch(r -> r.getSender().getUuid().equals(sender.getUuid()));

        if (hasPending) {
            throw new BadRequestException("You already have a pending request to this profile.");
        }

        ShowdownRequest request = new ShowdownRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setDurationMinutes(durationMinutes);
        return showdownRequestRepository.save(request);
    }

    public Showdown acceptRequest(Profile receiver, UUID requestUuid) {
        ShowdownRequest request = getRequestForReceiver(receiver, requestUuid);
        request.setStatus(ShowdownRequestStatus.ACCEPTED);
        showdownRequestRepository.save(request);
        return showdownService.createShowdown(request.getSender(), receiver, request.getDurationMinutes());
    }

    public void declineRequest(Profile receiver, UUID requestUuid) {
        ShowdownRequest request = getRequestForReceiver(receiver, requestUuid);
        request.setStatus(ShowdownRequestStatus.DECLINED);
        showdownRequestRepository.save(request);
    }

    public void cancelRequest(Profile sender, UUID requestUuid) {
        ShowdownRequest request = showdownRequestRepository.findById(requestUuid)
                .orElseThrow(() -> new BadRequestException("Request not found."));
        if (!request.getSender().getUuid().equals(sender.getUuid())) {
            throw new BadRequestException("You are not the sender of this request.");
        }
        if (request.getStatus() != ShowdownRequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be cancelled.");
        }
        request.setStatus(ShowdownRequestStatus.CANCELLED);
        showdownRequestRepository.save(request);
    }

    public List<ShowdownRequest> getIncomingRequests(Profile receiver) {
        return showdownRequestRepository.findByReceiverOrderByCreatedAtDesc(receiver);
    }

    public List<ShowdownRequest> getOutgoingRequests(Profile sender) {
        return showdownRequestRepository.findBySenderOrderByCreatedAtDesc(sender);
    }

    private ShowdownRequest getRequestForReceiver(Profile receiver, UUID requestUuid) {
        ShowdownRequest request = showdownRequestRepository.findById(requestUuid)
                .orElseThrow(() -> new BadRequestException("Request not found."));
        if (!request.getReceiver().getUuid().equals(receiver.getUuid())) {
            throw new BadRequestException("You are not the receiver of this request.");
        }
        if (request.getStatus() != ShowdownRequestStatus.PENDING) {
            throw new BadRequestException("Request is no longer pending.");
        }
        return request;
    }
}
