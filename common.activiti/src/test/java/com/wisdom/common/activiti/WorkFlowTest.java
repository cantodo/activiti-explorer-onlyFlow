package com.wisdom.common.activiti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.activiti.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.wisdom.common.activiti.model.vo.FlowTypeVO;
import com.wisdom.common.activiti.model.vo.FlowVO;
import com.wisdom.common.activiti.model.vo.SubmitFlowConditionVO;
import com.wisdom.common.activiti.model.vo.TaskVO;
import com.wisdom.common.activiti.service.LineDisposeService;
import com.wisdom.common.activiti.service.TaskFlowService;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testSpring.xml")
//@Transactional //开启事务
public class WorkFlowTest {
    
    
    private static final Logger logger = LoggerFactory.getLogger(WorkFlowTest.class);
    
    
    @Autowired
    private  TaskFlowService taskFlowService;
    
    @Autowired
    private  LineDisposeService  lineDisposeService;
    
    @Autowired
    private RepositoryService repositoryService;
    
    /**
     *编制数据提交 
     */
    @Test
    public void submit(){
	
	System.out.println("++++++测试 提交++++++");
	
	logger.info("测试");
	
	SubmitFlowConditionVO vo = new SubmitFlowConditionVO();
	vo.setProcessDefinitionKey("askForLeave");
	vo.setBusinessId("4");
	vo.setBusinessType("askForLeave");
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("id", "4");
	vo.setDetailParamMap(map);
	vo.setBusinessURL("/sys/resTemplate/toResTemplate");

	Map<String, Object> variables = new HashMap<String, Object>();
	variables.put("submitUser", "shanglei001");
	variables.put("orgCode","org001");
	
	String flowCondition= "";
	String submitUser="shanglei001";


	taskFlowService.submit(vo, variables, flowCondition, submitUser);
	
	System.out.println("++++++提交成功++++++");
    }
    
    @Test
    public void reSubmit(){
	
	System.out.println("++++++测试 提交++++++");
	
	logger.info("测试");
	
	String businessId="111";
	String businessType="Test";
	
	Map<String, Object> variables = new HashMap<String, Object>();
	variables.put("submitUser", "user9527");
	variables.put("orgCode","user9527");
	
	String flowCondition= "7";
	String submitUser="shangl02";
	
	taskFlowService.reSubmit(businessId, businessType, variables, flowCondition, submitUser);
	
	System.out.println("++++++提交成功++++++");
    }
    
   
    
    /**
     * 审核
     */
    @Test
    public void auditFlow(){
	
	String taskId = "57513";   
	String userId="shangl006";
	String message ="测试通过";	
	String flowCondition= "4";//通过
	String complete="";
	String status = "通过";
	
	String a = taskFlowService.auditFlow(taskId, userId, message, flowCondition,"",status);
	logger.info("测试");
	//System.out.println("++++++提交成功++++++"+a);
    }
    
    
    /**
     * 查询任务
     */
    @Test
    public void queryFlowData(){
	 
	 //String procDefId="IdKey2:17:77510";
	 String procDefId="IdKey2"; //type
	 String auditUser="TEST";
	 
	 List<TaskVO>  list = taskFlowService.queryFlowData(procDefId,auditUser);
	 if(!list.isEmpty()){
	     System.out.println("List长度"+list.size());
		 
		 for (TaskVO t : list) {
		    
		    System.out.print("CreateUser:" +t.getCreateUser()+ ",");
		    System.out.print("CreateTime:" + t.getCreateDate() + ",");
		    System.out.print("ExecutionId:"+t.getExecutionId() + ",");
		    System.out.print("name:" + t.getTaskName() + ",");
		    System.out.print("TaskId:" + t.getTaskId() + ",");
		    System.out.print("FlowType:" + t.getTaskCode() + ",");
		    System.out.print("pid:" + t.getProcessInstanceId() + ",");
		}
	 }
    }
    
    /**
     * 查询任务类别
     */
    @Test
    public void  queryFlowType(){
	 
	 String userName="-999";
	 
	 List<FlowTypeVO>  list = taskFlowService.queryFlowType(userName);
	 if(!list.isEmpty()){
	     System.out.println("List长度"+list.size());		 
		 for (FlowTypeVO t : list) {
		    System.out.print("COUNT:" + t.getCount() + ",");
		    System.out.print("ProcessKey:"+t.getProcessKey());
		}
	 }
    }
    
    /**
     *查询流程 
     */
    @Test
    public void  queryFlowByExeId(){
	
	String executionId ="17501";
	
	List<FlowVO> l = taskFlowService.queryFlowByExeId(executionId);
	
	if(!l.isEmpty()){
	     System.out.println("List长度"+l.size());
		 
		 for (FlowVO t : l) {
		    System.out.print("STATUS:" + t.getStatus() + ",");
		    System.out.print("NAME:"+t.getActName());
		    System.out.print("time:"+t.getEndTime());
		    System.out.print("message"+t.getMessage());
		    
		}
	 }
	
    }
    
   
    
    @Test
    public void getExeIdByBusinessAndType(){
	String businessId="111";
	String businessType="Test";
	
	String exeId = taskFlowService.getExeIdByBusinessAndType(businessId, businessType);
	
	System.out.print("成功"+exeId);
	
    }
    
    @Test
    public void getBusinessIdByExeId(){
	
	String executionId="2501";
	String businessId = taskFlowService.getBusinessIdByExeId(executionId);
	
	System.out.print("成功"+businessId);
    }
    
    
    @Test
    public void deleteByBusiness(){
	String businessId="111";
	String businessType="Test";
	taskFlowService.deleteByBusiness(businessId, businessType);
	System.out.print("删除成功");
    }
    
    @Test
    public void deleteByExeId() {
	
	String executionId="2501";
	
	taskFlowService.deleteByExeId(executionId);
	System.out.print("删除成功");
    }
    
    @Test
    public void getFlowData() throws ScriptException{
	
	ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine se = manager.getEngineByName("js");
       
        String str = "{'a'=='a'}";
         se.eval(str);
        System.out.println(se.eval(str));
	
	String   taskId ="67504";	
	taskFlowService.getFlowLineData(taskId);
	//lineDisposeService.queryNatureByTaskId(taskId);

    }
    
    @Test
    public void deploy()
    {
        // 部署流程定义
        repositoryService.createDeployment()
                         .addClasspathResource("newAskForLeave.bpmn20.xml")
                         .deploy();
        System.out.println("部署成功");
    }
    
    @Test
    public void  queryFinishTaskType(){
	 
	 String userName="-999";
	 
	 List<FlowTypeVO>  list = taskFlowService.queryFinishTaskType(userName);
	 if(!list.isEmpty()){
	     System.out.println("List长度"+list.size());		 
		 for (FlowTypeVO t : list) {
		    System.out.print("COUNT:" + t.getCount() + ",");
		    System.out.print("ProcessKey:"+t.getProcessKey());
		}
	 }
    }
    
    @Test
    public void  queryFinishTaskData(){
	 
	 String userName="-999";
	 String key ="askForLeave";
	 
	 List<TaskVO>  list = taskFlowService.queryFinishTask(key,userName);
	 if(!list.isEmpty()){
	     System.out.println("List长度"+list.size());		 
	 }
    }
    
    @Test
    public void  checkAuditUser(){
	
	String taskId ="12512";
	List<String>  l=taskFlowService.checkAuditType(taskId);
	if(!l.isEmpty()){
	     System.out.println("List长度"+l.size());		 
	 }
    }
}
