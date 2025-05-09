package com.birthae.be.event;

import com.birthae.be.common.dto.ResponseMessage;
import com.birthae.be.event.dto.CreateEventRequestDto;
import com.birthae.be.event.entity.Event;
import com.birthae.be.security.UserDetailsImpl;
import com.birthae.be.user.entity.User;
import com.birthae.be.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
public class EventController {
    private final EventService eventService;
    private final S3Util s3Util;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseMessage> createEvent(
            @ModelAttribute CreateEventRequestDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        String imageUrl = s3Util.uploadFile(request.getImage(), "birthae/images/event");

        Event createdEvent = eventService.createEvent(
                user.getUserId(),
                request.getTitle(),
                request.getDescription(),
                imageUrl
        );

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(createdEvent)
                .statusCode(HttpStatus.CREATED.value())
                .resultMessage("이벤트 생성 성공")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Page<Event> events = eventService.getAllEvents(page, pageSize);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(events)
                .statusCode(HttpStatus.OK.value())
                .resultMessage("이벤트 목록 조회 성공")
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<ResponseMessage> getEventById(@PathVariable("eventId") Long eventId) {
        Event event = eventService.getEventById(eventId);
        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(event)
                .statusCode(HttpStatus.OK.value())
                .resultMessage("이벤트 조회 성공")
                .build();
        return ResponseEntity.ok(responseMessage);
    }
}