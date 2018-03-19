package com.fastweb.web.dao.entity;

import java.util.Date;

import com.fastweb.core.jdbc.annotation.Id;
import com.fastweb.core.jdbc.annotation.Table;

@Table(name="user")
public class User {

	@Id
	private Long id;
	private String name;
	private String sex;
	private Date birthday;
	private String address;
	private String email;
	private String remark;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		
		return String.format("id=%s,name=%s,sex=%s,birthday=%s", id,name,sex,birthday);
	}
	
	
}
