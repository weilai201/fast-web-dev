package com.fastweb.core.jdbc.mysql;

import com.fastweb.core.jdbc.QueryColumnsBuilder;

public class MysqlQueryColumnsBuilder extends QueryColumnsBuilder{

	private int columnsSize=0;
	@Override
	public QueryColumnsBuilder add(String column) {
		
		if(columnsSize>0){
			columns.append(",");
		}
		
		columns.append(" `").append(column).append(" `");
		
		columnsSize++;
		return this;
	}

}
