package pl.bartlomiej.apiservice.shippoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/points")
public class ShipPointController {

    private final ShipMapManager shipMapManager;

    public ShipPointController(ShipMapManager shipMapManager) {
        this.shipMapManager = shipMapManager;
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<ShipPoint>>> getPoints() {
        return ResponseEntity.ok(
                new ResponseModel.Builder<List<ShipPoint>>(OK, true)
                        .body(shipMapManager.getActiveShipPoints())
                        .build()
        );
    }
}