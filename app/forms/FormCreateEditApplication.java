package forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.app.AppBo;
import play.data.validation.ValidationError;
import utils.UpasGlobals;

public class FormCreateEditApplication extends BaseForm {

    public final static FormCreateEditApplication defaultInstance = new FormCreateEditApplication();

    public static FormCreateEditApplication newInstance(AppBo bo) {
        FormCreateEditApplication form = new FormCreateEditApplication();
        form.id = bo.getId();
        form.isDisabled = bo.isDisabled() ? 1 : 0;
        form.apiKey = bo.getApiKey();
        form.editId = form.id;
        return form;
    }

    public int isDisabled = 0;
    public String id = "", apiKey = "";
    public String editId = "";

    public Map<String, String> toMap() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("id", id);
        data.put("editId", editId);
        data.put("isDisabled", String.valueOf(isDisabled));
        data.put("apiKey", apiKey);
        return data;
    }

    public int getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(int isDisabled) {
        this.isDisabled = isDisabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEditId() {
        return editId;
    }

    public void setEditId(String editId) {
        this.editId = editId;
    }

    public List<ValidationError> validate() throws Exception {
        return UpasGlobals.formValidator.validate(this);
    }

}
