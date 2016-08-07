package controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.queue.IQueue;

import akka.util.ByteString;
import bo.app.AppBo;
import bo.app.IAppDao;
import bo.upas.IUpasDao;
import bo.upas.PermissionBo;
import bo.upas.UserBo;
import bo.upas.UsergroupBo;
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

    public final static String PARAM_PERMISSION_ID = "perm_id";
    public final static String PARAM_USER_ID = "user_id";
    public final static String PARAM_USERGROUP_ID = "group_id";

    private Result validateRequestParams(Map<String, Object> params, String... fields) {
        for (String field : fields) {
            Object fieldValue = DPathUtils.getValue(params, field);
            if (fieldValue == null || (fieldValue instanceof CharSequence
                    && StringUtils.isBlank((CharSequence) fieldValue))) {
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
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_ID);
            if (result != null) {
                return result;
            }
            String id = DPathUtils.getValue(reqParams, PARAM_ID, String.class);
            String title = DPathUtils.getValue(reqParams, PARAM_TITLE, String.class);
            String desc = DPathUtils.getValue(reqParams, PARAM_DESC, String.class);

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
     * Remove an existing app permission.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code id}: (required) String, permission's unique id.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiRemovePermission() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_ID);
            if (result != null) {
                return result;
            }
            String id = DPathUtils.getValue(reqParams, PARAM_ID, String.class);

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueRemovePermission(queue, appId, id)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put remove-permission message to queue.");
                return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
            }
        } catch (Exception e) {
            return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Adds an app permission to a group.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code perm_id}: (required) String, permission's id.</li>
     * <li>{@code group_id}: (required) String, usergroup's id.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiAddPermissionToGroup() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_PERMISSION_ID,
                    PARAM_USERGROUP_ID);
            if (result != null) {
                return result;
            }
            String permId = DPathUtils.getValue(reqParams, PARAM_PERMISSION_ID, String.class);
            String groupId = DPathUtils.getValue(reqParams, PARAM_USERGROUP_ID, String.class);

            IAppDao appDao = getRegistry().getAppDao();
            AppBo app = appDao.getApp(appId);
            if (app == null) {
                return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
                        "App [" + appId + "] not found!");
            }

            IUpasDao upasDao = getRegistry().getUpasDao();
            PermissionBo perm = upasDao.getPermission(app, permId);
            if (perm == null) {
                return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
                        "Permission [" + permId + "] cannot be found in app [" + appId + "]!");
            }
            UsergroupBo ug = upasDao.getUsergroup(app, groupId);
            if (ug == null) {
                return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
                        "Usergroup [" + groupId + "] cannot be found in app [" + appId + "]!");
            }

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueAddPermissionToGroup(queue, appId, permId, groupId)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put add-permission-to-group message to queue.");
                return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
            }
        } catch (Exception e) {
            return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Remove an app permission from a group.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code perm_id}: (required) String, permission's id.</li>
     * <li>{@code group_id}: (required) String, usergroup's id.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiRemovePermissionFromGroup() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_PERMISSION_ID,
                    PARAM_USERGROUP_ID);
            if (result != null) {
                return result;
            }
            String permId = DPathUtils.getValue(reqParams, PARAM_PERMISSION_ID, String.class);
            String groupId = DPathUtils.getValue(reqParams, PARAM_USERGROUP_ID, String.class);

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueRemovePermissionFromGroup(queue, appId, permId, groupId)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put remove-permission-from-group message to queue.");
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
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_ID);
            if (result != null) {
                return result;
            }
            String id = DPathUtils.getValue(reqParams, PARAM_ID, String.class);
            Map<String, Object> data = DPathUtils.getValue(reqParams, PARAM_DATA, Map.class);

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
     * Remove an existing app user.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code id}: (required) String, user's unique id.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiRemoveUser() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_ID);
            if (result != null) {
                return result;
            }
            String id = DPathUtils.getValue(reqParams, PARAM_ID, String.class);

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueRemoveUser(queue, appId, id)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put remove-user message to queue.");
                return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
            }
        } catch (Exception e) {
            return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Adds an app user to a group.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code user_id}: (required) String, user's id.</li>
     * <li>{@code group_id}: (required) String, usergroup's id.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiAddUserToGroup() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_USER_ID, PARAM_USERGROUP_ID);
            if (result != null) {
                return result;
            }
            String userId = DPathUtils.getValue(reqParams, PARAM_USER_ID, String.class);
            String groupId = DPathUtils.getValue(reqParams, PARAM_USERGROUP_ID, String.class);

            IAppDao appDao = getRegistry().getAppDao();
            AppBo app = appDao.getApp(appId);
            if (app == null) {
                return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
                        "App [" + appId + "] not found!");
            }

            IUpasDao upasDao = getRegistry().getUpasDao();
            UserBo user = upasDao.getUser(app, userId);
            if (user == null) {
                return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
                        "User [" + userId + "] cannot be found in app [" + appId + "]!");
            }
            UsergroupBo ug = upasDao.getUsergroup(app, groupId);
            if (ug == null) {
                return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
                        "Usergroup [" + groupId + "] cannot be found in app [" + appId + "]!");
            }

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueAddUserToGroup(queue, appId, userId, groupId)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put add-user-to-group message to queue.");
                return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
            }
        } catch (Exception e) {
            return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Remove an app user from a group.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code user_id}: (required) String, user's id.</li>
     * <li>{@code group_id}: (required) String, usergroup's id.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiRemoveUserFromGroup() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_USER_ID, PARAM_USERGROUP_ID);
            if (result != null) {
                return result;
            }
            String userId = DPathUtils.getValue(reqParams, PARAM_USER_ID, String.class);
            String groupId = DPathUtils.getValue(reqParams, PARAM_USERGROUP_ID, String.class);

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueRemoveUserFromGroup(queue, appId, userId, groupId)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put remove-user-from-group message to queue.");
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
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_ID);
            if (result != null) {
                return result;
            }
            String id = DPathUtils.getValue(reqParams, PARAM_ID, String.class);
            Boolean isGod = DPathUtils.getValue(reqParams, PARAM_IS_GOD, Boolean.class);
            String title = DPathUtils.getValue(reqParams, PARAM_TITLE, String.class);
            String desc = DPathUtils.getValue(reqParams, PARAM_DESC, String.class);

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

    /**
     * Remove an existing app usergroup.
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code id}: (required) String, usergroup's unique id.</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiRemoveUsergroup() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_ID);
            if (result != null) {
                return result;
            }
            String id = DPathUtils.getValue(reqParams, PARAM_ID, String.class);

            IQueue queue = getRegistry().getQueueAppEvents();
            if (UpasUtils.queueRemoveUsergroup(queue, appId, id)) {
                return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
            } else {
                Logger.warn("Cannot put remove-usergroup message to queue.");
                return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
            }
        } catch (Exception e) {
            return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR, e.getMessage());
        }
    }

    /*----------------------------------------------------------------------*/

    @SuppressWarnings("unchecked")
    /**
     * Checks if a user has any permission.
     * 
     * <p>
     * Given a user and a list of permissions, this API returns {@code true} if
     * the user has any of the permission from the list, {@code false}
     * otherwise.
     * </p>
     * 
     * <p>
     * Params:
     * <ul>
     * <li>{@code user_id}: (required) String, user's id.</li>
     * <li>{@code perms}: (required) [String], list of permission ids</li>
     * </ul>
     * </p>
     * 
     * @return
     */
    @ApiAuthRequired
    public Result apiHasUserPermission() {
        try {
            Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
                    HEADER_APP_ID);
            String appId = reqHeaders.get(HEADER_APP_ID);

            Map<String, Object> reqParams = parseRequestContent();
            if (Logger.isDebugEnabled()) {
                String clientIp = UpasUtils.getClientIp(request());
                Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "], params: "
                        + reqParams);
            }

            Result result = validateRequestParams(reqParams, PARAM_USER_ID, "perms");
            if (result != null) {
                return result;
            }
            String userId = DPathUtils.getValue(reqParams, PARAM_USER_ID, String.class);
            Collection<String> permIds = DPathUtils.getValue(reqParams, "perms", Collection.class);

            if (permIds == null || permIds.size() == 0) {
                return doResponseJson(UpasConstants.RESPONSE_CLIENT_ERROR,
                        "Parameter [perm] is empty or invalid!");
            } else if (!(permIds instanceof Set)) {
                permIds = new HashSet<>(permIds);
            }

            IAppDao appDao = getRegistry().getAppDao();
            AppBo app = appDao.getApp(appId);
            if (app == null) {
                return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
                        "App [" + appId + "] not found!");
            }

            IUpasDao upasDao = getRegistry().getUpasDao();
            UserBo user = upasDao.getUser(app, userId);
            if (user == null) {
                return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
                        "User [" + userId + "] cannot be found in app [" + appId + "]!");
            }

            Set<UsergroupBo> usergroups = upasDao.getUserRoles(app, user);
            for (UsergroupBo ug : usergroups) {
                if (ug.isGod()) {
                    return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
                } else {
                    Set<PermissionBo> groupPerms = upasDao.getUsergroupPermissions(app, ug);
                    for (PermissionBo perm : groupPerms) {
                        if (permIds.contains(perm.getId())) {
                            return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
                        }
                    }
                }
            }

            return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
        } catch (Exception e) {
            return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR, e.getMessage());
        }
    }

    // /**
    // * Checks if a user belongs to a group.
    // *
    // * <p>
    // * Params:
    // * <ul>
    // * <li>{@code user_id}: (required) String, user's id.</li>
    // * <li>{@code group_id}: (required) String, user-group's id</li>
    // * </ul>
    // * </p>
    // *
    // * @return
    // */
    // @ApiAuthRequired
    // public Result apiIsUserInGroup() {
    // try {
    // Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
    // HEADER_APP_ID);
    // String appId = reqHeaders.get(HEADER_APP_ID);
    //
    // Map<String, Object> reqParams = parseRequestContent();
    // if (Logger.isDebugEnabled()) {
    // String clientIp = UpasUtils.getClientIp(request());
    // Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "],
    // params: "
    // + reqParams);
    // }
    //
    // Result result = validateRequestParams(reqParams, PARAM_USER_ID,
    // PARAM_USERGROUP_ID);
    // if (result != null) {
    // return result;
    // }
    // String userId = DPathUtils.getValue(reqParams, PARAM_USER_ID,
    // String.class);
    // String groupId = DPathUtils.getValue(reqParams, PARAM_USERGROUP_ID,
    // String.class);
    //
    // IAppDao appDao = getRegistry().getAppDao();
    // AppBo app = appDao.getApp(appId);
    // if (app == null) {
    // return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
    // "App [" + appId + "] not found!");
    // }
    //
    // IUpasDao upasDao = getRegistry().getUpasDao();
    // UserBo user = upasDao.getUser(app, userId);
    // if (user == null) {
    // return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
    // "User [" + userId + "] cannot be found in app [" + appId + "]!");
    // }
    // UsergroupBo ug = upasDao.getUsergroup(app, groupId);
    // if (ug == null) {
    // return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
    // "Usergroup [" + groupId + "] cannot be found in app [" + appId + "]!");
    // }
    //
    // UserRoleBo userRole = upasDao.getUserRole(app, userId, groupId);
    // return doResponseJson(UpasConstants.RESPONSE_OK, userRole != null ?
    // "true" : "false",
    // userRole != null);
    // } catch (Exception e) {
    // return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR,
    // e.getMessage());
    // }
    // }
    //
    // /**
    // * Checks if a user belongs to a god group.
    // *
    // * <p>
    // * Params:
    // * <ul>
    // * <li>{@code user_id}: (required) String, user's id.</li>
    // * </ul>
    // * </p>
    // *
    // * @return
    // */
    // @ApiAuthRequired
    // public Result apiIsUserGod() {
    // try {
    // Map<String, String> reqHeaders = UpasUtils.parseRequestHeaders(request(),
    // HEADER_APP_ID);
    // String appId = reqHeaders.get(HEADER_APP_ID);
    //
    // Map<String, Object> reqParams = parseRequestContent();
    // if (Logger.isDebugEnabled()) {
    // String clientIp = UpasUtils.getClientIp(request());
    // Logger.debug("Request [" + request().uri() + "] from [" + clientIp + "],
    // params: "
    // + reqParams);
    // }
    //
    // Result result = validateRequestParams(reqParams, PARAM_USER_ID);
    // if (result != null) {
    // return result;
    // }
    // String userId = DPathUtils.getValue(reqParams, PARAM_USER_ID,
    // String.class);
    //
    // IAppDao appDao = getRegistry().getAppDao();
    // AppBo app = appDao.getApp(appId);
    // if (app == null) {
    // return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
    // "App [" + appId + "] not found!");
    // }
    //
    // IUpasDao upasDao = getRegistry().getUpasDao();
    // UserBo user = upasDao.getUser(app, userId);
    // if (user == null) {
    // return doResponseJson(UpasConstants.RESPONSE_NOT_FOUND,
    // "User [" + userId + "] cannot be found in app [" + appId + "]!");
    // }
    //
    // Set<UsergroupBo> usergroups = upasDao.getUserRoles(app, user);
    // for (UsergroupBo usergroup : usergroups) {
    // if (usergroup.isGod()) {
    // return doResponseJson(UpasConstants.RESPONSE_OK, "true", true);
    // }
    // }
    // return doResponseJson(UpasConstants.RESPONSE_OK, "false", false);
    // } catch (Exception e) {
    // return doResponseJson(UpasConstants.RESPONSE_SERVER_ERROR,
    // e.getMessage());
    // }
    // }
}
