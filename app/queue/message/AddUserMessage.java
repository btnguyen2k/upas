package queue.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ddth.commons.utils.SerializationUtils;

import utils.UpasUtils;

public class AddUserMessage extends BaseMessage {

    public static AddUserMessage newInstance() {
        AddUserMessage bo = new AddUserMessage();
        bo.setId(UpasUtils.IDGEN.generateId128Hex()).setTimestampMs(System.currentTimeMillis());
        return bo;
    }

    public static AddUserMessage newInstance(String appId, String id, Map<String, Object> data) {
        AddUserMessage bo = newInstance();
        bo.setId(id).setDataMap(data);
        return bo;
    }

    protected final static String ATTR_ID = "id";
    protected final static String ATTR_DATA = "data";

    @JsonIgnore
    public String getId() {
        return getAttribute(ATTR_ID, String.class);
    }

    public AddUserMessage setId(String id) {
        setAttribute(ATTR_ID, id);
        return this;
    }

    @JsonIgnore
    public String getData() {
        return getAttribute(ATTR_DATA, String.class);
    }

    public AddUserMessage setData(String data) {
        setAttribute(ATTR_DATA, data);
        synchronized (this) {
            dataMap = null;
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    private Map<String, Object> getDataAsMap() {
        Map<String, Object> result = null;
        try {
            result = SerializationUtils.fromJsonString(getData(), Map.class);
        } catch (Exception e) {
        }
        return result != null ? result : new HashMap<String, Object>();
    }

    @JsonIgnore
    private Map<String, Object> dataMap;

    @JsonIgnore
    public Map<String, Object> getDataMap() {
        if (dataMap == null) {
            synchronized (this) {
                dataMap = getDataAsMap();
            }
        }
        return Collections.unmodifiableMap(dataMap);
    }

    public AddUserMessage setDataMap(Map<String, Object> dataMap) {
        setData(dataMap != null ? SerializationUtils.toJsonString(dataMap) : "{}");
        return this;
    }

}
