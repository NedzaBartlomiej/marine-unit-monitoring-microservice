package pl.bartlomiej.protectionservice.iploginprotection.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;
import pl.bartlomiej.protectionservice.iploginprotection.model.IpLoginProtectionRequest;
import pl.bartlomiej.protectionservice.iploginprotection.service.IpLoginProtectionService;

import java.security.Principal;

@RestController
@RequestMapping("/v1/ip-login-protection/rpc")
public class ProtectionRPCController {

    private final IpLoginProtectionService ipLoginProtectionService;

    public ProtectionRPCController(IpLoginProtectionService ipLoginProtectionService) {
        this.ipLoginProtectionService = ipLoginProtectionService;
    }

    @PreAuthorize("hasRole('IP_LOGIN_PROTECTOR')")
    @PostMapping("/protect-login")
    public ResponseEntity<ResponseModel<Void>> protectLogin(@RequestBody final IpLoginProtectionRequest request) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<Void>(HttpStatus.OK, true)
                        .message(this.ipLoginProtectionService.executeIpLoginProtection(request))
                        .build()
        );
    }

    @PostMapping("/trust-ip")
    public ResponseEntity<ResponseModel<Void>> trustIp(@RequestParam final String suspectLoginId, final Principal principal) {
        ipLoginProtectionService.trustIp(suspectLoginId, principal.getName());
        return ResponseEntity.ok(
                new ResponseModel.Builder<Void>(HttpStatus.OK, true)
                        .message("Successfully trusted ip address.")
                        .build()
        );
    }
}