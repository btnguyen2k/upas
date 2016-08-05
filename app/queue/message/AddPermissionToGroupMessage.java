package queue.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utils.UpasUtils;

public class AddPermissionToGroupMessage extends BaseMessage {

    public static AddPermissionToGroupMessage newInstance() {
        AddPermissionToGroupMessage bo = new AddPermissionToGroupMessage();
        bo.setId(UpasUtils.IDGEN.generateId128Hex()).setTimestampMs(System.currentTimeMillis());
        return bo;
    }

    public static AddPermissionToGroupMessage newInstance(String appId, String permId,
            String usergroupId) {
        AddPermissionToGroupMessage bo = newInstance();
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

    public AddPermissionToGroupMessage setPermissionId(String permId) {
        setAttribute(ATTR_PERMISSION_ID, permId);
        return this;
    }

    @JsonIgnore
    public String getUsergroupId() {
        return getAttribute(ATTR_GROUP_ID, String.class);
    }

    public AddPermissionToGroupMessage setUsergroupId(String usergroupId) {
        setAttribute(ATTR_GROUP_ID, usergroupId);
        return this;
    }

}
