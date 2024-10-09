package pl.bartlomiej.apiservice.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.apiservice.point.activepoint.ActivePoint;
import pl.bartlomiej.apiservice.point.activepoint.InactivePointFilter;
import pl.bartlomiej.apiservice.point.activepoint.service.ActivePointService;
import pl.bartlomiej.apiservice.point.service.PointService;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;
import reactor.core.publisher.Flux;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/points")
public class PointController {

    private final PointService pointService;
    private final ActivePointService activePointService;
    private final InactivePointFilter inactivePointFilter;

    public PointController(
            PointService pointService,
            ActivePointService activePointService,
            InactivePointFilter inactivePointFilter) {
        this.pointService = pointService;
        this.activePointService = activePointService;
        this.inactivePointFilter = inactivePointFilter;
    }

    @GetMapping
    public ResponseEntity<Flux<ResponseModel<Point>>> getPoints() {
        // ACTIVE LIST FILTRATION
        pointService.getPoints()
                .map(Point::mmsi)
                .collectList()
                .subscribe(mmsis ->
                        inactivePointFilter.filter(mmsis).subscribe()
                );

        // RESPONSE
        return ResponseEntity.ok(
                pointService.getPoints()
                        .flatMap(point ->
                                activePointService.addActivePoint(
                                        new ActivePoint(
                                                point.mmsi(),
                                                point.name()
                                        )
                                ).thenReturn(point)
                        )
                        .map(point ->
                                new ResponseModel.Builder<Point>(OK, OK.value())
                                        .body(point)
                                        .build()
                        )
        );
    }

}
