package pl.bartlomiej.protectionservice.iploginprotection.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface IpLoginProtectionHttpService {

    @GetExchange("http://{hostname}/v1/users/{id}/trustedIpAddresses")
    boolean verifyIp(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                     @PathVariable String hostname,
                     @PathVariable String id,
                     @RequestParam String ipAddress);

    @PostExchange("http://{hostname}/v1/users/{id}/trustedIpAddresses")
    void trustIp(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                 @PathVariable String hostname,
                 @PathVariable String id,
                 @RequestParam String ipAddress);
}
