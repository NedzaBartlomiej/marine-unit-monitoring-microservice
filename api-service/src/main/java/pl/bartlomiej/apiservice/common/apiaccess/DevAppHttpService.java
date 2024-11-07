package pl.bartlomiej.apiservice.common.apiaccess;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;
import reactor.core.publisher.Mono;

public interface DevAppHttpService {

    @GetExchange("/v1/applications/app-token/{appToken}")
    Mono<ResponseEntity<ResponseModel<Boolean>>> checkToken(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String serviceAccToken, @PathVariable String appToken);
}