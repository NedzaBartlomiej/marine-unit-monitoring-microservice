package pl.bartlomiej.apiservice.common.apiaccess;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import pl.bartlomiej.mumcommons.core.model.response.ResponseModel;
import reactor.core.publisher.Mono;

public interface DevServiceHttpService {

    @GetExchange("/v1/applications/app-token/{appToken}")
    Mono<ResponseEntity<ResponseModel<Boolean>>> checkToken(@PathVariable String appToken);
}