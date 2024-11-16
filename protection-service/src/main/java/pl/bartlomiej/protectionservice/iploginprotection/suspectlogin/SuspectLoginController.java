package pl.bartlomiej.protectionservice.iploginprotection.suspectlogin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

import java.security.Principal;

@RestController
@RequestMapping("/v1/suspect-logins")
public class SuspectLoginController {

    private final SuspectLoginService suspectLoginService;

    public SuspectLoginController(SuspectLoginService suspectLoginService) {
        this.suspectLoginService = suspectLoginService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SuspectLogin>> get(@PathVariable final String id, final Principal principal) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<SuspectLogin>(HttpStatus.OK, true)
                        .body(suspectLoginService.get(id, principal.getName()))
                        .build()
        );
    }
}