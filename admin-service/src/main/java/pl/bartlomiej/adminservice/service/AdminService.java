package pl.bartlomiej.adminservice.service;

import pl.bartlomiej.adminservice.domain.AppAdminEntity;
import pl.bartlomiej.mummicroservicecommons.globalidmservice.external.serviceidm.servlet.IDMServiceTemplate;

public interface AdminService extends IDMServiceTemplate<AppAdminEntity> {
}
