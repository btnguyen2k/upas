package queue.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utils.UpasUtils;

public class RemovePermissionFromGroupMessage extends BaseMessage {

    public static RemovePermissionFromGroupMessage newInstance() {
        RemovePermissionFromGroupMessage bo = new RemovePermissionFromGroupMessage();
        bo.setId(UpasUtils.IDGEN.generateId128Hex()).setTimestampMs(System.currentTimeMillis());
        return bo;
    }

    public static RemovePermissionFromGroupMessage newInstance(String appId, String permId,
            String usergroupId) {
        RemovePermissionFromGroupMessage bo = newInstance();
        bo.setAppId(appId);
        bo.setPermissionId(permId).setUsergroupId(usergroupId);
        return bo;
    }

    protected final static String ATTR_PERMISSION_ID = "pid";
    protected final static String ATTR_GROUP_ID = "gid";

    @JsonIgnore
    public String getPermissionId() {
        return getAttribute(ATTR_PERMISSION_ID, String.class);
    }

    public RemovePermissionFromGroupMessage setPermissionId(String permId) {
        setAttribute(ATTR_PERMISSION_ID, permId);
        return this;
    }

    @JsonIgnore
    public String getUsergroupId() {
        return getAttribute(ATTR_GROUP_ID, String.class);
    }

    public RemovePermissionFromGroupMessage setUsergroupId(String usergroupId) {
        setAttribute(ATTR_GROUP_ID, usergroupId);
        return this;
    }

}
