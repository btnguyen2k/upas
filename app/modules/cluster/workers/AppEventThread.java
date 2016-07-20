package modules.cluster.workers;

import com.github.ddth.queue.IQueueMessage;

import modules.cluster.BaseQueueThread;
import modules.registry.IRegistry;
import play.Logger;
import queue.message.BaseMessage;
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
        } else {
            if (Logger.isDebugEnabled()) {
                Logger.debug("\tMessage from queue: " + data);
            }
        }
        return false;
    }

}
