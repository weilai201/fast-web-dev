package com.fastweb.core.jdbc;

import java.util.ArrayList;
import java.util.List;

/**
 * 构建查询条件
 * @author zhang
 *
 */
public abstract class WhereConditionBuilder {
	
	protected StringBuffer queryCondition=new StringBuffer();
	protected List<Object> params=new ArrayList<>();
	
	public abstract WhereConditionBuilder and(String key,Object value);
	public abstract WhereConditionBuilder or(String key,Object value);
	
	public WhereConditionBuilder leftParenthesis(){
		queryCondition.append(" ( ");
		return this;
	}
	
	public WhereConditionBuilder rightParenthesis(){
		queryCondition.append(" ) ");
		return this;
	}
	
	public String getSql(){
		return queryCondition.toString();
	}

	public List<Object> getParams(){
		return params;
	}
}
