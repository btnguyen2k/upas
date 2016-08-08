package modules.initdb;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Provider;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import api.UpasApi;
import bo.app.AppBo;
import bo.app.IAppDao;
import bo.user.IUserDao;
import bo.user.UserBo;
import modules.registry.IRegistry;
import play.Logger;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;
import utils.UpasConstants;
import utils.UserUtils;

public class InitDbModule extends Module {

    public final static class InitDbBootstrap {

        private IRegistry registry;

        @Inject
        public InitDbBootstrap(Provider<IRegistry> registry) {
            this.registry = registry.get();

            try {
                initAdminUserAccount();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }

            try {
                initSystemApp();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }
            try {
                initSystemAppData();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }
        }

        private void initSystemAppData() {
            UpasApi upasApi = registry.getUpasApi();

            final String APP_ID = UpasConstants.SYSTEM_APP_ID;
            IAppDao appDao = registry.getAppDao();
            AppBo app = appDao.getApp(APP_ID);

            upasApi.addUser(app, UpasConstants.SYSTEM_ADMIN_USER_ID);
            upasApi.addUsergroup(app, "admin", true, "UPAS Administrator",
                    "Administrators have all permissions.");
            upasApi.addUserToGroup(app, UpasConstants.SYSTEM_ADMIN_USER_ID, "admin");
        }

        private void initSystemApp() {
            final String APP_ID = UpasConstants.SYSTEM_APP_ID;
            IAppDao appDao = registry.getAppDao();
            AppBo app = appDao.getApp(APP_ID);
            if (app == null) {
                final String API_KEY = RandomStringUtils.random(32, true, true);

                Logger.info("Creating system application: " + APP_ID);

                app = AppBo.newInstance(APP_ID, API_KEY);
                appDao.create(app);

                registry.getUpasDao().initApp(app);
            }
        }

        private void initAdminUserAccount() {
            IUserDao userDao = registry.getUserDao();
            UserBo admin = userDao.getUser("1");
            if (admin == null) {
                Config conf = ConfigFactory.load().getConfig("init");
                String id = UpasConstants.SYSTEM_ADMIN_USER_ID;
                String username = conf != null ? conf.getString("user.name") : null;
                if (StringUtils.isBlank(username)) {
                    username = UpasConstants.SYSTEM_ADMIN_USER_NAME;
                }
                String password = conf != null ? conf.getString("user.password") : null;
                if (StringUtils.isBlank(password)) {
                    password = UpasConstants.SYSTEM_ADMIN_USER_PASSWORD;
                }
                String email = conf != null ? conf.getString("user.email") : null;
                if (StringUtils.isBlank(email)) {
                    email = UpasConstants.SYSTEM_ADMIN_USER_EMAIL;
                }

                Logger.info("Creating admin user account: " + id + "/" + username + "/" + email);

                admin = UserBo.newInstance(username, UserUtils.encryptPassword(id, password), email)
                        .setId(id).setGroupId(UpasConstants.GROUP_ADMIN);
                userDao.create(admin);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Seq<Binding<?>> bindings(Environment env, Configuration conf) {
        Seq<Binding<?>> bindings = seq(bind(InitDbBootstrap.class).toSelf().eagerly());
        return bindings;
    }

}
