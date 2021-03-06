package modules.registry;

import com.github.ddth.queue.IQueue;

import akka.actor.ActorSystem;
import akka.cluster.Member;
import api.UpasApi;
import bo.app.IAppDao;
import bo.upas.IUpasDao;
import bo.user.IUserDao;
import play.Application;

public interface IRegistry {

    /*----------------------------------------------------------------------*/

    /**
     * Gets the current running Play application.
     * 
     * @return
     */
    public Application getPlayApplication();

    /**
     * Gets the {@link ActorSystem} instance.
     * 
     * @return
     */
    public ActorSystem getActorSystem();

    /*----------------------------------------------------------------------*/

    /**
     * Gets this node's id (name or IP).
     * 
     * @return
     */
    public String getNodeId();

    /**
     * Adds an Akka's member node to management list.
     * 
     * @param node
     */
    public void addNode(Member node);

    /**
     * Removes an Akka's member node from management list.
     * 
     * @param node
     */
    public void removeNode(Member node);

    /**
     * Checks if an Akka's member node is leader of a role.
     * 
     * @param role
     * @param node
     * @return
     */
    public boolean isLeader(String role, Member node);

    /**
     * Gets leader node for a role.
     * 
     * @param role
     * @return
     */
    public Member getLeader(String role);

    /*----------------------------------------------------------------------*/
    /**
     * Gets {@link IAppDao} instance.
     * 
     * @return
     */
    public IAppDao getAppDao();

    /**
     * Gets {@link IUserDao} instance.
     * 
     * @return
     */
    public IUserDao getUserDao();

    /**
     * Gets {@link IUpasDao} instance.
     * 
     * @return
     */
    public IUpasDao getUpasDao();

    /**
     * Gets {@link IQueue} to buffer application's events.
     * 
     * @return
     */
    public IQueue getQueueAppEvents();

    /**
     * Gets {@link IQueue} to buffer push notifications.
     * 
     * @return
     */
    public IQueue getQueuePushNotifications();

    /**
     * Gets {@link UpasApi} instance.
     * 
     * @return
     */
    public UpasApi getUpasApi();

}
