package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

@RestController
@RequestMapping("/v1/suspect-logins")
public class SuspectLoginController {

    private final SuspectLoginService suspectLoginService;

    public SuspectLoginController(SuspectLoginService suspectLoginService) {
        this.suspectLoginService = suspectLoginService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SuspectLogin>> get(@PathVariable final String id, @RequestParam final String uid) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<SuspectLogin>(HttpStatus.OK, true)
                        .body(suspectLoginService.get(id, uid))
                        .build()
        );
    }
}
