package pl.bartlomiej.devservice.application.service;

import pl.bartlomiej.devservice.application.domain.Application;
import pl.bartlomiej.devservice.application.domain.ApplicationRequestStatus;
import pl.bartlomiej.devservice.application.domain.dto.ApplicationRequestDto;

import java.util.List;

public interface ApplicationService {

    Application create(ApplicationRequestDto applicationRequestDto, String devId);

    List<Application> getApplications(ApplicationRequestStatus requestStatus);

    List<Application> getApplications(String devId);

    ApplicationRequestStatus considerAppRequest(String id, ApplicationRequestStatus requestStatus, String details);
}