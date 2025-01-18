package pl.bartlomiej.keycloakspibundle.usercreationauthenticator;

import pl.bartlomiej.loginservices.IdmServiceRepresentation;

import java.util.List;

public class IdmServicesRepsConfig {
    private List<IdmServiceRepresentation> idmServiceRepresentations;

    public IdmServicesRepsConfig(List<IdmServiceRepresentation> idmServiceRepresentations) {
        this.idmServiceRepresentations = idmServiceRepresentations;
    }

    public IdmServicesRepsConfig() {
    }

    public List<IdmServiceRepresentation> getIdmServiceRepresentations() {
        return idmServiceRepresentations;
    }
}