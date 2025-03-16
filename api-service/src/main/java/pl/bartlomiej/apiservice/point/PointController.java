package pl.bartlomiej.apiservice.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.apiservice.point.activepoint.ActivePoint;
import pl.bartlomiej.apiservice.point.activepoint.InactivePointFilter;
import pl.bartlomiej.apiservice.point.activepoint.service.ActivePointService;
import pl.bartlomiej.apiservice.point.service.PointService;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;

import java.util.List;

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
    public ResponseEntity<ResponseModel<List<Point>>> getPoints() {
        List<Point> points = pointService.getPoints();
        // ACTIVE LIST FILTRATION
        List<String> currPointsMmsis = points.stream()
                .map(Point::mmsi)
                .toList();
        inactivePointFilter.filter(currPointsMmsis);

        // ActivePoint SYNC todo -> !(To be optimized, described in the ActivePoint.class)!
        points.forEach(point -> activePointService.addActivePoint(
                new ActivePoint(
                        point.mmsi(),
                        point.name()
                )
        ));

        // RESPONSE
        return ResponseEntity.ok(
                new ResponseModel.Builder<List<Point>>(OK, true)
                        .body(points)
                        .build()
        );
    }
}