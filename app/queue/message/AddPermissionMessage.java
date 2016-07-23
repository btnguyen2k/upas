package queue.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utils.UpasUtils;

public class AddPermissionMessage extends BaseMessage {

    public static AddPermissionMessage newInstance() {
        AddPermissionMessage bo = new AddPermissionMessage();
        bo.setId(UpasUtils.IDGEN.generateId128Hex()).setTimestampMs(System.currentTimeMillis());
        return bo;
    }

    public static AddPermissionMessage newInstance(String appId, String id, String title,
            String desc) {
        AddPermissionMessage bo = newInstance();
        bo.setId(id).setTitle(title).setDescription(desc);
        return bo;
    }

    protected final static String ATTR_ID = "id";
    protected final static String ATTR_TITLE = "title";
    protected final static String ATTR_DESC = "desc";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public AddPermissionMessage setId(String id) {
        setAttribute(ATTR_ID, id);
        return this;
    }

    @JsonIgnore
    public String getTitle() {
        return getAttribute(ATTR_TITLE, String.class);
    }

    public AddPermissionMessage setTitle(String title) {
        setAttribute(ATTR_TITLE, title);
        return this;
    }

    @JsonIgnore
    public String getDescription() {
        return getAttribute(ATTR_DESC, String.class);
    }

    public AddPermissionMessage setDescription(String desc) {
        setAttribute(ATTR_DESC, desc);
        return this;
    }
}
