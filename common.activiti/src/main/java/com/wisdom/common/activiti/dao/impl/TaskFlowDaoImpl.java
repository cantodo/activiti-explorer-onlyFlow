package com.wisdom.common.activiti.dao.impl;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.wisdom.common.activiti.CodeConstant;
import com.wisdom.common.activiti.dao.TaskFlowDao;
import com.wisdom.common.activiti.model.vo.FlowTypeVO;
import com.wisdom.common.activiti.model.vo.FlowVO;
import com.wisdom.common.activiti.model.vo.TaskVO;


@Repository
public class TaskFlowDaoImpl implements TaskFlowDao {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TaskFlowDaoImpl.class);
    
    @Resource
    private JdbcTemplate jdbcTemplate;
    
    
    @Override
    public List<FlowTypeVO> queryFlowType(String  userId ) {  
	try{
	    DatabaseMetaData md = this.jdbcTemplate.getDataSource()
	                  .getConnection().getMetaData(); 
	    LOGGER.info(md.getDatabaseProductName());  
	    LOGGER.info(md.getDatabaseProductVersion()); 
	    String dbType = md.getDatabaseProductName();
            //修改前后的流程审批算一类
	    StringBuffer sql = new StringBuffer();	
	    sql.append(" SELECT distinct a.count,a.processKey,b.NAME_  processName FROM");
	   
	    //db2
	    if(CodeConstant.DB2.equals(dbType)){
		    sql.append(" (SELECT COUNT(*)||'' count ,pro.processKey FROM ("
			     + " SELECT ");
		    sql.append(" SUBSTR(PROC_DEF_ID_ ,1,posstr(PROC_DEF_ID_,':')-1) ");
	    }else
		
	    //mySQL
	    if(CodeConstant.MYSQL.equals(dbType)){
		
		    sql.append(" (SELECT CONCAT(COUNT(*),'')  count ,pro.processKey FROM ("
			     + " SELECT ");
		    sql.append(" SUBSTRING(PROC_DEF_ID_ ,1,POSITION(':' IN PROC_DEF_ID_)-1) ");
	    }else
		
	    //oracle
	    if(CodeConstant.ORACLE.equals(dbType)){
		    sql.append(" (SELECT COUNT(*)||'' count ,pro.processKey FROM ("
			     + " SELECT ");
		    sql.append(" SUBSTR(PROC_DEF_ID_ ,1,instr(PROC_DEF_ID_ ,':' )-1) ");
	    }else{
	        return null;
	    }
		
	    sql.append(" AS processKey FROM ACT_RU_TASK WHERE ASSIGNEE_    like '%"+userId+"%' ")
	       .append(" ) pro GROUP BY  processKey ) a "
	       + " LEFT JOIN ACT_RE_PROCDEF b ON a.processKey =b.KEY_");

	    //修改前后的流程算两类
	    /*StringBuilder sql = new StringBuilder("SELECT  COUNT||'' count , PROC_DEF_ID_ "
	        	        + " FROM  ACT_RU_TASK WHERE ASSIGNEE_  like '%"+userName+"%' "
	        		+ " GROUP BY  PROC_DEF_ID_");*/
		
	    List<FlowTypeVO> data  = jdbcTemplate.query(sql.toString(), new Object[]{},
			                        new BeanPropertyRowMapper<FlowTypeVO>(FlowTypeVO.class));	
	    return data;
	
	}catch(Exception e){
	    LOGGER.info("获取数据源失败!");  
	    return null;
	
	}
    }


    @Override
    public List<FlowVO> queryFlowByProId(String processInstanceId) {
	
	StringBuffer sql = new StringBuffer(" SELECT act.EXECUTION_ID_  executionId, "
		+ " act.ACT_NAME_ actName,com.USER_ID_ userId, "
		+ " act.ASSIGNEE_ assignee, com.TIME_ endTime ,com.MESSAGE_ message"
		+ " FROM ACT_HI_ACTINST act "
		+ " LEFT JOIN  ACT_HI_COMMENT com "
		+ " ON   act.TASK_ID_ = com.TASK_ID_ AND com.ACTION_ ='"+CodeConstant.ACT_TYPE_COMMENT+"'"
		+ " WHERE act.ACT_TYPE_ = '"+CodeConstant.ACT_TYPE_TASK+"' AND "
		+ " act.PROC_INST_ID_ = ?"     //'"+processInstanceId+"'"
	);

	List<FlowVO> data  = jdbcTemplate.query(sql.toString(), new Object[]{processInstanceId}, 
		                       new BeanPropertyRowMapper<FlowVO>(FlowVO.class));
		
        return data;
    }
   
    @Override
    public List<FlowTypeVO> queryFinishTaskType(String userId){
        try{
	    DatabaseMetaData md = this.jdbcTemplate.getDataSource()
                                      .getConnection().getMetaData(); 
            LOGGER.info(md.getDatabaseProductName());  
            LOGGER.info(md.getDatabaseProductVersion()); 
            String dbType = md.getDatabaseProductName();
	    StringBuffer sql = new StringBuffer("SELECT distinct  c.count,c.processKey,d.NAME_  processName FROM ("
		     + " SELECT COUNT(*)||'' count ,pro.processKey FROM ("
		     + " SELECT");
	    if(CodeConstant.DB2.equals(dbType)){
		sql.append(" SUBSTR(a.PROC_DEF_ID_ ,1,posstr(a.PROC_DEF_ID_,':')-1) ");
	    }else 
	    
	    //mySQL
	    if(CodeConstant.MYSQL.equals(dbType)){
	        sql.append(" SUBSTRING(PROC_DEF_ID_ ,1,POSITION(':' IN PROC_DEF_ID_)-1) ");
	    }else	
	    //oracle
	    if(CodeConstant.ORACLE.equals(dbType)){
	        sql.append(" SUBSTR(PROC_DEF_ID_ ,1,instr(PROC_DEF_ID_ ,':' )-1) ");
            }else{
	        return null;
	    }
	    sql.append(" AS processKey  FROM ACT_HI_ACTINST a left join ACT_HI_COMMENT b "
		     + " on b.ACTION_ = '"+CodeConstant.ACT_TYPE_COMMENT+"' and  a.TASK_ID_ = b.TASK_ID_"
		     + " where b.TIME_ IS NOT NULL and b.USER_ID_= '"+userId+"'"
		     + " ) pro GROUP BY  processKey )  c"
		     + " LEFT JOIN ACT_RE_PROCDEF d ON c.processKey =d.KEY_");
	
	    List<FlowTypeVO> data  = jdbcTemplate.query(sql.toString(), new Object[]{},
                  new BeanPropertyRowMapper<FlowTypeVO>(FlowTypeVO.class));
	    return data;
        }catch(SQLException e){
            LOGGER.info("获取数据源失败!");  
	    return null;
        }
    }
 
    @Override
    public List<TaskVO> queryFinishTask(String procDefId,String userId){
	 StringBuilder sql = new StringBuilder(" select  a.ACT_ID_ taskCode , a.EXECUTION_ID_  executionId,"
	 	+ " a.TASK_ID_ taskId  ,a.ACT_NAME_  taskName,"
	 	+ " b.TIME_ endTime,b.MESSAGE_ message"
	 	+ " from ACT_HI_ACTINST a  left join ACT_HI_COMMENT b "
	 	+ " on b.ACTION_ = '"+CodeConstant.ACT_TYPE_COMMENT+"' and  a.TASK_ID_ = b.TASK_ID_ "
		+ " where b.TIME_ IS NOT NULL  and b.USER_ID_='"+userId
		+ "'and  a.PROC_DEF_ID_  like  '"+procDefId+"%' ");
	 
	List<TaskVO> data  = jdbcTemplate.query(sql.toString(), new Object[]{}, 
		                       new BeanPropertyRowMapper<TaskVO>(TaskVO.class));
	return data;
    }
    
    
    

    @Override
    public void insertActToBusiness(String businessId, String businessType,
	    String executionId) {
	
	StringBuilder sql = new StringBuilder(" insert into ATC_BUSINESS_INFO"
		      + " (businessId,businessType,executionId) values(?,?,?)"); 
        jdbcTemplate.update(sql.toString(),new Object[]{businessId,businessType,executionId});  
    }


    


    @Override
    public String getExeIdByBusinessAndType(String businessId,
	    String businessType) {
	
	StringBuilder sql = new StringBuilder(" select executionId From ATC_BUSINESS_INFO"
				+ " where businessId = ? and businessType=?"); 
	
	return (String)jdbcTemplate.queryForObject(sql.toString(),  
                new Object[]{businessId,businessType},String.class);
    }


    @Override
    public String getBusinessIdByExeId(String executionId) {
	
	StringBuilder sql = new StringBuilder(" select businessId From ATC_BUSINESS_INFO"
		+ " where executionId = ? "); 

	return (String)jdbcTemplate.queryForObject(sql.toString(),  
		new Object[]{executionId},String.class);
    }


    @Override
    public void deleteByBusiness(String businessId, String businessType) {
	
	 StringBuilder sql = new StringBuilder(" delete from ATC_BUSINESS_INFO "
	 					+ " where businessId = ? and businessType=?"); 
	 jdbcTemplate.update(  
	                sql.toString(),   
	                new Object[]{businessId,businessType}); 
    }


    @Override
    public void deleteByExeId(String executionId) {
	
	StringBuilder sql = new StringBuilder(" delete from ATC_BUSINESS_INFO "
			+ " where executionId = ? "); 
	jdbcTemplate.update(  
			sql.toString(),   
			new Object[]{executionId}); 
    }
    
    
    
    
    
}
