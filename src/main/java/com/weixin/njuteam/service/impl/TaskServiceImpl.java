package com.weixin.njuteam.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.weixin.njuteam.config.AccessTokenTask;
import com.weixin.njuteam.dao.help.HelpInfoMapper;
import com.weixin.njuteam.dao.help.SeekHelpInfoMapper;
import com.weixin.njuteam.dao.manager.ManagerNucleicAcidBookingMapper;
import com.weixin.njuteam.dao.manager.ManagerNucleicAcidInfoMapper;
import com.weixin.njuteam.dao.manager.ManagerNucleicAcidTestingMapper;
import com.weixin.njuteam.dao.nucleic.NucleicAcidBookingMapper;
import com.weixin.njuteam.dao.nucleic.NucleicAcidInfoMapper;
import com.weixin.njuteam.dao.nucleic.NucleicAcidTestingMapper;
import com.weixin.njuteam.entity.po.nucleic.RemindPO;
import com.weixin.njuteam.entity.vo.help.HelpTaskVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpTaskVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTaskVO;
import com.weixin.njuteam.entity.vo.nucleic.RemindVO;
import com.weixin.njuteam.entity.vo.nucleic.TaskVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.exception.UpdateException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import com.weixin.njuteam.service.TaskService;
import com.weixin.njuteam.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Zyi
 */
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

	private final ThreadPoolTaskScheduler syncScheduler;
	private final HelpInfoMapper helpInfoMapper;
	private final SeekHelpInfoMapper seekHelpInfoMapper;
	private final AccessTokenTask accessTokenTask;

	private final NucleicAcidTestingMapper testingMapper;
	private final NucleicAcidInfoMapper infoMapper;
	private final NucleicAcidBookingMapper bookingMapper;

	private final ManagerNucleicAcidTestingMapper managerTestingMapper;
	private final ManagerNucleicAcidBookingMapper managerBookingMapper;
	private final ManagerNucleicAcidInfoMapper managerInfoMapper;

	private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
	private final List<String> taskList = new CopyOnWriteArrayList<>();

	@Autowired
	public TaskServiceImpl(ThreadPoolTaskScheduler syncScheduler, HelpInfoMapper helpInfoMapper,
						   SeekHelpInfoMapper seekHelpInfoMapper, AccessTokenTask accessTokenTask,
						   NucleicAcidTestingMapper testingMapper, NucleicAcidInfoMapper infoMapper,
						   NucleicAcidBookingMapper bookingMapper, ManagerNucleicAcidTestingMapper managerTestingMapper,
						   ManagerNucleicAcidBookingMapper managerBookingMapper, ManagerNucleicAcidInfoMapper managerInfoMapper) {
		this.syncScheduler = syncScheduler;
		this.helpInfoMapper = helpInfoMapper;
		this.seekHelpInfoMapper = seekHelpInfoMapper;
		this.accessTokenTask = accessTokenTask;

		this.testingMapper = testingMapper;
		this.infoMapper = infoMapper;
		this.bookingMapper = bookingMapper;

		this.managerTestingMapper = managerTestingMapper;
		this.managerBookingMapper = managerBookingMapper;
		this.managerInfoMapper = managerInfoMapper;
	}

	@Scheduled(cron = "0 0 0 1/1 * *")
	private void removeExecutedTask() {
		// 删除已经执行过的任务
		// 每天删除一次
		for (Map.Entry<String, ScheduledFuture<?>> entry : taskMap.entrySet()) {
			String key = entry.getKey();
			ScheduledFuture<?> task = entry.getValue();
			if (task.isDone()) {
				log.info("Remove Task: " + key);
				taskMap.remove(key);
				taskList.remove(key);
			}
		}
	}

	@Override
	public List<String> getTaskList() {
		return taskList;
	}

	@Override
	public String addUserNucleicAcidTask(RemindVO remindVo) {
		// 移除已经执行过的task
		String taskName = RandomUtil.getRandomUUID();
		TaskVO task = TaskVO.builder()
			.name(taskName)
			.remindVo(remindVo)
			.startTime(remindVo.getExecuteTime())
			.build();

		// 此处的逻辑是 ，如果当前已经有这个名字的任务存在，先删除之前的，再添加现在的。（即重复就覆盖）
		if (taskMap.get(task.getName()) != null) {
			stopTask(task.getName());
		}

		// 检查是否设置了任务的开始时间 如果没有的话则返回null
		if (task.getStartTime() == null) {
			return null;
		}

		ScheduledFuture<?> schedule = syncScheduler.schedule(getRemindRunnable(task), task.getStartTime());
		taskMap.put(task.getName(), schedule);
		taskList.add(task.getName());

		return taskName;
	}

	@Override
	public void addUserNucleicAcidTask(HelpTaskVO helpTask) throws AddTaskException {
		String taskName = "helpInfo" + helpTask.getHelpId();
		if (taskMap.get(taskName) != null) {
			stopTask(taskName);
		}

		if (helpTask.getDeadLine() == null) {
			throw new AddTaskException("add task failed! help info id: " + helpTask.getHelpId());
		}

		ScheduledFuture<?> schedule = syncScheduler.schedule(getHelpInfoRunnable(helpTask), helpTask.getDeadLine());
		taskMap.put(taskName, schedule);
		taskList.add(taskName);
	}

	@Override
	public void addUserNucleicAcidTask(SeekHelpTaskVO seekHelpTask) throws AddTaskException {
		String taskName = "seekHelpInfo" + seekHelpTask.getSeekHelpId();
		if (taskMap.get(taskName) != null) {
			stopTask(taskName);
		}

		if (seekHelpTask.getDeadLine() == null) {
			throw new AddTaskException("add task failed! seek help info id: " + seekHelpTask.getSeekHelpId());
		}

		ScheduledFuture<?> schedule = syncScheduler.schedule(getSeekHelpInfoRunnable(seekHelpTask), seekHelpTask.getDeadLine());
		taskMap.put(taskName, schedule);
		taskList.add(taskName);
	}

	@Override
	public void addUserNucleicAcidTask(NucleicAcidTaskVO taskVo) throws AddTaskException {
		String taskName = "nucleicAcidTask" + taskVo.getType() + taskVo.getId();
		if (taskMap.get(taskName) != null) {
			stopTask(taskName);
		}

		if (taskVo.getDeadLine() == null) {
			throw new AddTaskException("add task failed! nucleic acid task type: " + taskVo.getType() + ", id: " + taskVo.getId());
		}

		ScheduledFuture<?> schedule = syncScheduler.schedule(getInfoRunnable(taskVo), taskVo.getDeadLine());
		taskMap.put(taskName, schedule);
		taskList.add(taskName);
	}

	@Override
	public void addManagerNucleicAcidTask(NucleicAcidTaskVO task) throws AddTaskException {
		String taskName = "managerTask" + task.getType() + task.getId();
		if (taskMap.get(taskName) != null) {
			stopTask(taskName);
		}

		if (task.getDeadLine() == null) {
			throw new AddTaskException("add task failed! nucleic acid task type: " + task.getType() + ", id: " + task.getId());
		}

		ScheduledFuture<?> schedule = syncScheduler.schedule(getManagerRunnable(task), task.getDeadLine());
		taskMap.put(taskName, schedule);
		taskList.add(taskName);
	}

	@Override
	public boolean containsTask(String taskName) {
		return taskList.contains(taskName);
	}

	private Runnable getRemindRunnable(TaskVO task) {
		return () -> {
			// 任务逻辑
			// 向微信发送请求
			sendPost(task.getRemindVo());
		};
	}

	private Runnable getHelpInfoRunnable(HelpTaskVO helpTask) {
		return () -> {
			// 更新 helpInfo 的状态
			int i = helpInfoMapper.updateFinishStatus(helpTask.getHelpId(), HelpFinishStatus.getStatusByValue(helpTask.getFinishStatus()));
			if (i <= 0) {
				throw new UpdateException("update help info error!");
			}
		};
	}

	private Runnable getSeekHelpInfoRunnable(SeekHelpTaskVO seekHelpTask) {
		return () -> {
			// 更新 seekHelpInfo 的状态
			int i = seekHelpInfoMapper.updateFinishStatus(seekHelpTask.getSeekHelpId(), SeekHelpFinishStatus.getStatusByValue(seekHelpTask.getFinishStatus()));
			if (i <= 0) {
				throw new UpdateException("update seek help info error!");
			}
		};
	}

	private Runnable getInfoRunnable(NucleicAcidTaskVO taskVo) {
		return () -> {
			// 更新finishStatus
			int i = -1;
			switch (taskVo.checkType()) {
				case BOOKING:
					i = bookingMapper.updateFinish(taskVo.getId(), FinishStatus.getStatusByValue(taskVo.getFinishStatus()));
					break;
				case TESTING:
					i = testingMapper.updateFinish(taskVo.getId(), FinishStatus.getStatusByValue(taskVo.getFinishStatus()));
					break;
				case REPORTING:
					i = infoMapper.updateRecordFinish(taskVo.getId(), FinishStatus.getStatusByValue(taskVo.getFinishStatus()));
					break;
				default:
			}

			if (i <= 0) {
				throw new UpdateNucleicAcidException("update user nucleic info error! type: " + taskVo.getType());
			}
		};
	}

	private Runnable getManagerRunnable(NucleicAcidTaskVO taskVo) {
		return () -> {
			int i = -1;
			switch (taskVo.checkType()) {
				case BOOKING:
					i = managerBookingMapper.updateBookingFinish(taskVo.getId(), FinishStatus.getStatusByValue(taskVo.getFinishStatus()));
					break;
				case TESTING:
					i = managerTestingMapper.updateTestingFinish(taskVo.getId(), FinishStatus.getStatusByValue(taskVo.getFinishStatus()));
					break;
				case REPORTING:
					i = managerInfoMapper.updateInfoFinish(taskVo.getId(), FinishStatus.getStatusByValue(taskVo.getFinishStatus()));
					break;
				default:
			}

			if (i <= 0) {
				throw new UpdateNucleicAcidException("update manager nucleic info error! type: " + taskVo.getType());
			}
		};
	}

	private void sendPost(RemindVO remind) {
		RemindPO remindPo = RemindPO.builder()
			.touser(remind.getTouser())
			.template_id(remind.getTemplateId())
			.data(remind.getData())
			.build();

		String jsonStr = JSON.toJSONString(remindPo);
		String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={0}";
		String accessToken = accessTokenTask.getAccessToken();
		url = url.replace("{0}", accessToken);
		HttpResponse res = HttpRequest.post(url).body(jsonStr).execute();
		// response res nothing to do with
		log.info("Status code: " + res.getStatus());
		log.info("Result body: " + res.body());
	}


	@Override
	public boolean stopTask(String name) {
		if (null == taskMap.get(name)) {
			return false;
		}
		ScheduledFuture<?> scheduledFuture = taskMap.get(name);
		scheduledFuture.cancel(true);
		taskMap.remove(name);
		taskList.remove(name);
		return true;
	}
}
