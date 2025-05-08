package com.birthae.be.event;

import com.birthae.be.common.dto.ResponseMessage;
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
}