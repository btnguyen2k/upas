package controllers;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.queue.IQueue;

import akka.util.ByteString;
import compositions.ApiAuthRequired;
import play.Logger;
import play.mvc.Http.RawBuffer;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import utils.UpasConstants;
import utils.UpasUtils;

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
    public final static String PARAM_ID = "id";
    public final static String PARAM_TITLE = "title";
    public final static String PARAM_DESC = "desc";
    public final static String PARAM_DATA = "data";
    public final static String PARAM_IS_GOD = "is_god";

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

    /**
     * Adds/Updates an app permission.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code id}: (required) String, permission's unique id.</li>
     * <li>{@code title}: (optional) String, permission's title.</li>
     * <li>{@code desc}: (optional) String, permission's description.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiAddPermission() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            Result result = validateRequestParams(reqParams, PARAM_ID);
            if (result != null) {
                return result;
            }
            String id = DPathUtils.getValue(reqParams, PARAM_ID, String.class);
            String title = DPathUtils.getValue(reqParams, PARAM_TITLE, String.class);
            String desc = DPathUtils.getValue(reqParams, PARAM_DESC, String.class);

            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueAddPermission(queue, appId, id, title, desc)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put add-permission message to queue.");
                return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
            }
        } catch (Exception e) {
            return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Adds/Updates an app user.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code id}: (required) String, user's unique id.</li>
     * <li>{@code data}: (optional) Map, user's profile data.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    @ApiAuthRequired
    public Result apiAddUser() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            Result result = validateRequestParams(reqParams, PARAM_ID);
            if (result != null) {
                return result;
            }
            String id = DPathUtils.getValue(reqParams, PARAM_ID, String.class);
            Map<String, Object> data = DPathUtils.getValue(reqParams, PARAM_DATA, Map.class);

            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueAddUser(queue, appId, id, data)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put add-user message to queue.");
                return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
            }
        } catch (Exception e) {
            return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Adds/Updates an app usergroup.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code id}: (required) String, usergroup's unique id.</li>
     * <li>{@code is_god}: (optional) Boolean, is this usergroup "God"?</li>
     * <li>{@code title}: (optional) String, usergroup's title.</li>
     * <li>{@code desc}: (optional) String, usergroup's description.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiAddUsergroup() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            Result result = validateRequestParams(reqParams, PARAM_ID);
            if (result != null) {
                return result;
            }
            String id = DPathUtils.getValue(reqParams, PARAM_ID, String.class);
            Boolean isGod = DPathUtils.getValue(reqParams, PARAM_IS_GOD, Boolean.class);
            String title = DPathUtils.getValue(reqParams, PARAM_TITLE, String.class);
            String desc = DPathUtils.getValue(reqParams, PARAM_DESC, String.class);

            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueAddUsergroup(queue, appId, id, isGod, title, desc)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put add-permission message to queue.");
                return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
            }
        } catch (Exception e) {
            return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR, e.getMessage());
        }
    }
}
