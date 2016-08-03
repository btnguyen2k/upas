package queue.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utils.UpasUtils;

public class RemovePermissionMessage extends BaseMessage {

    public static RemovePermissionMessage newInstance() {
        RemovePermissionMessage bo = new RemovePermissionMessage();
        bo.setId(UpasUtils.IDGEN.generateId128Hex()).setTimestampMs(System.currentTimeMillis());
        return bo;
    }

    public static RemovePermissionMessage newInstance(String appId, String id) {
        RemovePermissionMessage bo = newInstance();
        bo.setAppId(appId);
        bo.setId(id);
        return bo;
    }

    protected final static String ATTR_ID = "id";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public RemovePermissionMessage setId(String id) {
        setAttribute(ATTR_ID, id);
        return this;
    }

}
