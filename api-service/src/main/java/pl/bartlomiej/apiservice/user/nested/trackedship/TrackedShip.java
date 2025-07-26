package pl.bartlomiej.apiservice.user.nested.trackedship;

import java.util.Objects;

public record TrackedShip(String mmsi, String name) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TrackedShip that = (TrackedShip) o;
        return Objects.equals(mmsi, that.mmsi);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mmsi);
    }
}