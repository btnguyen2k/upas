package modules.cluster.workers;

import com.github.ddth.queue.IQueueMessage;

import api.UpasApi;
import bo.app.AppBo;
import bo.app.IAppDao;
import bo.upas.IUpasDao;
import bo.upas.PermissionBo;
import bo.upas.UserBo;
import bo.upas.UsergroupBo;
import modules.cluster.BaseQueueThread;
import modules.registry.IRegistry;
import play.Logger;
import queue.message.AddPermissionMessage;
import queue.message.AddUserMessage;
import queue.message.AddUsergroupMessage;
import queue.message.BaseMessage;
import queue.message.RemovePermissionMessage;
import queue.message.RemoveUserMessage;
import queue.message.RemoveUsergroupMessage;
import utils.UpasUtils;

/**
 * Thread to process messages in app-event queue.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class AppEventThread extends BaseQueueThread {

    public AppEventThread(IRegistry registry) {
        super(AppEventThread.class.getSimpleName(), registry, registry.getQueueAppEvents());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean processQueueMessage(IQueueMessage queueMsg) {
        Object data = queueMsg.qData();
        if (data instanceof byte[]) {
            BaseMessage baseMsg = UpasUtils.fromBytes((byte[]) data, BaseMessage.class);

            if (Logger.isDebugEnabled()) {
                Logger.debug("\tMessage from queue [" + baseMsg.getClass().getSimpleName() + "]: "
                        + baseMsg);
            }

            if (baseMsg instanceof AddPermissionMessage) {
                return addPermission((AddPermissionMessage) baseMsg);
            } else if (baseMsg instanceof AddUsergroupMessage) {
                return addUsergroup((AddUsergroupMessage) baseMsg);
            } else if (baseMsg instanceof AddUserMessage) {
                return addUser((AddUserMessage) baseMsg);
            } else if (baseMsg instanceof RemovePermissionMessage) {
                return removePermission((RemovePermissionMessage) baseMsg);
            } else if (baseMsg instanceof RemoveUsergroupMessage) {
                return removeUsergroup((RemoveUsergroupMessage) baseMsg);
            } else if (baseMsg instanceof RemoveUserMessage) {
                return removeUser((RemoveUserMessage) baseMsg);
            }
        } else {
            if (Logger.isDebugEnabled()) {
                Logger.debug("\tMessage from queue: " + data);
            }
        }
        return false;
    }

    private AppBo validateApp(BaseMessage msg) {
        IAppDao appDao = getRegistry().getAppDao();
        AppBo app = appDao.getApp(msg.getAppId());
        if (app == null) {
            Logger.error("App [" + msg.getAppId() + "] not found!");
            return null;
        }
        if (app.isDisabled()) {
            Logger.error("App [" + msg.getAppId() + "] is disabled!");
            return null;
        }
        return app;
    }

    private boolean addPermission(AddPermissionMessage msg) {
        AppBo app = validateApp(msg);
        if (app == null) {
            return false;
        }
        UpasApi upasApi = getRegistry().getUpasApi();
        PermissionBo perm = upasApi.addPermission(app, msg.getId(), msg.getTitle(),
                msg.getDescription());
        if (perm == null) {
            Logger.error("Failed to add permission for app [" + msg.getAppId() + "]: " + msg);
        }
        return true;
    }

    private boolean removePermission(RemovePermissionMessage msg) {
        AppBo app = validateApp(msg);
        if (app == null) {
            return false;
        }
        IUpasDao upasDao = getRegistry().getUpasDao();
        PermissionBo perm = upasDao.getPermission(app, msg.getId());
        if (perm != null) {
            UpasApi upasApi = getRegistry().getUpasApi();
            upasApi.remove(app, perm);
            return true;
        } else {
            Logger.error("Permission [" + msg.getId() + "] doesnot exist in app [" + msg.getAppId()
                    + "]: " + msg);
            return false;
        }
    }

    private boolean addUsergroup(AddUsergroupMessage msg) {
        AppBo app = validateApp(msg);
        if (app == null) {
            return false;
        }
        UpasApi upasApi = getRegistry().getUpasApi();
        UsergroupBo ug = upasApi.addUsergroup(app, msg.getId(), msg.isGod(), msg.getTitle(),
                msg.getDescription());
        if (ug == null) {
            Logger.error("Failed to add usergroup for app [" + msg.getAppId() + "]: " + msg);
        }
        return true;
    }

    private boolean removeUsergroup(RemoveUsergroupMessage msg) {
        AppBo app = validateApp(msg);
        if (app == null) {
            return false;
        }
        IUpasDao upasDao = getRegistry().getUpasDao();
        UsergroupBo ug = upasDao.getUsergroup(app, msg.getId());
        if (ug != null) {
            UpasApi upasApi = getRegistry().getUpasApi();
            upasApi.remove(app, ug);
            return true;
        } else {
            Logger.error("Usergroup [" + msg.getId() + "] doesnot exist in app [" + msg.getAppId()
                    + "]: " + msg);
            return false;
        }
    }

    private boolean addUser(AddUserMessage msg) {
        AppBo app = validateApp(msg);
        if (app == null) {
            return false;
        }
        UpasApi upasApi = getRegistry().getUpasApi();
        UserBo user = upasApi.addUser(app, msg.getId(), msg.getDataMap());
        if (user == null) {
            Logger.error("Failed to add user for app [" + msg.getAppId() + "]: " + msg);
        }
        return true;
    }

    private boolean removeUser(RemoveUserMessage msg) {
        AppBo app = validateApp(msg);
        if (app == null) {
            return false;
        }
        IUpasDao upasDao = getRegistry().getUpasDao();
        UserBo user = upasDao.getUser(app, msg.getId());
        if (user != null) {
            UpasApi upasApi = getRegistry().getUpasApi();
            upasApi.remove(app, user);
            return true;
        } else {
            Logger.error("User [" + msg.getId() + "] doesnot exist in app [" + msg.getAppId()
                    + "]: " + msg);
            return false;
        }
    }
}
