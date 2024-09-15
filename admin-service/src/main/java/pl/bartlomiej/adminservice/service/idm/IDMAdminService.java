package pl.bartlomiej.adminservice.service.idm;

import pl.bartlomiej.adminservice.domain.Admin;
import pl.bartlomiej.adminservice.domain.AdminRegisterDto;

public interface IDMAdminService {
    Admin create(final AdminRegisterDto admin);
}
