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
}
