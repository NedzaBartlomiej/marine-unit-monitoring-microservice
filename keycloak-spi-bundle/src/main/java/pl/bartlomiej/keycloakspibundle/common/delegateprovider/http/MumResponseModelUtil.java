package pl.bartlomiej.keycloakspibundle.common.delegateprovider.http;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Util class for the marine-unit-microservice ResponseModel used in each response.
 * Contains methods that return the used ResponseModel fields.
 * It can be used for cases where you need to get a ResponseModel field from a JsonNode in this keycloak SPI project.
 */
public class MumResponseModelUtil {
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";

    public static JsonNode getSuccess(final JsonNode jsonResponse) {
        return jsonResponse.get(SUCCESS);
    }

    public static JsonNode getMessage(final JsonNode jsonResponse) {
        return jsonResponse.get(MESSAGE);
    }
}
