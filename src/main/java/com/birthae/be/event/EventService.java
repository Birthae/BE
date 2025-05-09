package com.birthae.be.event;

import com.birthae.be.common.exception.BizRuntimeException;
import com.birthae.be.event.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Event createEvent(Long userId, String title, String description, String imageUrl) {
        Event newEvent = new Event();
        newEvent.setUserId(userId);
        newEvent.setTitle(title);
        newEvent.setDescription(description);
        newEvent.setImageUrl(imageUrl);
        return eventRepository.save(newEvent);
    }

    public Page<Event> getAllEvents(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return eventRepository.findAll(pageable);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BizRuntimeException(String.valueOf(HttpStatus.NOT_FOUND.value()), "이벤트가 존재하지 않습니다."));
    }

    public Event updateEvent(Long eventId, Long userId, String title, String description, String imageUrl) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BizRuntimeException(String.valueOf(HttpStatus.NOT_FOUND.value()), "이벤트가 존재하지 않습니다."));

        if (!event.getUserId().equals(userId)) {
            throw new BizRuntimeException(String.valueOf(HttpStatus.FORBIDDEN.value()), "이벤트를 수정할 권한이 없습니다.");
        }

        if (title != null) {
            event.setTitle(title);
        }
        if (description != null) {
            event.setDescription(description);
        }
        if (imageUrl != null) {
            event.setImageUrl(imageUrl);
        }

        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BizRuntimeException(String.valueOf(HttpStatus.NOT_FOUND.value()), "이벤트가 존재하지 않습니다."));

        if (!event.getUserId().equals(userId)) {
            throw new BizRuntimeException(String.valueOf(HttpStatus.FORBIDDEN.value()), "이벤트를 삭제할 권한이 없습니다.");
        }

        event.setDltYsno("Y");
        eventRepository.save(event);
    }
}