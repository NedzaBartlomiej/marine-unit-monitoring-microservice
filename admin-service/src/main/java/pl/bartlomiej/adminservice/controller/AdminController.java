package pl.bartlomiej.adminservice.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;
import pl.bartlomiej.adminservice.service.idm.IDMAdminService;

@RestController
@RequestMapping("/v1/admins")
public class AdminController {

    private final IDMAdminService IDMAdminService;

    public AdminController(@Qualifier("IDMAdminServiceImpl") IDMAdminService IDMAdminService) {
        this.IDMAdminService = IDMAdminService;
    }

    @PostMapping
    public ResponseEntity<Admin> create(@RequestBody final AdminRegisterDto adminRegisterDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IDMAdminService.create(adminRegisterDto));
    }
}
