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
    private static final String HTTP_STATUS_CODE = "httpStatusCode";

    public static boolean getSuccess(final JsonNode jsonResponse) {
        return jsonResponse.get(SUCCESS).asBoolean();
    }

    public static String getMessage(final JsonNode jsonResponse) {
        return jsonResponse.get(MESSAGE).asText();
    }

    public static int getHttpStatusCode(final JsonNode jsonResponse) {
        return jsonResponse.get(HTTP_STATUS_CODE).asInt();
    }
}
