package pl.bartlomiej.devservice.application.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.devservice.application.domain.Application;
import pl.bartlomiej.devservice.application.domain.ApplicationRequestStatus;
import pl.bartlomiej.devservice.application.domain.dto.ApplicationRequestDto;
import pl.bartlomiej.devservice.application.service.ApplicationService;
import pl.bartlomiej.devservice.application.service.ApplicationTokenService;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ApplicationTokenService applicationTokenService;

    public ApplicationController(ApplicationService applicationService, ApplicationTokenService applicationTokenService) {
        this.applicationService = applicationService;
        this.applicationTokenService = applicationTokenService;
    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.devservice.developer.domain.DeveloperKeycloakRole).DEVELOPER.getRole())")
    @PostMapping
    public ResponseEntity<ResponseModel<Application>> createApplication(@RequestBody @Valid final ApplicationRequestDto applicationRequestDto,
                                                                        final Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseModel.Builder<Application>(HttpStatus.CREATED, HttpStatus.CREATED.value())
                        .body(applicationService.create(applicationRequestDto, principal.getName()))
                        .build()
                );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping
    public ResponseEntity<ResponseModel<List<Application>>> getApplications(@RequestParam(required = false) final ApplicationRequestStatus requestStatus) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<List<Application>>(HttpStatus.OK, HttpStatus.OK.value())
                        .body(applicationService.getApplications(requestStatus))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PatchMapping("/{id}/requestStatus/{requestStatus}")
    public ResponseEntity<ResponseModel<Void>> considerAppRequest(@PathVariable final String id,
                                                                  @PathVariable final ApplicationRequestStatus requestStatus) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<Void>(HttpStatus.OK, HttpStatus.OK.value())
                        .message("Updated application request status to: "
                                + applicationService.considerAppRequest(id, requestStatus)
                        )
                        .build()
        );
    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.devservice.application.domain.ApplicationRole).APP_TOKEN_CHECKER.name())")
    @GetMapping("/app-token/{appToken}")
    public Boolean checkToken(@PathVariable String appToken) {
        return applicationTokenService.checkToken(appToken);
    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.devservice.application.domain.ApplicationRole).APP_TOKEN_CHECKER.name())")
    @PatchMapping("/{id}/app-token")
    public ResponseEntity<ResponseModel<String>> replaceCurrentAppToken(@PathVariable final String id) {
        return ResponseEntity.ok(
                new ResponseModel.Builder<String>(HttpStatus.OK, HttpStatus.OK.value())
                        .message("Successfully replaced the current application token with a new one.")
                        .body(applicationTokenService.replaceCurrentAppToken(id))
                        .build()
        );
    }
}