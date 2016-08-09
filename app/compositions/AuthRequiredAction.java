package compositions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import bo.user.UserBo;
import controllers.CpController;
import modules.registry.IRegistry;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import utils.UpasConstants;
import utils.UserUtils;

public class AuthRequiredAction extends Action<AuthRequired> {

    private CompletionStage<Result> goLogin(final Context ctx, final String flashMsg) {
        return CompletableFuture.supplyAsync(new Supplier<Result>() {
            @Override
            public Result get() {
                String urlReturn = ctx.request().uri();
                if (!StringUtils.isBlank(flashMsg)) {
                    ctx.flash().put(CpController.VIEW_LOGIN, flashMsg);
                }
                return redirect(controllers.routes.CpController.login(urlReturn));
            }
        });
    }

    // private CompletionStage<Result> goLogin(final Context ctx) {
    // return goLogin(ctx, null);
    // }

    @Inject
    private Provider<IRegistry> registry;

    @Override
    public CompletionStage<Result> call(Context context) {
        UserBo user = UserUtils.currentCpUser(registry.get(), context.session());
        if (user == null) {
            return goLogin(context, UpasConstants.FLASH_MSG_PREFIX_ERROR
                    + "Please login to access the requested page!");
        }
        if (configuration.usergroup() != 0 && configuration.usergroup() != user.getGroupId()) {
            // wrong usergroup
            return goLogin(context, UpasConstants.FLASH_MSG_PREFIX_ERROR
                    + "You donot have permission to access the requested page!");
        }

        try {
            return delegate.call(context);
        } catch (final Exception e) {
            return CompletableFuture.supplyAsync(new Supplier<Result>() {
                @Override
                public Result get() {
                    StringBuilder sb = new StringBuilder(
                            "Error occured, refresh the page to retry. If the error persists, please contact site admin for support.");
                    String stacktrace = ExceptionUtils.getStackTrace(e);
                    sb.append("\n\nError details: ").append(e.getMessage()).append("\n")
                            .append(stacktrace);
                    Throwable cause = e.getCause();
                    while (cause != null) {
                        sb.append("\n").append(ExceptionUtils.getStackTrace(cause));
                        cause = cause.getCause();
                    }
                    return ok(sb.toString());
                }
            });
        }
    }

}
