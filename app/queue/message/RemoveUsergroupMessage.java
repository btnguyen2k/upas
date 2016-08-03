package queue.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utils.UpasUtils;

public class RemoveUsergroupMessage extends BaseMessage {

    public static RemoveUsergroupMessage newInstance() {
        RemoveUsergroupMessage bo = new RemoveUsergroupMessage();
        bo.setId(UpasUtils.IDGEN.generateId128Hex()).setTimestampMs(System.currentTimeMillis());
        return bo;
    }

    public static RemoveUsergroupMessage newInstance(String appId, String id) {
        RemoveUsergroupMessage bo = newInstance();
        bo.setAppId(appId);
        bo.setId(id);
        return bo;
    }

    protected final static String ATTR_ID = "id";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public RemoveUsergroupMessage setId(String id) {
        setAttribute(ATTR_ID, id);
        return this;
    }

}
