package modules.cluster.workers;

import com.github.ddth.queue.IQueueMessage;

import api.UpasApi;
import bo.app.AppBo;
import bo.app.IAppDao;
import bo.upas.IUpasDao;
import bo.upas.PermissionBo;
import bo.upas.UserBo;
import bo.upas.UserRoleBo;
import bo.upas.UsergroupBo;
import bo.upas.UsergroupPermBo;
import modules.cluster.BaseQueueThread;
import modules.registry.IRegistry;
import play.Logger;
import queue.message.AddPermissionMessage;
import queue.message.AddPermissionToGroupMessage;
import queue.message.AddUserMessage;
import queue.message.AddUserToGroupMessage;
import queue.message.AddUsergroupMessage;
import queue.message.BaseMessage;
import queue.message.RemovePermissionFromGroupMessage;
import queue.message.RemovePermissionMessage;
import queue.message.RemoveUserFromGroupMessage;
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
                addPermission((AddPermissionMessage) baseMsg);
                return true;
            } else if (baseMsg instanceof AddUsergroupMessage) {
                addUsergroup((AddUsergroupMessage) baseMsg);
                return true;
            } else if (baseMsg instanceof AddUserMessage) {
                addUser((AddUserMessage) baseMsg);
                return true;
            } else if (baseMsg instanceof RemovePermissionMessage) {
                removePermission((RemovePermissionMessage) baseMsg);
                return true;
            } else if (baseMsg instanceof RemoveUsergroupMessage) {
                removeUsergroup((RemoveUsergroupMessage) baseMsg);
                return true;
            } else if (baseMsg instanceof RemoveUserMessage) {
                removeUser((RemoveUserMessage) baseMsg);
                return true;
            } else if (baseMsg instanceof AddUserToGroupMessage) {
                addUserToGroup((AddUserToGroupMessage) baseMsg);
                return true;
            } else if (baseMsg instanceof RemoveUserFromGroupMessage) {
                removeUserFromGroup((RemoveUserFromGroupMessage) baseMsg);
                return true;
            } else if (baseMsg instanceof AddPermissionToGroupMessage) {
                addPermissionToGroup((AddPermissionToGroupMessage) baseMsg);
                return true;
            } else if (baseMsg instanceof RemovePermissionFromGroupMessage) {
                removePermissionFromGroup((RemovePermissionFromGroupMessage) baseMsg);
                return true;
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

    private void addPermission(AddPermissionMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            UpasApi upasApi = getRegistry().getUpasApi();
            PermissionBo perm = upasApi.addPermission(app, msg.getId(), msg.getTitle(),
                    msg.getDescription());
            if (perm == null) {
                Logger.error("Failed to add permission for app [" + msg.getAppId() + "]: " + msg);
            }
        }
    }

    private void removePermission(RemovePermissionMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            IUpasDao upasDao = getRegistry().getUpasDao();
            PermissionBo perm = upasDao.getPermission(app, msg.getId());
            if (perm != null) {
                UpasApi upasApi = getRegistry().getUpasApi();
                upasApi.remove(app, perm);
            } else {
                Logger.warn("Permission [" + msg.getId() + "] doesnot exist in app ["
                        + msg.getAppId() + "]: " + msg);
            }
        }
    }

    private void addPermissionToGroup(AddPermissionToGroupMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            UpasApi upasApi = getRegistry().getUpasApi();
            String permId = msg.getPermissionId();
            String groupId = msg.getUsergroupId();
            UsergroupPermBo bo = upasApi.addPermissionToGroup(app, permId, groupId);
            if (bo == null) {
                Logger.error("Failed to add permission [" + permId + "] to group [" + groupId
                        + "] for app [" + msg.getAppId() + "]: " + msg);
            }
        }
    }

    private void removePermissionFromGroup(RemovePermissionFromGroupMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            String permId = msg.getPermissionId();
            String groupId = msg.getUsergroupId();
            IUpasDao upasDao = getRegistry().getUpasDao();
            UsergroupPermBo bo = upasDao.getUsergroupPerm(app, groupId, permId);
            if (bo != null) {
                UpasApi upasApi = getRegistry().getUpasApi();
                upasApi.remove(app, bo);
            }
        }
    }

    private void addUsergroup(AddUsergroupMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            UpasApi upasApi = getRegistry().getUpasApi();
            UsergroupBo ug = upasApi.addUsergroup(app, msg.getId(), msg.isGod(), msg.getTitle(),
                    msg.getDescription());
            if (ug == null) {
                Logger.error("Failed to add usergroup for app [" + msg.getAppId() + "]: " + msg);
            }
        }
    }

    private void removeUsergroup(RemoveUsergroupMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            IUpasDao upasDao = getRegistry().getUpasDao();
            UsergroupBo ug = upasDao.getUsergroup(app, msg.getId());
            if (ug != null) {
                UpasApi upasApi = getRegistry().getUpasApi();
                upasApi.remove(app, ug);
            } else {
                Logger.warn("Usergroup [" + msg.getId() + "] doesnot exist in app ["
                        + msg.getAppId() + "]: " + msg);
            }
        }
    }

    private void addUser(AddUserMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            UpasApi upasApi = getRegistry().getUpasApi();
            UserBo user = upasApi.addUser(app, msg.getId(), msg.getDataMap());
            if (user == null) {
                Logger.error("Failed to add user for app [" + msg.getAppId() + "]: " + msg);
            }
        }
    }

    private void removeUser(RemoveUserMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            IUpasDao upasDao = getRegistry().getUpasDao();
            UserBo user = upasDao.getUser(app, msg.getId());
            if (user != null) {
                UpasApi upasApi = getRegistry().getUpasApi();
                upasApi.remove(app, user);
            } else {
                Logger.warn("User [" + msg.getId() + "] doesnot exist in app [" + msg.getAppId()
                        + "]: " + msg);
            }
        }
    }

    private void addUserToGroup(AddUserToGroupMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            UpasApi upasApi = getRegistry().getUpasApi();
            String userId = msg.getUserId();
            String groupId = msg.getUsergroupId();
            UserRoleBo bo = upasApi.addUserToGroup(app, userId, groupId);
            if (bo == null) {
                Logger.error("Failed to add user [" + userId + "] to group [" + groupId
                        + "] for app [" + msg.getAppId() + "]: " + msg);
            }
        }
    }

    private void removeUserFromGroup(RemoveUserFromGroupMessage msg) {
        AppBo app = validateApp(msg);
        if (app != null) {
            String userId = msg.getUserId();
            String groupId = msg.getUsergroupId();
            IUpasDao upasDao = getRegistry().getUpasDao();
            UserRoleBo bo = upasDao.getUserRole(app, userId, groupId);
            if (bo != null) {
                UpasApi upasApi = getRegistry().getUpasApi();
                upasApi.remove(app, bo);
            }
        }
    }
}
