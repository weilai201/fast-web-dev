package com.fastweb.web.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastweb.core.jdbc.Model;
import com.fastweb.core.jdbc.repository.CrudRepository;
import com.fastweb.core.page.PageContext;
import com.fastweb.web.dao.entity.User;

@Component
public class UserReposity extends CrudRepository<User, Long>{
	
	@Autowired
	Model model;
	
	/**
	 * 复杂查询，直接使用model方式查询
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public PageContext findByPagination(int pageSize,int pageNo){
		String sql="select * from user";
		return model.queryForPagin(sql, pageNo, pageSize, User.class);
	}

}
