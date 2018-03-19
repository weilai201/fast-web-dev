package com.fastweb.core.jdbc.mysql;

import com.fastweb.core.jdbc.WhereConditionBuilder;

public class MysqlWhereConditionBuilder extends WhereConditionBuilder{
	private int numberOfCondition=0;
	
	public static WhereConditionBuilder getBuilder(){
		return new MysqlWhereConditionBuilder();
	}
	
	@Override
	public WhereConditionBuilder and(String key,Object value){
		if(numberOfCondition==0){
			queryCondition.append(" `").append(key).append("`=? ");
		}else{
			queryCondition.append(" and ").append(key).append("=? ");
		}
		
		params.add(value);
		
		numberOfCondition++;
		return this;
	}
	
	@Override
	public WhereConditionBuilder or(String key,Object value){
		if(numberOfCondition==0){
			queryCondition.append(" ").append(key).append("=? ");
		}else{
			queryCondition.append(" or ").append(key).append("=? ");
		}
		
		params.add(value);
		numberOfCondition++;
		
		return this;
	}

}
