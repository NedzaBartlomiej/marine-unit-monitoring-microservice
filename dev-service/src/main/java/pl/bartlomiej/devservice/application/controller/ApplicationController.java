package pl.bartlomiej.devservice.application.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.devservice.application.domain.Application;
import pl.bartlomiej.devservice.application.domain.dto.ApplicationRequestDto;
import pl.bartlomiej.devservice.application.service.ApplicationService;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

import java.security.Principal;

@RestController
@RequestMapping("/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PreAuthorize("hasRole(T(pl.bartlomiej.devservice.developer.domain.DeveloperKeycloakRole).DEVELOPER.getRole())")
    @PostMapping
    public ResponseEntity<ResponseModel<Application>> createApplication(@RequestBody @Valid final ApplicationRequestDto applicationRequestDto,
                                                                        Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseModel.Builder<Application>(HttpStatus.CREATED, HttpStatus.CREATED.value())
                        .body(applicationService.create(applicationRequestDto, principal.getName()))
                        .build()
                );
    }
}