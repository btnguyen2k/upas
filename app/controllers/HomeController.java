package controllers;

import java.util.Date;

import javax.inject.Inject;

import com.github.ddth.commons.utils.DateFormatUtils;
import com.google.inject.Provider;

import compositions.AdminAuthRequired;
import modules.cluster.ICluster;
import modules.registry.IRegistry;
import play.mvc.Controller;
import play.mvc.Result;

@AdminAuthRequired
public class HomeController extends Controller {

    @Inject
    private Provider<ICluster> cluster;

    @Inject
    private Provider<IRegistry> registry;

    public Result index() {
        Date now = new Date();
        StringBuilder msg = new StringBuilder("Your application is ready: "
                + DateFormatUtils.toString(now, "yyyy-MM-dd HH:mm:ss"));
        msg.append("<br>\nCluster: ").append(cluster != null ? cluster.get() : "[null]")
                .append("<br>\n");
        msg.append("<br>\nRegistry: ").append(registry != null ? registry.get() : "[null]")
                .append("<br>\n");

        response().setHeader(CONTENT_TYPE, "text/html; charset=utf-8");
        return ok(msg.toString());
    }
}
