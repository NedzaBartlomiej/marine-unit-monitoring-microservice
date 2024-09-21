package pl.bartlomiej.adminservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;
import pl.bartlomiej.adminservice.service.AdminService;

import java.security.Principal;

@RestController
@RequestMapping("/v1/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize(
            "hasRole(T(pl.bartlomiej.adminservice.domain.AdminKeycloakRole).SUPERADMIN.name())"
    )
    @PostMapping
    public ResponseEntity<Admin> create(@RequestBody final AdminRegisterDto adminRegisterDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.create(adminRegisterDto));
    }

    @PreAuthorize(
            "hasAnyRole(" +
                    "T(pl.bartlomiej.adminservice.domain.AdminKeycloakRole).ADMIN.name()," +
                    "T(pl.bartlomiej.adminservice.domain.AdminKeycloakRole).SUPERADMIN.name()" +
                    ")"
    )
    @GetMapping("/hello")
    public ResponseEntity<String> helloAdmin(Principal principal) {
        return ResponseEntity.ok("Hello " + principal.getName());
    }
}