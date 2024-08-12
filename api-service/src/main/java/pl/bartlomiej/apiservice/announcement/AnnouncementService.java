package pl.bartlomiej.apiservice.announcement;

import reactor.core.publisher.Mono;

public interface AnnouncementService {
    Mono<Void> announce(Announcement announcement);
}
