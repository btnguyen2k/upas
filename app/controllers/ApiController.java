package controllers;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.SerializationUtils;

import akka.util.ByteString;
import play.Logger;
import play.mvc.Http.RawBuffer;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import utils.UpasConstants;

/**
 * Controller that handles API requests.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ApiController extends BaseController {

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseRequestContent() throws IOException {
        try {
            RequestBody requestBody = request().body();
            String requestContent = null;
            JsonNode jsonNode = requestBody.asJson();
            if (jsonNode != null) {
                requestContent = jsonNode.toString();
            } else {
                RawBuffer rawBuffer = requestBody.asRaw();
                if (rawBuffer != null) {
                    ByteString buffer = rawBuffer.asBytes();
                    if (buffer != null) {
                        requestContent = buffer.toString();
                    } else {
                        byte[] buff = FileUtils.readFileToByteArray(rawBuffer.asFile());
                        requestContent = buff != null ? new String(buff, UpasConstants.UTF8) : "";
                    }
                } else {
                    requestContent = requestBody.asText();
                }
            }
            return SerializationUtils.fromJsonString(requestContent, Map.class);
        } catch (Exception e) {
            Logger.warn("Error while parsing request params: " + e.getMessage(), e);
            return null;
        }
    }

    public final static String HEADER_API_KEY = "api_key";
    public final static String HEADER_APP_ID = "app_id";
    public final static String PARAM_TOKEN = "token";
    public final static String PARAM_OS = "os";
    public final static String PARAM_TAGS = "tags";
    public final static String PARAM_TARGETS = "targets";
    public final static String PARAM_TITLE = "title";
    public final static String PARAM_CONTENT = "content";

    // private final static String API_ERROR_INVALID_TAGS = "Parameter [" +
    // PARAM_TAGS
    // + "] is invalid!";

    private Result validateRequestParams(Map<String, Object> params, String... fields) {
        for (String field : fields) {
            String fieldValue = DPathUtils.getValue(params, field, String.class);
            if (StringUtils.isBlank(fieldValue)) {
                return doResponseJson(UpasConstants.RESPONSE_CLIENT_ERROR,
                        "Field [" + field + "] is missting or invalid!");
            }
        }
        return null;
    }

}
