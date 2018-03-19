package com.fastweb.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastweb.core.page.PageContext;
import com.fastweb.web.controller.vo.UserVo;
import com.fastweb.web.dao.UserReposity;
import com.fastweb.web.dao.entity.User;

@Service
public class UserService {

	@Autowired
	UserReposity userReposity;
	
	/**
	 * 保存用户信息
	 * @param userVo
	 */
	@Transactional
	public void save(UserVo userVo){
		User user=new User();
		BeanUtils.copyProperties(userVo, user);
		userReposity.save(user);
	}
	
	/**
	 * 批量保存
	 * @param list
	 */
	@Transactional
	public void batchSave(List<UserVo> list){
		List<User> entities=new ArrayList<>();
		for(UserVo userVo:list){
			User user=new User();
			BeanUtils.copyProperties(userVo, user);
			entities.add(user);
		}
		
		//此方法优化后，没20条记录保存一次
		userReposity.saveAll(entities);
	}
	
	/**
	 * 修改用户信息
	 * @param userVo
	 */
	@Transactional
	public void update(UserVo userVo){
		User user=new User();
		BeanUtils.copyProperties(userVo, user);
		userReposity.update(user);
	}
	
	/**
	 * 查询用户信息
	 * @param id
	 * @return
	 */
	public User get(Long id){
		return userReposity.findById(id);
	}
	
	/**
	 * 查询所有用户信息
	 * @return
	 */
	public List<User> findAll(){
		return userReposity.findAll();
	}
	
	public PageContext findByPagination(int pageSize,int pageNo){
		
		return userReposity.findByPagination(pageSize, pageNo);
	}
	
	/**
	 * 根据用户名查询用户
	 * @param name
	 * @return
	 */
	public List<User> findByName(String name){
		Map<String, Object> params=new HashMap<>();
		params.put("name", name);
		return userReposity.findByColumns(params);
	}
	
	/**
	 * 判断用户是否存在
	 * @param id
	 * @return
	 */
	public boolean exists(Long id){
		User user=new User();
		user.setId(id);
		return userReposity.existsById(user);
	}
	
	/**
	 * 查询用户数量
	 * @return
	 */
	public long count(){
		return userReposity.count();
	}
	
	/**
	 * 删除用户
	 * @param id
	 */
	@Transactional
	public void delete(long id){
		User user=new User();
		user.setId(id);
	    userReposity.delete(user);
	}
	
	
}
