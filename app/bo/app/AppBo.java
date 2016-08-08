package bo.app;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ddth.dao.BaseBo;

public class AppBo extends BaseBo {

    public final static AppBo[] EMPTY_ARRAY = new AppBo[0];

    public static AppBo newInstance() {
        Date now = new Date();
        AppBo bo = new AppBo();
        bo.setTimestampCreate(now).setTimestampUpdate(now);
        return bo;
    }

    public static AppBo newInstance(String id) {
        AppBo bo = newInstance();
        bo.setId(id);
        return bo;
    }

    public static AppBo newInstance(String id, String apiKey) {
        AppBo bo = newInstance(id);
        bo.setApiKey(apiKey);
        return bo;
    }

    public static AppBo newInstance(AppBo another) {
        AppBo bo = newInstance();
        bo.fromMap(another.toMap());
        return bo;
    }

    /*----------------------------------------------------------------------*/

    private final static String ATTR_ID = "id";
    private final static String ATTR_IS_DISABLED = "disabled";
    private final static String ATTR_API_KEY = "api_key";
    private final static String ATTR_TIMESTAMP_CREATE = "tcreate";
    private final static String ATTR_TIMESTAMP_UPDATE = "tupdate";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public AppBo setId(String id) {
        setAttribute(ATTR_ID, id != null ? id.trim().toLowerCase() : null);
        return this;
    }

    @JsonIgnore
    public boolean isDisabled() {
        Integer value = getAttribute(ATTR_IS_DISABLED, Integer.class);
        return value != null ? value.intValue() > 0 : false;
    }

    public AppBo setDisabled(int value) {
        setAttribute(ATTR_IS_DISABLED, value != 0 ? 1 : 0);
        return this;
    }

    public AppBo setDisabled(boolean value) {
        setAttribute(ATTR_IS_DISABLED, value ? 1 : 0);
        return this;
    }

    @JsonIgnore
    public String getApiKey() {
        return getAttribute(ATTR_API_KEY, String.class);
    }

    public AppBo setApiKey(String apiKey) {
        setAttribute(ATTR_API_KEY, apiKey != null ? apiKey.trim().toLowerCase() : "");
        return this;
    }

    @JsonIgnore
    public Date getTimestampCreate() {
        return getAttribute(ATTR_TIMESTAMP_CREATE, Date.class);
    }

    public AppBo setTimestampCreate(Date timestamp) {
        setAttribute(ATTR_TIMESTAMP_CREATE, timestamp);
        return this;
    }

    @JsonIgnore
    public Date getTimestampUpdate() {
        return getAttribute(ATTR_TIMESTAMP_UPDATE, Date.class);
    }

    public AppBo setTimestampUpdate(Date timestamp) {
        setAttribute(ATTR_TIMESTAMP_UPDATE, timestamp);
        return this;
    }

}
