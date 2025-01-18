package pl.bartlomiej.devservice.developer.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.devservice.developer.domain.AppDeveloperEntity;
import pl.bartlomiej.devservice.developer.domain.dto.DeveloperRegisterDto;
import pl.bartlomiej.devservice.developer.service.DeveloperService;
import pl.bartlomiej.loginservices.IdmServiceRepUserCreationDto;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;

@RestController
@RequestMapping("/v1/developers")
public class DeveloperController {

    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseModel<AppDeveloperEntity>> register(@RequestBody @Valid final DeveloperRegisterDto developerRegisterDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseModel.Builder<AppDeveloperEntity>(HttpStatus.CREATED, true)
                        .body(developerService.register(developerRegisterDto, "127.0.0.1"))
                        .build()
                );
    }

    @PreAuthorize("hasRole('USER_CREATION_AUTHENTICATOR')")
    @PostMapping
    public ResponseEntity<ResponseModel<AppDeveloperEntity>> create(@RequestBody final IdmServiceRepUserCreationDto idmServiceRepUserCreationDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseModel.Builder<AppDeveloperEntity>(HttpStatus.CREATED, true)
                        .body(developerService.create(
                                        idmServiceRepUserCreationDto.uid(),
                                        idmServiceRepUserCreationDto.email(),
                                        idmServiceRepUserCreationDto.ipAddress()
                                )
                        )
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
