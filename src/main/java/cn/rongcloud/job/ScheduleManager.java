package cn.rongcloud.job;

import cn.rongcloud.common.ApiException;
import cn.rongcloud.common.ErrorEnum;
import cn.rongcloud.config.RoomProperties;
import cn.rongcloud.im.IMHelper;
import cn.rongcloud.im.message.TicketExpiredMessage;
import cn.rongcloud.pojo.ScheduledTaskInfo;
import cn.rongcloud.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by weiqinxiao on 2019/3/15.
 */
@Slf4j
@Service
public class ScheduleManager implements SchedulingConfigurer {
    private ScheduledTaskRegistrar taskRegistrar;

    @Autowired
    RoomProperties roomProperties;

    @Autowired
    IMHelper imHelper;

    @Autowired
    @Lazy
    UserService userService;

    private ConcurrentHashMap<String, ScheduledTask> schedulingTasks = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Date> userIMOfflineMap = new ConcurrentHashMap<>();
    private ScheduledDelayTask userIMOfflineKickTask = new ScheduledDelayTask(new Runnable() {
        @Override
        public void run() {
            for (Map.Entry<String, Date> entry : userIMOfflineMap.entrySet()) {
                long currentTimeMillis = System.currentTimeMillis();
                log.info("userIMOfflineKickTask entry={}, currentTimeMillis={}", entry.getValue().getTime(), currentTimeMillis);
                if (currentTimeMillis - entry.getValue().getTime() > roomProperties.getUserIMOfflineKickTtl()) {
                    userIMOfflineMap.remove(entry.getKey());
                    userService.imOfflineKick(entry.getKey());
                }
            }
        }
    }, 60000, 60000, null);

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        this.taskRegistrar = scheduledTaskRegistrar;
        this.taskRegistrar.scheduleFixedDelayTask(userIMOfflineKickTask);
    }

    public void userIMOffline(String userId) {
        userIMOfflineMap.put(userId, new Date());
    }

    public void userIMOnline(String userId) {
        userIMOfflineMap.remove(userId);
    }

    public void addTask(String appkey, String secret, ScheduledTaskInfo task) {
        log.info("add task: {}", task);
        schedulingTasks.put(task.getTicket(), Objects.requireNonNull(taskRegistrar.scheduleFixedDelayTask(new ScheduledDelayTask(new Runnable() {
            @Override
            public void run() {
                log.info("task expired, execute task: {}", task);
                TicketExpiredMessage msg = new TicketExpiredMessage();
                msg.setFromUserId(task.getApplyUserId());
                msg.setToUserId(task.getTargetUserId());
                msg.setTicket(task.getTicket());
                try {
                    imHelper.publishMessage(appkey, secret, task.getTargetUserId(), task.getRoomId(), msg);
                } catch (Exception e) {
                    log.error("msg send error: {}", e.getMessage());
                }
                ScheduledTask scheduledTask = schedulingTasks.remove(task.getTicket());
                scheduledTask.cancel();
            }
        }, roomProperties.getTaskTtl() * 60, roomProperties.getTaskTtl(), task))));
    }

    public ScheduledTaskInfo cancelTask(String key) {
        ScheduledTask scheduledTask = schedulingTasks.remove(key);
        if (scheduledTask == null) {
            log.error("task not exist: key={}", key);
            throw new ApiException(ErrorEnum.ERR_APPLY_TICKET_INVALID);
        }
        ScheduledDelayTask task = (ScheduledDelayTask)scheduledTask.getTask();
        ScheduledTaskInfo taskInfo = task.getScheduledTaskInfo();
        scheduledTask.cancel();
        log.info("cancel task: {}", taskInfo);
        return taskInfo;
    }
}
