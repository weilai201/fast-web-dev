package com.fastweb.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.fastweb.core.page.PageContext;
import com.fastweb.core.result.RtnData;
import com.fastweb.web.controller.vo.UserVo;
import com.fastweb.web.dao.entity.User;
import com.fastweb.web.service.UserService;

/**
 * 用户基础信息管理controller
 * @author zhang
 *
 */
@Validated
@RestController
@RequestMapping(produces = {"application/json;charset=utf-8"}, value = "/user")
public class UserController {

	@Autowired
	private UserService userService;
	/**
	 * 保存用户基础信息
	 * @param userVo
	 * @return
	 */
	@GetMapping(value = "/save")
	public RtnData saveUser(@Valid UserVo userVo){
		userService.save(userVo);
		return RtnData.ok();
	}
	
	
	/**
	 * 测试
	 * @param userVo
	 * @return
	 */
	@GetMapping(value = "/test")
	public RtnData test(){
		//1. save
		/*
		UserVo userVo=new UserVo();
		userVo.setName("zhangsan");
		userVo.setSex("boy");
		userVo.setBirthday(new Date());
		userVo.setAddress("address");
		userService.save(userVo);
		*/
		//2. batch save
		/*
		List<UserVo> batchSaveData=new ArrayList<>();
		for(int i=0;i<30;i++){
			UserVo userVo1=new UserVo();
			userVo1.setName("zhangsan"+i);
			userVo1.setSex("boy");
			userVo1.setBirthday(new Date());
			userVo1.setAddress("address");
			batchSaveData.add(userVo1);
		}
		userService.batchSave(batchSaveData);
		*/
		
		//3. query
		long count=userService.count();
		System.out.println("===================");
		System.out.println("User count is:"+count);
		
		//4. query all
		List<User> userList=userService.findAll();
		System.out.println("===================");
		System.out.println("findAll:");
		System.out.println(JSON.toJSONString(userList));
		
		//5. query by page
		PageContext pageContext=userService.findByPagination(10, 1);
		System.out.println("===================");
		System.out.println("findByPagination:");
		System.out.println(JSON.toJSONString(pageContext));
		
		
		//6. query by column
		List<User> list=userService.findByName("zhangsan");
		System.out.println("===================");
		System.out.println("findByName:");
		System.out.println(JSON.toJSONString(list));
		
		//7. find by id
		User user=userList.get(0);
		
		System.out.println("===================");
		System.out.println("get:");
		User tempUser=userService.get(user.getId());
		System.out.println(JSON.toJSONString(tempUser));
		
		
		//7. update
		user=userList.get(0);
		
		System.out.println("===================");
		System.out.println("update:");
		
		UserVo userVo=new UserVo();
		BeanUtils.copyProperties(user, userVo);
		userVo.setSex("girl");
		System.out.println(JSON.toJSONString(userVo));
		userService.update(userVo);
		
		//8. delete
		System.out.println("===================");
		System.out.println("delete:");
		
		userService.delete(user.getId());
		return RtnData.ok();
		
		
	}
	
}
