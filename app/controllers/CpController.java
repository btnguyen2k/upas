package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import api.UpasApi;
import bo.app.AppBo;
import bo.app.IAppDao;
import bo.upas.IUpasDao;
import bo.upas.PermissionBo;
import compositions.AuthRequired;
import forms.FormCreateEditApplication;
import forms.FormLogin;
import models.AppModel;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.UpasConstants;
import utils.UserUtils;

/**
 * Base class for ControlPanel controller.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class CpController extends BaseController {

    public Result index() throws Exception {
        return redirect(controllers.routes.CpController.home());
    }

    public final static String VIEW_HOME = "home";

    @AuthRequired
    public Result home() throws Exception {
        Html html = render(VIEW_HOME);
        return ok(html);
    }

    public final static String VIEW_LOGIN = "login";

    /*
     * Handle GET:/login?returnUrl=xxx
     */
    public Result login(final String returnUrl) throws Exception {
        Form<FormLogin> form = formFactory.form(FormLogin.class);
        Html html = render(VIEW_LOGIN, form);
        return ok(html);
    }

    /*
     * Handle POST:/login?returnUrl=xxx
     */
    public Result loginSubmit(final String returnUrl) throws Exception {
        Form<FormLogin> form = formFactory.form(FormLogin.class).bindFromRequest();
        if (form.hasErrors()) {
            Html html = render(VIEW_LOGIN, form);
            return ok(html);
        }
        FormLogin model = form.get();
        UserUtils.loginCp(model.user, session());
        if (returnUrl != null && returnUrl.startsWith("/")) {
            return redirect(returnUrl);
        } else {
            return redirect(controllers.routes.CpController.home());
        }
    }
}
