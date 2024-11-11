package pl.bartlomiej.protectionservice.iploginprotection.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;
import pl.bartlomiej.protectionservice.iploginprotection.model.IpLoginProtectionRequest;
import pl.bartlomiej.protectionservice.iploginprotection.service.IpLoginProtectionService;

@RestController
@RequestMapping("v1/protection/rpc")
public class ProtectionRPCController {

    private final IpLoginProtectionService ipLoginProtectionService;

    public ProtectionRPCController(IpLoginProtectionService ipLoginProtectionService) {
        this.ipLoginProtectionService = ipLoginProtectionService;
    }

    @PostMapping("/execute-ip-login-protection")
    public ResponseEntity<ResponseModel<Void>> executeIpLoginProtection(@RequestBody final IpLoginProtectionRequest request) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<Void>(HttpStatus.OK, true)
                        .message(this.ipLoginProtectionService.executeIpLoginProtection(request))
                        .build()
        );
    }
}