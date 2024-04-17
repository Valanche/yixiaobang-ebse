package com.weixin.njuteam.service.impl.manager;

import com.weixin.njuteam.dao.manager.ManagerMapper;
import com.weixin.njuteam.entity.po.manager.ManagerAndStudentPO;
import com.weixin.njuteam.entity.po.manager.ManagerPO;
import com.weixin.njuteam.entity.vo.manager.ManagerAndStudentVO;
import com.weixin.njuteam.entity.vo.manager.ManagerVO;
import com.weixin.njuteam.exception.ManagerExistException;
import com.weixin.njuteam.exception.NickNameDuplicateException;
import com.weixin.njuteam.exception.PasswordException;
import com.weixin.njuteam.service.ManagerService;
import com.weixin.njuteam.util.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;

/**
 * @author Zyi
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "manager")
public class ManagerServiceImpl implements ManagerService {

	private static final String DEFAULT_PASSWORD = "123456";
	private final ManagerMapper managerMapper;

	@Autowired
	public ManagerServiceImpl(ManagerMapper managerMapper) {
		this.managerMapper = managerMapper;
	}

	@Override
	@Cacheable(cacheNames = "manager", keyGenerator = "managerIdGenerator")
	public ManagerVO queryManagerById(@NotNull Long id) {
		if (id <= 0) {
			return null;
		}
		ManagerPO managerPo = managerMapper.queryManagerById(id);
		return managerPo == null ? null : new ManagerVO(managerPo);
	}

	@Override
	public ManagerVO queryManagerByInfo(String school, String institute, String major, String grade) {
		ManagerPO managerPo = managerMapper.queryManagerByInfo(school, institute, major, grade);
		return managerPo == null ? null : new ManagerVO(managerPo);
	}

	@Override
	@Cacheable(cacheNames = "studentList", keyGenerator = "managerIdGenerator")
	public ManagerAndStudentVO queryStudentList(Long id) {
		ManagerAndStudentPO managerAndStudentPo = managerMapper.queryManagerAndStudent(id);

		return managerAndStudentPo == null ? null : new ManagerAndStudentVO(managerAndStudentPo);
	}

	@Override
	public ManagerVO register(ManagerVO managerVo) throws PasswordException, NickNameDuplicateException, ManagerExistException {
		if (managerVo.getPassword() == null) {
			throw new PasswordException("password can not be null");
		}

		// 检查用户名是否重复
		String nickName = managerVo.getNickName();
		ManagerPO checkManager = managerMapper.queryManagerByNickName(nickName);
		if (checkManager != null) {
			throw new NickNameDuplicateException("nick name can not duplicate");
		}

		String existNickName = managerMapper.queryManagerByKey(managerVo.getSchool(), managerVo.getInstitute(), managerVo.getMajor(), managerVo.getGrade());
		if (existNickName != null) {
			throw new ManagerExistException();
		}

		ManagerPO managerPo = new ManagerPO(managerVo);
		// 对密码进行加密
		managerPo.setPassword(EncryptionUtil.encrypt(managerPo.getPassword()));
		int i = managerMapper.insertManager(managerPo);
		managerVo.setId(managerPo.getId());

		return i > 0 ? managerVo : null;
	}

	@Override
	public ManagerVO authLogin(String nickName, String password) throws PasswordException {
		ManagerPO managerPo = managerMapper.queryManagerByNickName(nickName);

		if (managerPo == null) {
			return null;
		}

		if (EncryptionUtil.match(password, managerPo.getPassword())) {
			return new ManagerVO(managerPo);
		}

		throw new PasswordException("密码错误!");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ManagerVO authLogin(ManagerVO managerVo) throws SQLException, NickNameDuplicateException {
		// 判断openid是不是空
		if (managerVo.getOpenId() == null || managerVo.getOpenId().trim().isEmpty()) {
			return null;
		}

		ManagerPO managerPo = managerMapper.queryManagerByInfo(managerVo.getSchool(), managerVo.getInstitute(), managerVo.getMajor(), managerVo.getGrade());
		ManagerPO registerManager = new ManagerPO(managerVo);

		if (managerPo == null) {
			// 说明是未注册的
			// 首先检查nickname是否重复
			if (registerManager.getNickName() != null && managerMapper.queryManagerByNickName(registerManager.getNickName()) != null) {
				throw new NickNameDuplicateException("nickname duplicate");
			}
			// 设置默认密码
			if (registerManager.getPassword() == null) {
				registerManager.setPassword(EncryptionUtil.encrypt(DEFAULT_PASSWORD));
			} else {
				registerManager.setPassword(EncryptionUtil.encrypt(registerManager.getPassword()));
			}
			int i = managerMapper.insertManager(registerManager);

			if (i <= 0) {
				// 说明插入不成功
				throw new SQLException("插入用户不成功！");
			}
		} else if (isNotTheSame(managerPo, managerVo)) {
			int i = managerMapper.updateManager(managerPo);
			if (i <= 0) {
				throw new SQLException("更新用户信息不成功! ");
			}
		}

		return managerPo == null ? new ManagerVO(registerManager) : new ManagerVO(managerPo);
	}

	@Override
	@CacheEvict(cacheNames = "manager", keyGenerator = "managerIdGenerator")
	public ManagerVO updateManagerInfo(Long id, ManagerVO managerVo) {
		managerVo.setId(id);
		ManagerPO managerPo = new ManagerPO(managerVo);
		int i = managerMapper.updateManager(managerPo);

		return i > 0 ? new ManagerVO(managerPo) : null;
	}

	@Override
	public ManagerVO updateManagerPassword(Long id, String password) {
		// 对密码加密并更新
		int i = managerMapper.updateManagerPassword(id, EncryptionUtil.encrypt(password));
		ManagerPO managerPo = managerMapper.queryManagerById(id);

		return i > 0 && managerPo != null ? new ManagerVO(managerPo) : null;
	}

	@Override
	@CacheEvict(cacheNames = "manager", keyGenerator = "managerIdGenerator")
	public boolean deleteManager(Long id) {
		int i = managerMapper.deleteManager(id);
		return i > 0;
	}

	private boolean isNotTheSame(ManagerPO managerPo, ManagerVO managerVo) {
		return managerPo.getNickName().equals(managerVo.getNickName()) && managerPo.getAvatarUrl().equals(managerVo.getAvatarUrl()) && managerPo.getOpenId().equals(managerVo.getOpenId());
	}
}
