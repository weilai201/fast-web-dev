package com.fastweb.core.jdbc.repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fastweb.core.exception.CrudRepositoryException;
import com.fastweb.core.jdbc.Dialect;
import com.fastweb.core.jdbc.Model;
import com.fastweb.core.jdbc.WhereConditionBuilder;

/**
 * Simple crud of database
 * @author zhang
 *
 */

@SuppressWarnings("unchecked")
public abstract class CrudRepository<T,ID> {
	
	private static final Log logger = LogFactory.getLog(CrudRepository.class);

	@Autowired
	private Dialect dialect;
	
	@Autowired
	private Model model;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final int BATCH_UPDATE_SIZE=20;
	
	 private Class<T> clazz = null;
	    {
	        Type type = getClass().getGenericSuperclass();
	        if( type instanceof ParameterizedType ){
	            ParameterizedType pType = (ParameterizedType)type;
	            Type claz = pType.getActualTypeArguments()[0];
	            if( claz instanceof Class ){
	                this.clazz = (Class<T>) claz;
	            }
	        }
	    }
	
	/**
	 * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
	 * entity instance completely.
	 * 
	 * @param entity must not be {@literal null}.
	 * @return the saved entity will never be {@literal null}.
	 */
	public <S extends T> S save(S entity){
		model.save(entity);
		return entity;
	}

	/**
	 * Saves all given entities.
	 * 
	 * @param entities must not be {@literal null}.
	 * @return the saved entities will never be {@literal null}.
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities){
		
		if(entities==null){
			logger.debug("The 'entities' is null!");
		}
		
		String sql=null;
		List<Object[]> params=new ArrayList<>();
		
		
		Iterator<S> iterator=entities.iterator();
		
		int iLoop=0;
		while(iterator.hasNext()){
			S obj=iterator.next();
			
			iLoop++;
			
			List<Object> paramsTemp=new ArrayList<>();
			sql=dialect.forModelSave(obj, paramsTemp);
			params.add(paramsTemp.toArray());
			
			if(iLoop%BATCH_UPDATE_SIZE==0){
				logger.debug(String.format("Execute SQL is : %s", sql));
				logger.debug(String.format("Execute parameters are : %s", params==null?"":params.toString()));
				
				jdbcTemplate.batchUpdate(sql, params);
				params.clear();
			}
			
		}
		
		if(!params.isEmpty()){
			logger.debug(String.format("Execute SQL is : %s", sql));
			logger.debug(String.format("Execute parameters are : %s", params==null?"":params.toString()));
			
			jdbcTemplate.batchUpdate(sql, params);
			params.clear();
		}
		
		return entities;
	}
	
	/**
	 * update entity
	 * @param entity
	 * @return
	 */
	public <S extends T> S update(S entity){
		model.update(entity);
		return entity;
	}

	/**
	 * Retrieves an entity by its id.
	 * 
	 * @param id must not be {@literal null}.
	 * @return the entity with the given id or {@literal Optional#empty()} if none found
	 * @throws IllegalArgumentException if {@code id} is {@literal null}.
	 */
	public T findById(ID id){
		return model.get(id, clazz);
	}
	
	public List<T> findByColumns(Map<String, Object> columns){
		
		if(columns==null || columns.size()==0){
			throw new CrudRepositoryException("columns cannot be null");
		}
		
		Iterator<Entry<String, Object>> iterator=columns.entrySet().iterator();
		
		WhereConditionBuilder builder=dialect.getWhereConditionBuilder();
		while(iterator.hasNext()){
			Entry<String, Object> entry=iterator.next();
			builder.and(entry.getKey(), entry.getValue());
		}
		
		StringBuffer sql=new StringBuffer();
		sql.append("select * from ")
		   .append(dialect.getTableName(clazz))
		   .append(" ")
		   .append(" where ")
		   .append(builder.getSql());
		
		List<Object> params=builder.getParams();
		
		return model.query(sql.toString(), clazz, params.toArray());
	}
	
	public T findOneByColumns(Map<String, Object> columns){
		List<T> list=findByColumns(columns);
		
		if(list==null || list.size()==0){
			return null;
		}
		if(list.size()>0){
			return list.get(0);
		}
		
		return null;
	}

	/**
	 * Returns whether an entity with the given id exists.
	 * 
	 * @param id must not be {@literal null}.
	 * @return {@literal true} if an entity with the given id exists, {@literal false} otherwise.
	 * @throws IllegalArgumentException if {@code id} is {@literal null}.
	 */
	public boolean existsById(T entity){
		
		long count=count();
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Returns all instances of the type.
	 * 
	 * @return all entities
	 */
	public List<T> findAll(){
		StringBuffer sql=new StringBuffer();
		sql.append("select * from ").append(dialect.getTableName(clazz));
		
		logger.debug(String.format("Execute SQL is : %s", sql));
		
		return model.query(sql.toString(), clazz);
		
	}

	/**
	 * Returns all instances of the type with the given IDs.
	 * 
	 * @param ids
	 * @return
	 */
//	public Iterable<T> findAllById(Iterable<ID> ids){
//		
//		
//	}

	/**
	 * Returns the number of entities available.
	 * 
	 * @return the number of entities
	 */
	public long count(){
		
		StringBuffer sql=new StringBuffer();
		sql.append("select count(1) from ").append(dialect.getTableName(clazz));
		
		logger.debug(String.format("Execute SQL is : %s", sql));
		
		Long count=model.queryForObject(sql.toString(),Long.class);
		return count;
	}

	/**
	 * Deletes a given entity.
	 * 
	 * @param entity
	 * @throws IllegalArgumentException in case the given entity is {@literal null}.
	 */
	public void delete(T entity){
		model.delete(entity);
	}

	/**
	 * Deletes all entities managed by the repository.
	 */
//	public void deleteAll(){
//		
//	}
}
