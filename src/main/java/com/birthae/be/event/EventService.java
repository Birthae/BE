package com.birthae.be.event;

import com.birthae.be.event.entity.Event;
import lombok.RequiredArgsConstructor;
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
}
