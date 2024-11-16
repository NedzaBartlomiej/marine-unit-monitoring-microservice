package pl.bartlomiej.devservice.developer.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.devservice.developer.domain.AppDeveloperEntity;
import pl.bartlomiej.devservice.developer.domain.dto.DeveloperRegisterDto;
import pl.bartlomiej.devservice.developer.service.DeveloperService;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

@RestController
@RequestMapping("/v1/developers")
public class DeveloperController {

    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @PostMapping
    public ResponseEntity<ResponseModel<AppDeveloperEntity>> create(@RequestBody @Valid final DeveloperRegisterDto developerRegisterDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseModel.Builder<AppDeveloperEntity>(HttpStatus.CREATED, true)
                        .body(developerService.create(developerRegisterDto, "127.0.0.1"))
                        .build()
                );
    }

    @PreAuthorize("hasRole('IP_LOGIN_PROTECTOR')")
    @GetMapping("/{id}/trustedIpAddresses")
    public ResponseEntity<ResponseModel<Boolean>> verifyIp(@PathVariable String id,
                                                           @RequestParam String ipAddress) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<Boolean>(HttpStatus.OK, true)
                        .body(developerService.verifyIp(id, ipAddress))
                        .build()
        );
    }

    @PreAuthorize("hasRole('IP_LOGIN_PROTECTOR')")
    @PostMapping("/{id}/trustedIpAddresses")
    public ResponseEntity<ResponseModel<Void>> trustIp(@PathVariable String id,
                                                       @RequestParam String ipAddress) {
        developerService.trustIp(id, ipAddress);
        return ResponseEntity.ok(
                new ResponseModel.Builder<Void>(HttpStatus.OK, true)
                        .build()
        );
    }
}
