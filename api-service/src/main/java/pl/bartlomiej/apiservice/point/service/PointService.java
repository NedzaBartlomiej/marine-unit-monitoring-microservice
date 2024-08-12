package pl.bartlomiej.apiservice.point.service;

import pl.bartlomiej.apiservice.point.Point;
import reactor.core.publisher.Flux;

public interface PointService {

    Flux<Point> getPoints();
}
