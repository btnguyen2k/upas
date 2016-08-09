package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import api.UpasApi;
import bo.app.AppBo;
import bo.app.IAppDao;
import bo.upas.IUpasDao;
import bo.upas.PermissionBo;
import compositions.AuthRequired;
import forms.FormCreateEditApplication;
import models.AppModel;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.UpasConstants;

/**
 * AdminCp controller.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
@AuthRequired(usergroup = UpasConstants.GROUP_ADMIN)
public class AdminCpController extends CpController {

    public final static String VIEW_APPLICATION_LIST = "app_list";

    public Result applicationList() throws Exception {
        IAppDao appDao = getRegistry().getAppDao();
        String[] appIdList = appDao.getAllAppIds();
        List<AppBo> apps = new ArrayList<>();
        for (String id : appIdList) {
            AppBo app = appDao.getApp(id);
            if (app != null) {
                apps.add(app);
            }
        }
        Html html = render(VIEW_APPLICATION_LIST, (Object) AppModel.newInstances(apps));
        return ok(html);
    }

    public final static String VIEW_CREATE_APPLICATION = "create_app";

    public Result createApplication() throws Exception {
        Form<FormCreateEditApplication> form = formFactory.form(FormCreateEditApplication.class)
                .bind(FormCreateEditApplication.defaultInstance.toMap());
        form.discardErrors();
        Html html = render(VIEW_CREATE_APPLICATION, form);
        return ok(html);
    }

    public Result createApplicationSubmit() throws Exception {
        Form<FormCreateEditApplication> form = formFactory.form(FormCreateEditApplication.class)
                .bindFromRequest();
        if (form.hasErrors()) {
            Html html = render(VIEW_CREATE_APPLICATION, form);
            return ok(html);
        }

        FormCreateEditApplication model = form.get();
        AppBo app = AppBo.newInstance();
        app.setId(model.id).setApiKey(model.apiKey);
        app.setDisabled(model.isDisabled);

        IAppDao appDao = registry.get().getAppDao();
        if (appDao.create(app)) {
            IUpasDao upasDao = registry.get().getUpasDao();
            UpasApi upasApi = registry.get().getUpasApi();
            PermissionBo permAttachedToApp = PermissionBo.newInstance(app);
            AppBo systemApp = appDao.getApp(UpasConstants.SYSTEM_APP_ID);
            try {
                // initialize app's storage
                upasDao.initApp(app);

                // create a system permission attached to the app
                upasApi.addPermission(systemApp, permAttachedToApp.getId(),
                        permAttachedToApp.getTitle(), permAttachedToApp.getDescription());

                flash(VIEW_APPLICATION_LIST, calcMessages().at("msg.app.create.done", app.getId()));
            } catch (Exception e) {
                Logger.error(e.getMessage(), e);
                try {
                    appDao.delete(app);
                } catch (Exception e1) {
                    Logger.warn(e.getMessage(), e);
                }
                try {
                    upasDao.destroyApp(app);
                } catch (Exception e1) {
                    Logger.warn(e.getMessage(), e);
                }
                try {
                    upasApi.remove(systemApp, permAttachedToApp);
                } catch (Exception e1) {
                    Logger.warn(e.getMessage(), e);
                }

                flash(VIEW_APPLICATION_LIST,
                        calcMessages().at("msg.app.create.failed", app.getId()));
            }
        } else {
            flash(VIEW_APPLICATION_LIST, calcMessages().at("msg.app.create.failed", app.getId()));
        }
        return redirect(routes.AdminCpController.applicationList());
    }

    public final static String VIEW_EDIT_APPLICATION = "edit_app";

    public Result editApplication(String id) throws Exception {
        IAppDao appDao = registry.get().getAppDao();
        AppBo app = appDao.getApp(id);
        if (app == null) {
            flash(VIEW_APPLICATION_LIST, UpasConstants.FLASH_MSG_PREFIX_ERROR
                    + calcMessages().at("error.app.not_found", id));

            return redirect(routes.AdminCpController.applicationList());
        }

        Form<FormCreateEditApplication> form = formFactory.form(FormCreateEditApplication.class)
                .bind(FormCreateEditApplication.newInstance(app).toMap());
        form.discardErrors();
        Html html = render(VIEW_EDIT_APPLICATION, form);
        return ok(html);
    }

    public Result editApplicationSubmit(String id) throws Exception {
        IAppDao appDao = registry.get().getAppDao();
        AppBo app = appDao.getApp(id);
        if (app == null) {
            flash(VIEW_APPLICATION_LIST, UpasConstants.FLASH_MSG_PREFIX_ERROR
                    + calcMessages().at("error.app.not_found", id));

            return redirect(routes.AdminCpController.applicationList());
        }

        Form<FormCreateEditApplication> form = formFactory.form(FormCreateEditApplication.class)
                .bindFromRequest();
        if (form.hasErrors()) {
            Html html = render(VIEW_EDIT_APPLICATION, form);
            return ok(html);
        }

        FormCreateEditApplication model = form.get();
        app.setApiKey(model.apiKey);
        app.setDisabled(model.isDisabled);
        appDao.update(app);

        flash(VIEW_APPLICATION_LIST, calcMessages().at("msg.app.edit.done", app.getId()));

        return redirect(routes.AdminCpController.applicationList());
    }

    public final static String VIEW_DELETE_APPLICATION = "delete_app";

    public Result deleteApplication(String id) throws Exception {
        IAppDao appDao = registry.get().getAppDao();
        AppBo app = appDao.getApp(id);
        if (app == null) {
            flash(VIEW_APPLICATION_LIST, UpasConstants.FLASH_MSG_PREFIX_ERROR
                    + calcMessages().at("error.app.not_found", id));
            return redirect(routes.AdminCpController.applicationList());
        } else if (StringUtils.equals(app.getId(), UpasConstants.SYSTEM_APP_ID)) {
            flash(VIEW_APPLICATION_LIST, UpasConstants.FLASH_MSG_PREFIX_ERROR
                    + calcMessages().at("error.app.cannot_delete_sytem_app", id));
            return redirect(routes.AdminCpController.applicationList());
        }

        AppModel model = AppModel.newInstance(app);
        Html html = render(VIEW_DELETE_APPLICATION, model);
        return ok(html);
    }

    public Result deleteApplicationSubmit(String id) throws Exception {
        IAppDao appDao = registry.get().getAppDao();
        AppBo app = appDao.getApp(id);
        if (app == null) {
            flash(VIEW_APPLICATION_LIST, UpasConstants.FLASH_MSG_PREFIX_ERROR
                    + calcMessages().at("error.app.not_found", id));
            return redirect(routes.AdminCpController.applicationList());
        } else if (StringUtils.equals(app.getId(), UpasConstants.SYSTEM_APP_ID)) {
            flash(VIEW_APPLICATION_LIST, UpasConstants.FLASH_MSG_PREFIX_ERROR
                    + calcMessages().at("error.app.cannot_delete_sytem_app", id));
            return redirect(routes.AdminCpController.applicationList());
        }

        appDao.delete(app);

        // cleanup app's storage
        IUpasDao upasDao = registry.get().getUpasDao();
        upasDao.destroyApp(app);

        // remove the system permission attached to the app
        UpasApi upasApi = registry.get().getUpasApi();
        upasApi.remove(appDao.getApp(UpasConstants.SYSTEM_APP_ID), PermissionBo.newInstance(app));

        flash(VIEW_APPLICATION_LIST, calcMessages().at("msg.app.delete.done", app.getId()));

        return redirect(routes.AdminCpController.applicationList());
    }

}
