package utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.ddth.commons.utils.IdGenerator;
import com.github.ddth.commons.utils.SerializationUtils;
import com.github.ddth.queue.IQueue;
import com.github.ddth.queue.impl.universal.UniversalQueueMessage;

import play.Logger;
import play.mvc.Http.Request;
import queue.message.AddPermissionMessage;
import queue.message.AddPermissionToGroupMessage;
import queue.message.AddUserMessage;
import queue.message.AddUserToGroupMessage;
import queue.message.AddUsergroupMessage;
import queue.message.BaseMessage;
import queue.message.RemovePermissionFromGroupMessage;
import queue.message.RemovePermissionMessage;
import queue.message.RemoveUserFromGroupMessage;
import queue.message.RemoveUserMessage;
import queue.message.RemoveUsergroupMessage;

public class UpasUtils {

    public final static IdGenerator IDGEN = IdGenerator.getInstance(IdGenerator.getMacAddr());

    public static String getClientIp(Request request) {
        String[] headerFields = new String[] { "X-Forwarded-For", "X-Real-IP", "Real-IP" };
        String clientIPHeader = null;
        for (String field : headerFields) {
            clientIPHeader = request.getHeader(field);
            if (!StringUtils.isBlank(clientIPHeader)) {
                if (Logger.isDebugEnabled()) {
                    Logger.debug(
                            "Got client ip [" + clientIPHeader + "] from field [" + field + "]");
                }
                break;
            }
        }
        if (StringUtils.isBlank(clientIPHeader)) {
            clientIPHeader = request.remoteAddress();
            if (Logger.isDebugEnabled()) {
                Logger.debug("Got client ip [" + clientIPHeader + "] from field [Remote-Address]");
            }
        }
        return clientIPHeader;
    }

    public static Collection<String> parseTags(Object tagsData) {
        Set<String> tags = new HashSet<>();
        if (tagsData instanceof List) {
            tagsData = ((List<?>) tagsData).toArray();
        }
        if (tagsData instanceof String[]) {
            for (String tag : (String[]) tagsData) {
                tags.add(tag);
            }
        } else if (tagsData instanceof Object[]) {
            for (Object tag : (Object[]) tagsData) {
                tags.add(tag.toString());
            }
        } else if (tagsData instanceof String) {
            String[] tokens = ((String) tagsData).split("[,;\\s]+");
            for (String tag : tokens) {
                tags.add(tag);
            }
        }
        return tags;
    }

    public static Map<String, String> parseRequestHeaders(Request request) {
        return parseRequestHeaders(request, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static Map<String, String> parseRequestHeaders(Request request, String... filters) {
        boolean hasFilters = false;
        if (filters != null && filters.length > 1) {
            Arrays.sort(filters);
            hasFilters = true;
        }
        Map<String, String> result = new HashMap<String, String>();
        Map<String, String[]> headers = request.headers();
        if (headers != null) {
            for (Entry<String, String[]> entry : headers.entrySet()) {
                String key = entry.getKey();
                if (!hasFilters || Arrays.binarySearch(filters, key) >= 0) {
                    String[] values = entry.getValue();
                    result.put(key, values != null & values.length > 0 ? values[0] : "");
                }
            }
        }
        return result;
    }

    /**
     * Counts number of available applications.
     * 
     * @return
     */
    public static int countApplications() {
        return UpasGlobals.registry.getAppDao().getAllAppIds().length;
    }

    public static Date extractTimestamp(String id128Hex) {
        return new Date(IdGenerator.extractTimestamp128(id128Hex));
    }

    public static byte[] base64Decode(String encodedStr) {
        return encodedStr != null ? Base64.decodeBase64(encodedStr) : null;
    }

    public static String base64Encode(byte[] data) {
        return data != null ? Base64.encodeBase64String(data) : null;
    }

    public static byte[] toBytes(BaseMessage msg) {
        return SerializationUtils.toByteArray(msg);
    }

    public static <T extends BaseMessage> T fromBytes(byte[] data, Class<T> clazz) {
        return SerializationUtils.fromByteArray(data, clazz);
    }

    public static boolean queueAddPermission(IQueue queue, String appId, String id, String title,
            String desc) {
        AddPermissionMessage msg = AddPermissionMessage.newInstance(appId, id, title, desc);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

    public static boolean queueRemovePermission(IQueue queue, String appId, String id) {
        RemovePermissionMessage msg = RemovePermissionMessage.newInstance(appId, id);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

    public static boolean queueAddPermissionToGroup(IQueue queue, String appId, String permId,
            String groupId) {
        AddPermissionToGroupMessage msg = AddPermissionToGroupMessage.newInstance(appId, permId,
                groupId);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

    public static boolean queueRemovePermissionFromGroup(IQueue queue, String appId, String permId,
            String groupId) {
        RemovePermissionFromGroupMessage msg = RemovePermissionFromGroupMessage.newInstance(appId,
                permId, groupId);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

    public static boolean queueAddUser(IQueue queue, String appId, String id,
            Map<String, Object> data) {
        AddUserMessage msg = AddUserMessage.newInstance(appId, id, data);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

    public static boolean queueRemoveUser(IQueue queue, String appId, String id) {
        RemoveUserMessage msg = RemoveUserMessage.newInstance(appId, id);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

    public static boolean queueAddUserToGroup(IQueue queue, String appId, String userId,
            String groupId) {
        AddUserToGroupMessage msg = AddUserToGroupMessage.newInstance(appId, userId, groupId);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

    public static boolean queueRemoveUserFromGroup(IQueue queue, String appId, String userId,
            String groupId) {
        RemoveUserFromGroupMessage msg = RemoveUserFromGroupMessage.newInstance(appId, userId,
                groupId);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

    public static boolean queueAddUsergroup(IQueue queue, String appId, String id, Boolean isGod,
            String title, String desc) {
        AddUsergroupMessage msg = AddUsergroupMessage.newInstance(appId, id, isGod, title, desc);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

    public static boolean queueRemoveUsergroup(IQueue queue, String appId, String id) {
        RemoveUsergroupMessage msg = RemoveUsergroupMessage.newInstance(appId, id);
        UniversalQueueMessage queueMsg = UniversalQueueMessage.newInstance();
        queueMsg.content(toBytes(msg));
        return queue.queue(queueMsg);
    }

}
