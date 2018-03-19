package com.fastweb.core.jdbc;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.fastweb.core.exception.BusinessException;
import com.fastweb.core.page.PageContext;
import com.fastweb.core.page.Pagination;


/**
 * 数据库操作类
 * @author 张未来
 *
 */
@Component
public class Model implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 344734225623267471L;

	private static final Log LOGGER = LogFactory.getLog(Model.class);
	
	@Autowired
	private Dialect dialect;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 查询一条记录
	 * @param id 可以是基础类型，也可以是联合主键
	 * @param clz 记录类型
	 * @return
	 */
	 public <T> T get(Object id,Class<T> clz){
		 try{
			 List<String> list=dialect.getPrimaryKey(clz);
			 
			 T oTemp=clz.newInstance();
			 
			 String sql=null;
			 
			 List<Object> params=new ArrayList<Object>();
			 if(list.size()==1){
				 Field f=clz.getDeclaredField(list.get(0));
				 String setMethod=String.format("set%s",dialect.firstStrToUpper(f.getName()));
				 
				 Method method=clz.getMethod(setMethod,id.getClass());
				 
				 method.invoke(oTemp, id);
				 sql=dialect.forModelFindById(oTemp, null, params);
			 }else{
				 sql=dialect.forModelFindById(id, null, params);
			 }
			 
			 LOGGER.debug(String.format("Execute SQL is : %s", sql));
			 LOGGER.debug(String.format("Execute parameters are : %s", params==null?"":params.toString()));
			 
			 List<T> result = jdbcTemplate.query(sql, params.toArray(), new BeanPropertyRowMapper(clz));
			if (list == null || list.isEmpty()) {
				return null;
			} else if(list.size()==1){
				return  result.get(0);
			}else{
				LOGGER.error("too many rows!");
				throw new BusinessException("too many rows!");
			}
			 
		 }catch(Exception e){
			 LOGGER.error(e.getMessage());
			 e.printStackTrace();
		 }
		 
		 return null;
	 }
	 
	 /**
	  * 查询数据库中记录集合
	  * @param sql sql语句
	  * @param clz 数据类型
	  * @param args sql语句？对应的参数集合
	  * @return
	  */
	 public <T> List<T> query(String sql,Class<T> clz,Object... args){
		 
		 LOGGER.debug(String.format("Execute SQL is : %s", sql));
		 LOGGER.debug(String.format("Execute parameters are : %s", args==null?"":args.toString()));
		 
		 return jdbcTemplate.query(sql, args, new BeanPropertyRowMapper(clz));
	 }
	 
	 public <T> T queryForObject(String sql,Class<T> clz,Object ...args){
		 LOGGER.debug(String.format("Execute SQL is : %s", sql));
		 LOGGER.debug(String.format("Execute parameters are : %s", args==null?"":args.toString()));
		 
		 return jdbcTemplate.queryForObject(sql, clz, args);
	 }
	 
	 public <T> PageContext queryForPagin(String sql,int pageNo,int pageSize,Class<T> clz,Object... args){
		 
		 StringBuffer countSql=new StringBuffer();
		 countSql.append("select count(1) from (").append(sql).append(") t_1");
		 
		 Pagination pagination=new Pagination();
		 pagination.setPageNo(pageNo);
	     pagination.setPageSize(pageSize);
	     pagination.setTotal(queryForObject(countSql.toString(), Integer.class,args));
		 
		 String sql1=dialect.forPaginate(pageNo, pageSize, new StringBuilder(sql));
		 
		 LOGGER.debug(String.format("Execute SQL is : %s", sql1));
		 LOGGER.debug(String.format("Execute parameters are : %s", args==null?"":args.toString()));
		 
		 List<T> list=jdbcTemplate.query(sql1, args, new BeanPropertyRowMapper(clz));
		 
		 PageContext result=new PageContext();
		 result.setPage(pagination);
		 result.setList(list);
		 
		 return result;
	 }
	 
	 /**
	  * 获取第一条记录
	  * @param sql
	  * @param clz
	  * @param args
	  * @return
	  */
	 public <T> T getOne(String sql,Class<T> clz,Object... args){
		 List<T> list=query(sql, clz, args);
		 if(list==null || list.size()==0){
			 return null;
		 }
		 
		 return list.get(0);
	 }
	 
	 /**
	  * 保存数据库。未指定数据库列名时，默认使用字段名称作为数据库列名
	  * @param obj
	  * @return
	  */
	 public int save(Object obj){
		final List<Object> params=new ArrayList<Object>();
		final String sql=dialect.forModelSave(obj, params);
		
		LOGGER.debug(String.format("Execute SQL is : %s", sql));
		LOGGER.debug(String.format("Execute parameters are : %s", params==null?"":params.toString()));
		 
		KeyHolder keyHolder = new GeneratedKeyHolder();  
		
		
		int row=jdbcTemplate.update(new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql,  
                        Statement.RETURN_GENERATED_KEYS);  
                
				for(int i=0;i<params.size();i++){
					ps.setObject(i+1, params.get(i));
					
				}
                return ps;  
			}
		}, keyHolder);
		
		if(keyHolder!=null && keyHolder.getKey()!=null){
			Long key=keyHolder.getKey().longValue();
			List<String> list=dialect.getPrimaryKey(obj.getClass());
			if(list!=null && list.size()>0){
				try {
					Method method=obj.getClass().getMethod("set"+dialect.firstStrToUpper(list.get(0)), Long.class);
					method.invoke(obj, key);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return row;
	 }
	 
	 /**
	  * 更新数据库。未指定数据库列名时，默认使用字段名称作为数据库列名
	  * @param obj
	  * @return
	  */
	 public int update(Object obj){
		List<Object> params=new ArrayList<Object>();
		String sql=dialect.forModelUpdate(obj,null,params);
		
		LOGGER.debug(String.format("Execute SQL is : %s", sql));
		LOGGER.debug(String.format("Execute parameters are : %s", params==null?"":params.toString()));
		
		return jdbcTemplate.update(sql, params.toArray());
	 }
	 
	 /**
	  * 更新数据库。未指定数据库列名时，默认使用字段名称作为数据库列名
	  * @param obj
	  * @param attrs 需要更新的列名
	  * @return
	  */
	 public int updateUseAttrs(Object obj,List<String> attrs){
		List<Object> params=new ArrayList<Object>();
		String sql=dialect.forModelUpdate(obj,attrs,params);
		
		LOGGER.debug(String.format("Execute SQL is : %s", sql));
		LOGGER.debug(String.format("Execute parameters are : %s", params==null?"":params.toString()));
		
		return jdbcTemplate.update(sql, params.toArray());
	 }
	 
	 /**
	  * 更新数据库。未指定数据库列名时，默认使用字段名称作为数据库列名
	  * @param obj
	  * @param attrs 需要更新的列名
	  * @return
	  */
	 public int updateUseAttrs(Object obj,String ... args){
		List<Object> params=new ArrayList<Object>();
		
		List<String> attrs=new ArrayList<>();
		if(args!=null && args.length!=0){
			for(String attr:args){
				attrs.add(attr);
			}
		}
		String sql=dialect.forModelUpdate(obj,attrs,params);
		
		LOGGER.debug(String.format("Execute SQL is : %s", sql));
		LOGGER.debug(String.format("Execute parameters are : %s", params==null?"":params.toString()));
		
		return jdbcTemplate.update(sql, params.toArray());
	 }
	 
	 /**
	  * 更新数据库。指定的列不更新
	  * @param obj
	  * @param attrs
	  * @return
	  */
	 public int updateExcludeAttrs(Object obj,List<String> attrs){
		List<Object> params=new ArrayList<Object>();
		String sql=dialect.forModelUpdateExcludeAttrs(obj, attrs, params);
		
		LOGGER.debug(String.format("Execute SQL is : %s", sql));
		LOGGER.debug(String.format("Execute parameters are : %s", params==null?"":params.toString()));
		
		return jdbcTemplate.update(sql, params.toArray());
	 }
	 
	 public int update(String sql,Object... args){
		 return jdbcTemplate.update(sql, args);
	 }
	 /**
	  * 保存或更新。不存在则保存，存在则更新。
	  * @param obj
	  * @return
	  */
	 public int saveOrUpdate(Object obj){
		 Object oTemp=get(obj, obj.getClass());
		 if(oTemp!=null){
			 return update(obj);
		 }else{
			 return save(obj);
		 }
	 }
	 
	 /**
	  * 根据主键删除一条记录。
	  * @param obj
	  * @return
	  */
	 public int delete(Object obj){
		 List<Object> params=new ArrayList<Object>();
		 String sql=dialect.forModelDeleteById(obj, params);
		 
		 LOGGER.debug(String.format("Execute SQL is : %s", sql));
		 LOGGER.debug(String.format("Execute parameters are : %s", params==null?"":params.toString()));
			
		 return jdbcTemplate.update(sql, params.toArray());
	 }
	 
	 /**
	  * 删除
	  * @param sql
	  * @param args
	  * @return
	  */
	 public int delete(String sql,Object... args){
		 
		 LOGGER.debug(String.format("Execute SQL is : %s", sql));
		 LOGGER.debug(String.format("Execute parameters are : %s", args==null?"":args.toString()));
		 
		 return jdbcTemplate.update(sql, args);
	 }


}
