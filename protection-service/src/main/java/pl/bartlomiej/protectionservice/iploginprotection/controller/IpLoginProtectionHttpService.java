package pl.bartlomiej.protectionservice.iploginprotection.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import pl.bartlomiej.mummicroservicecommons.model.response.ResponseModel;

public interface IpLoginProtectionHttpService {

    @GetExchange("http://{hostname}:{port}/v1/{loginResourceIdentifier}/{id}/trustedIpAddresses")
    ResponseModel<Boolean> verifyIp(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                    @PathVariable String hostname,
                                    @PathVariable int port,
                                    @PathVariable String loginResourceIdentifier,
                                    @PathVariable String id,
                                    @RequestParam String ipAddress);

    @PostExchange("http://{hostname}:{port}/v1/{loginResourceIdentifier}/{id}/trustedIpAddresses")
    void trustIp(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                 @PathVariable String hostname,
                 @PathVariable int port,
                 @PathVariable String loginResourceIdentifier,
                 @PathVariable String id,
                 @RequestParam String ipAddress);
}
