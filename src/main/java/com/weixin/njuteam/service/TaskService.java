package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.help.HelpTaskVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpTaskVO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidTaskVO;
import com.weixin.njuteam.entity.vo.nucleic.RemindVO;
import com.weixin.njuteam.exception.AddTaskException;

import java.util.List;

/**
 * @author Zyi
 */
public interface TaskService {

	/**
	 * 获取任务列表
	 *
	 * @return list of task
	 */
	List<String> getTaskList();

	/**
	 * 添加一个定时任务
	 *
	 * @param remindVo 定时任务
	 * @return 任务是否添加成功
	 */
	String addUserNucleicAcidTask(RemindVO remindVo);

	/**
	 * 添加一个定时更新 help info finish status 的定时任务
	 *
	 * @param helpTask help task
	 * @throws AddTaskException add task failed
	 */
	void addUserNucleicAcidTask(HelpTaskVO helpTask) throws AddTaskException;

	/**
	 * 添加一个定时更新 seek help info finish status 的定时任务
	 *
	 * @param seekHelpTask seek help task
	 * @throws AddTaskException add task failed
	 */
	void addUserNucleicAcidTask(SeekHelpTaskVO seekHelpTask) throws AddTaskException;

	/**
	 * 添加一个定时任务更新 核酸通知的 finish status 定时任务
	 *
	 * @param taskVo nucleic acid task
	 * @throws AddTaskException add task failed
	 */
	void addUserNucleicAcidTask(NucleicAcidTaskVO taskVo) throws AddTaskException;

	/**
	 * 添加一个定时任务更新 管理端 核酸通知的 finish status
	 *
	 * @param task manager nucleic acid task
	 * @throws AddTaskException add task failed
	 */
	void addManagerNucleicAcidTask(NucleicAcidTaskVO task) throws AddTaskException;

	/**
	 * is task list contains task
	 *
	 * @param taskName task name
	 * @return true if task list contains task, false otherwise
	 */
	boolean containsTask(String taskName);

	/**
	 * 停止一个定时任务
	 *
	 * @param name 任务名字 保证唯一
	 * @return 任务是否停止成功
	 */
	boolean stopTask(String name);
}
