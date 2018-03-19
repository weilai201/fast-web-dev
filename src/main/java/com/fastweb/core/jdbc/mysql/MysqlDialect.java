package com.fastweb.core.jdbc.mysql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fastweb.core.exception.BusinessException;
import com.fastweb.core.jdbc.Dialect;
import com.fastweb.core.jdbc.QueryColumnsBuilder;
import com.fastweb.core.jdbc.WhereConditionBuilder;



/**
 * MysqlDialect.
 */
@Component
public class MysqlDialect extends Dialect{

	@Override
	public String forModelSave(Object obj, List<Object> params) {
		StringBuffer sql=new StringBuffer();
		Class clz=obj.getClass();
		
		sql.append("insert into `").append(getTableName(clz)).append("`(");
		StringBuilder temp = new StringBuilder(") values(");
		
		Field[] fields=clz.getDeclaredFields();
		
		int iLoop=0;
		for(Field f:fields){
			if("serialVersionUID".equals(f.getName())){
				continue;
			}
			
			if(iLoop>0){
				sql.append(",");
				temp.append(",");
			}
			
			sql.append("`").append(getColumnName(f)).append("`");
			temp.append("?");
			
			try {
				params.add(getColumnValueByFieldName(obj, f.getName()));
				
			} catch (Exception e) {
			} 
			iLoop++;
		}
		
		sql.append(temp.toString()).append(")");
		
		return sql.toString();
	}

	@Override
	public String forModelDeleteById(Object obj, List<Object> params) {
		List<String> list = getPrimaryKey(obj.getClass());
		if(list==null || list.size()==0){
			throw new BusinessException("no primary key specify!");
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("delete from `");
		sql.append(getTableName(obj.getClass()));
		sql.append("` where ");
		
		int iLoop=0;
		
		for(String str:list){
			if(iLoop>0){
				sql.append(" and ");
			}
			
			sql.append(" `").append(str).append("`=?");
			
			
			
			try {
				Field field=obj.getClass().getDeclaredField(str);
				params.add(getColumnValueByFieldName(obj, field.getName()));
			} catch (Exception e) {
				
			}
			
			iLoop++;
		}
		return sql.toString();
	}

	@Override
	public String forModelUpdate(Object obj, List<String> attrs,List<Object> params) {
		List<String> list = getPrimaryKey(obj.getClass());
		if(list==null || list.size()==0){
			throw new BusinessException("no primary key specify!");
		}
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("update `");
		sql.append(getTableName(obj.getClass()));
		sql.append("` set ");
		
		int iLoop=0;
		
		if(attrs!=null && attrs.size()>0){
			
			for(String str:attrs){
				if(iLoop>0){
					sql.append(",");
				}
				
				sql.append(" `").append(str).append("`=?");
				
				try{
				   Field f=obj.getClass().getDeclaredField(str);
				   params.add(getColumnValueByFieldName(obj, f.getName()));
				}catch(Exception e){
					throw new BusinessException("field is not exists!["+str+"]");
				}
				
				iLoop++;
			}
		}else{
			Field[] fields=obj.getClass().getDeclaredFields();
			
			for(Field f:fields){
				
				if("serialVersionUID".equals(f.getName())){
					continue;
				}
				
				if(iLoop>0){
					sql.append(",");
				}
				
				sql.append(" `").append(f.getName()).append("`=?");
				
				try{
					params.add(getColumnValueByFieldName(obj, f.getName()));
				}catch(Exception e){
					throw new BusinessException("field is not exists!["+f.getName()+"]");
				}
				
				iLoop++;
			}
		}
		
		sql.append(" where 1=1 ");
		
		for(String sp:list){
			sql.append(" and `").append(sp).append("`=?");
			
			try{
			   Field f=obj.getClass().getDeclaredField(sp);
			   params.add(getColumnValueByFieldName(obj, f.getName()));
			}catch(Exception e){
				throw new BusinessException("field is not exists!["+sp+"]");
			}
			
		}
		
		
		return sql.toString();
	}
	
	@Override
	public String forModelUpdateExcludeAttrs(Object obj, List<String> attrs,List<Object> params) {
		
		List<String> result=new ArrayList<>();
		Field[] fields=obj.getClass().getDeclaredFields();
		for(Field field:fields){
			
			if("serialVersionUID".equals(field.getName())){
				continue;
			}
			
			if(attrs!=null && !attrs.contains(field.getName())){
				result.add(field.getName());
			}
		}
		
		return forModelUpdate(obj, result, params);
		
	}
	
	@Override
	public <T> String forModelFindById(Object obj,String columns,List<Object> params) {
		List<String> list = getPrimaryKey(obj.getClass());
		if(list==null || list.size()==0){
			throw new BusinessException("no primary key specify!");
		}
		
		
		StringBuilder sql = new StringBuilder();
		sql.append("select  ");
		sql.append(columns==null?"*":columns).append(" from `");
		sql.append(getTableName(obj.getClass()));
		sql.append("` where ");
		
		int iLoop=0;
		
		for(String str:list){
			if(iLoop>0){
				sql.append(" and ");
			}
			
			sql.append(" `").append(str).append("`=?");
			
			
			
			try {
				Field field=obj.getClass().getDeclaredField(str);
				params.add(getColumnValueByFieldName(obj, field.getName()));
			} catch (Exception e) {
				
			}
			
			iLoop++;
		}
		return sql.toString();
	}
	
	public String forPaginate(int pageNumber, int pageSize, StringBuilder findSql) {
		int offset = pageSize * (pageNumber - 1);
		findSql.append(" limit ").append(offset).append(", ").append(pageSize);	
		return findSql.toString();
	}

	@Override
	public WhereConditionBuilder getWhereConditionBuilder() {
		return new MysqlWhereConditionBuilder();
	}

	@Override
	public QueryColumnsBuilder getQueryColumnsBuilder() {
		return new MysqlQueryColumnsBuilder();
	}
	
}
