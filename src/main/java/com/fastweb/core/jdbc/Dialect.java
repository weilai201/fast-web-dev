package com.fastweb.core.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fastweb.core.exception.BusinessException;
import com.fastweb.core.jdbc.annotation.Column;
import com.fastweb.core.jdbc.annotation.Id;
import com.fastweb.core.jdbc.annotation.Table;

/**
 * Dialect.
 */
public abstract class Dialect {
	public abstract String forModelSave(Object obj,List<Object> params);
	public abstract String forModelDeleteById(Object obj, List<Object> params);
	public abstract String forModelUpdate(Object obj, List<String> attrs,List<Object> params);
	public abstract String forModelUpdateExcludeAttrs(Object obj, List<String> attrs,List<Object> params);
	public abstract <T> String forModelFindById(Object obj, String columns,List<Object> params);
	public abstract String forPaginate(int pageNumber, int pageSize, StringBuilder findSql);
	
	public abstract WhereConditionBuilder getWhereConditionBuilder();
	public abstract QueryColumnsBuilder getQueryColumnsBuilder();
	
	public boolean isOracle() {
		return false;
	}
	
	public boolean isTakeOverDbPaginate() {
		return false;
	}
	
	public void fillStatement(PreparedStatement pst, Object... paras) throws SQLException {
		for (int i=0; i<paras.length; i++) {
			pst.setObject(i + 1, paras[i]);
		}
	}
	
	public String getDefaultPrimaryKey() {
		return "id";
	}
	
	protected boolean isPrimaryKey(String colName, String[] pKeys) {
		for (String pKey : pKeys)
			if (colName.equalsIgnoreCase(pKey))
				return true;
		return false;
	}
	
	public <T> String getTableName(Class<T> clz){
		if(clz==null){
			return null;
		}
		
		Table table=(Table) clz.getAnnotation(Table.class);
		
		String tableName=null;
		if(table!=null){
			tableName=table.name();
		}
		
		if(tableName==null || "".equals(tableName.trim())){
			tableName=clz.getSimpleName();
		}
		
		
		return tableName;
	}
	
	public static String getColumnName(Field field){
		String result=null;
		
		if(field==null){
			return null;
		}
		
		Column col=(Column)field.getAnnotation(Column.class);
		
		if(col!=null){
			result=col.name();
		}
		
		if(result==null || "".equals(result.trim())){
			Class clz=field.getDeclaringClass();
			
			try {
				Method method=clz.getMethod("get"+firstStrToUpper(field.getName()));
				
				result=getColumnName(method);
			} catch (Exception e) {} 
		}
		
		if(result==null || "".equals(result.trim())){
			result=field.getName();
		}
		
		return result;
		
	}
	
	public static String getColumnName(Method method){
		String result=null;
		
		if(method==null){
			return null;
		}
		
		Column col=(Column)method.getAnnotation(Column.class);
		
		if(col!=null){
			result=col.name();
		}
		
		return result;
	}
	
	public static String firstStrToUpper(String str){
		if(str==null || "".equals(str.trim())){
			return null;
		}
		
		return str.substring(0,1).toUpperCase()+str.substring(1);
	}
	
	public <T> List<String> getPrimaryKey(Class<T> clz){
		List<String> list=new ArrayList<String>();
		if(clz==null){
			return null;
		}
		
		Field[] fields=clz.getDeclaredFields();
		for(Field field:fields){
			Id id=(Id)field.getAnnotation(Id.class);
			if(id!=null){
				list.add(field.getName());
			}else{
				
				try {
					Method method = clz.getMethod("get"+firstStrToUpper(field.getName()));
					
					if(method!=null){
						Id id1=(Id)method.getAnnotation(Id.class);
						if(id1!=null){
							list.add(field.getName());
						}
					}
				} catch (Exception e) {
				} 
				
			}
		}
		
		return list;
	}
	
	public Object getColumnValueByFieldName(Object o,String fieldName){
		Class clz=o.getClass();
		
		try {
			Method method = clz.getMethod("get"+firstStrToUpper(fieldName));
			
			return method.invoke(o);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}
	
}






