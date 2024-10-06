package pl.bartlomiej.adminservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.dto.AdminRegisterDto;
import pl.bartlomiej.adminservice.service.AdminService;

@RestController
@RequestMapping("/v1/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize(
            "hasRole(T(pl.bartlomiej.adminservice.domain.AdminKeycloakRole).SUPERADMIN.getRole())"
    )
    @PostMapping
    public ResponseEntity<Admin> create(@RequestBody @Valid final AdminRegisterDto adminRegisterDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.create(adminRegisterDto, "127.0.0.1"));
    }
}