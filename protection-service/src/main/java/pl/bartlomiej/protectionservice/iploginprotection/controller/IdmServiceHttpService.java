package pl.bartlomiej.protectionservice.iploginprotection.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import pl.bartlomiej.mumcommons.coreutils.model.response.ResponseModel;

public interface IdmServiceHttpService {

    @GetExchange("http://{hostname}:{port}/{resourceApiVersion}/{idmResourceIdentifier}/{id}/trustedIpAddresses")
    ResponseModel<Boolean> verifyIp(@PathVariable String hostname,
                                    @PathVariable int port,
                                    @PathVariable String resourceApiVersion,
                                    @PathVariable String idmResourceIdentifier,
                                    @PathVariable String id,
                                    @RequestParam String ipAddress);

    @PostExchange("http://{hostname}:{port}/{resourceApiVersion}/{idmResourceIdentifier}/{id}/trustedIpAddresses")
    void trustIp(@PathVariable String hostname,
                 @PathVariable int port,
                 @PathVariable String resourceApiVersion,
                 @PathVariable String idmResourceIdentifier,
                 @PathVariable String id,
                 @RequestParam String ipAddress);
}
