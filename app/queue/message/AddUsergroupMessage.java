package queue.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utils.UpasUtils;

public class AddUsergroupMessage extends BaseMessage {

    public static AddUsergroupMessage newInstance() {
        AddUsergroupMessage bo = new AddUsergroupMessage();
        bo.setId(UpasUtils.IDGEN.generateId128Hex()).setIsGod(false)
                .setTimestampMs(System.currentTimeMillis());
        return bo;
    }

    public static AddUsergroupMessage newInstance(String appId, String id, Boolean isGod,
            String title, String desc) {
        AddUsergroupMessage bo = newInstance();
        bo.setAppId(appId);
        bo.setId(id).setIsGod(isGod).setTitle(title).setDescription(desc);
        return bo;
    }

    protected final static String ATTR_ID = "id";
    protected final static String ATTR_IS_GOD = "is_god";
    protected final static String ATTR_TITLE = "title";
    protected final static String ATTR_DESC = "desc";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public AddUsergroupMessage setId(String id) {
        setAttribute(ATTR_ID, id);
        return this;
    }

    @JsonIgnore
    public boolean isGod() {
        Boolean value = getAttribute(ATTR_IS_GOD, Boolean.class);
        return value != null ? value.booleanValue() : false;
    }

    public AddUsergroupMessage setIsGod(boolean value) {
        setAttribute(ATTR_IS_GOD, value);
        return this;
    }

    public AddUsergroupMessage setIsGod(Boolean value) {
        setAttribute(ATTR_IS_GOD, value != null ? value.booleanValue() : false);
        return this;
    }

    @JsonIgnore
    public String getTitle() {
        return getAttribute(ATTR_TITLE, String.class);
    }

    public AddUsergroupMessage setTitle(String title) {
        setAttribute(ATTR_TITLE, title);
        return this;
    }

    @JsonIgnore
    public String getDescription() {
        return getAttribute(ATTR_DESC, String.class);
    }

    public AddUsergroupMessage setDescription(String desc) {
        setAttribute(ATTR_DESC, desc);
        return this;
    }
}
