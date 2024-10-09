package pl.bartlomiej.apiservice.user.domain.dto;

import pl.bartlomiej.apiservice.user.nested.trackedship.TrackedShip;

import java.util.List;

// todo some mapper (maybe common for every service cause that approach will be implemented everywhere)
public record UserReadDto(String id, String username, String email, List<TrackedShip> trackedShips) {
}