package com.weixin.njuteam.web.controller;

import com.weixin.njuteam.annotation.CurUserInfo;
import com.weixin.njuteam.annotation.NeedUserToken;
import com.weixin.njuteam.entity.vo.UserVO;
import com.weixin.njuteam.entity.vo.nucleic.RemindVO;
import com.weixin.njuteam.enums.StatusCode;
import com.weixin.njuteam.service.TaskService;
import com.weixin.njuteam.web.Response;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zyi
 */
@RestController
@RequestMapping("/task")
public class TaskController {

	public final TaskService taskService;

	@Autowired
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping("/list")
	@ApiOperation(value = "查看已经开始但还未执行的定时任务")
	public Response<List<String>> getStartingTaskList() {
		List<String> taskList = taskService.getTaskList();

		if (taskList == null) {
			return new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "获取任务列表失败");
		}

		return new Response<>(StatusCode.OK, "获取任务列表成功", taskList);
	}

	@NeedUserToken
	@PostMapping("/add")
	@ApiOperation(value = "加入新的任务")
	public Response<String> addTask(@CurUserInfo UserVO userVo, @ApiParam(value = "任务", name = "remind", example = "xxx") @RequestBody RemindVO remind) {
		remind.setTouser(userVo.getOpenId());
		String taskName = taskService.addUserNucleicAcidTask(remind);

		return taskName == null ? new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "定时任务添加失败!") : new Response<>(StatusCode.OK, "定时任务添加成功，任务名字:" + taskName, taskName);
	}

	@DeleteMapping("/delete/{taskName}")
	@ApiOperation(value = "删除还未执行的任务")
	public Response<Boolean> stopTask(@PathVariable String taskName) {
		boolean isStop = taskService.stopTask(taskName);

		return isStop ? new Response<>(StatusCode.OK, "停止任务成功", true) : new Response<>(StatusCode.INTERNAL_SERVER_ERROR, "停止任务失败", false);
	}
}
