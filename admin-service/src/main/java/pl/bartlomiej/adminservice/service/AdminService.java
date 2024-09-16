package pl.bartlomiej.adminservice.service;

import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;

public interface AdminService {
    Admin create(AdminRegisterDto adminRegisterDto);
}
