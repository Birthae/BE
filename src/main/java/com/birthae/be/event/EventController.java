package com.birthae.be.event;

import com.birthae.be.event.dto.CreateEventRequestDto;
import com.birthae.be.event.entity.Event;
import com.birthae.be.security.UserDetailsImpl;
import com.birthae.be.user.entity.User;
import com.birthae.be.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
public class EventController {
    private final EventService eventService;
    private final S3Util s3Util;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Event> createEvent(
            @ModelAttribute CreateEventRequestDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        try {
            String imageUrl = null;
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                imageUrl = s3Util.uploadFile(request.getImage(), "birthae/images/event");
            }

            Event createdEvent = eventService.createEvent(
                    user.getUserId(),
                    request.getTitle(),
                    request.getDescription(),
                    imageUrl
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}