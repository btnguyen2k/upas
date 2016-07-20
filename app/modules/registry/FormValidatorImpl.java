package modules.registry;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import bo.app.IAppDao;
import bo.user.IUserDao;
import forms.FormCreateEditApplication;
import forms.FormLogin;
import play.data.validation.ValidationError;
import utils.UpasGlobals;
import utils.UserUtils;

@Singleton
public class FormValidatorImpl implements IFormValidator {

    protected Provider<IRegistry> registry;

    @Inject
    public FormValidatorImpl(Provider<IRegistry> registry) {
        UpasGlobals.formValidator = this;

        this.registry = registry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidationError> validate(FormLogin form) {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        String username = !StringUtils.isBlank(form.username) ? form.username.trim().toLowerCase()
                : null;
        String password = !StringUtils.isBlank(form.password) ? form.password.trim() : null;

        if (username == null || password == null) {
            errors.add(new ValidationError("username", "error.login.failed||"));
            return errors;
        }

        IUserDao userDao = registry.get().getUserDao();
        form.user = userDao.getUserByUsername(username);
        if (!UserUtils.authenticate(form.user, password)) {
            errors.add(new ValidationError("username", "error.login.failed||"));
            return errors;
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ValidationError> validate(FormCreateEditApplication form) {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        String id = !StringUtils.isBlank(form.id) ? form.id.trim().toLowerCase() : null;
        if (StringUtils.isBlank(id)) {
            errors.add(new ValidationError("id", "error.app.empty_id||"));
            return errors;
        }
        if (!StringUtils.equalsIgnoreCase(form.id, form.editId)) {
            IAppDao appDao = registry.get().getAppDao();
            if (appDao.getApp(id) != null) {
                errors.add(new ValidationError("id", "error.app.exists||" + id));
            }
        }

        String apiKey = !StringUtils.isBlank(form.apiKey) ? form.apiKey.trim().toLowerCase() : null;
        if (StringUtils.isBlank(apiKey)) {
            errors.add(new ValidationError("apiKey", "error.app.empty_api_key||"));
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        form.id = id;
        form.apiKey = apiKey;

        return errors.isEmpty() ? null : errors;
    }

}
