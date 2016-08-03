package queue.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utils.UpasUtils;

public class RemoveUserMessage extends BaseMessage {

    public static RemoveUserMessage newInstance() {
        RemoveUserMessage bo = new RemoveUserMessage();
        bo.setId(UpasUtils.IDGEN.generateId128Hex()).setTimestampMs(System.currentTimeMillis());
        return bo;
    }

    public static RemoveUserMessage newInstance(String appId, String id) {
        RemoveUserMessage bo = newInstance();
        bo.setAppId(appId);
        bo.setId(id);
        return bo;
    }

    protected final static String ATTR_ID = "id";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public RemoveUserMessage setId(String id) {
        setAttribute(ATTR_ID, id);
        return this;
    }

}
