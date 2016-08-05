package queue.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utils.UpasUtils;

public class RemoveUserFromGroupMessage extends BaseMessage {

    public static RemoveUserFromGroupMessage newInstance() {
        RemoveUserFromGroupMessage bo = new RemoveUserFromGroupMessage();
        bo.setId(UpasUtils.IDGEN.generateId128Hex()).setTimestampMs(System.currentTimeMillis());
        return bo;
    }

    public static RemoveUserFromGroupMessage newInstance(String appId, String userId,
            String usergroupId) {
        RemoveUserFromGroupMessage bo = newInstance();
        bo.setAppId(appId);
        bo.setUserId(userId).setUsergroupId(usergroupId);
        return bo;
    }

    protected final static String ATTR_USER_ID = "uid";
    protected final static String ATTR_GROUP_ID = "gid";

    @JsonIgnore
    public String getUserId() {
        return getAttribute(ATTR_USER_ID, String.class);
    }

    public RemoveUserFromGroupMessage setUserId(String userId) {
        setAttribute(ATTR_USER_ID, userId);
        return this;
    }

    @JsonIgnore
    public String getUsergroupId() {
        return getAttribute(ATTR_GROUP_ID, String.class);
    }

    public RemoveUserFromGroupMessage setUsergroupId(String usergroupId) {
        setAttribute(ATTR_GROUP_ID, usergroupId);
        return this;
    }

}
