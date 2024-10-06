package pl.bartlomiej.devservice.application.service;

import pl.bartlomiej.devservice.application.domain.Application;
import pl.bartlomiej.devservice.application.domain.dto.ApplicationRequestDto;

public interface ApplicationService {

    Application create(ApplicationRequestDto applicationRequestDto, String devId);
}
