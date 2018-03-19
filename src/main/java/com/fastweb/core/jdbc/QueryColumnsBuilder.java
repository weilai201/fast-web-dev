package com.fastweb.core.jdbc;

/**
 * 构建查询结果
 * @author zhang
 *
 */
public abstract class QueryColumnsBuilder {
	protected StringBuffer columns=new StringBuffer();
	
	public abstract QueryColumnsBuilder add(String column);
	
	public String getColumns(){
		return columns.toString();
	}
}
