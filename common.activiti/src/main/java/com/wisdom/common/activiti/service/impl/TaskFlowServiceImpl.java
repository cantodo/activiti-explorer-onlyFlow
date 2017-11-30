package com.wisdom.common.activiti.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.sf.json.JSONObject;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wisdom.common.activiti.CodeConstant;
import com.wisdom.common.activiti.dao.TaskFlowDao;
import com.wisdom.common.activiti.exception.RollbackException;
import com.wisdom.common.activiti.model.vo.FlowChartVO;
import com.wisdom.common.activiti.model.vo.FlowTypeVO;
import com.wisdom.common.activiti.model.vo.FlowVO;
import com.wisdom.common.activiti.model.vo.SelectDataVO;
import com.wisdom.common.activiti.model.vo.SubmitFlowConditionVO;
import com.wisdom.common.activiti.model.vo.TaskVO;
import com.wisdom.common.activiti.service.AuditUserService;
import com.wisdom.common.activiti.service.LineDisposeService;
import com.wisdom.common.activiti.service.TaskFlowService;
import com.wisdom.common.activiti.util.ProcessDiagramGenerator;

@SuppressWarnings("unused")
@Service("taskFlowService")
public class TaskFlowServiceImpl<T> implements TaskFlowService{
    
    private static final Logger logger = LoggerFactory.getLogger(TaskFlowServiceImpl.class);
    
    @Resource
    private ProcessEngine processEngine;
    
    @Resource
    private RuntimeService runtimeService;
    
    @Resource
    private TaskService taskService;
    
    @Resource
    private RepositoryService repositoryService;

    @Resource
    private HistoryService historyService;
    
    @Resource
    private AuditUserService<T> auditUserService;    
    
    @Resource
    private TaskFlowDao taskFlowDao;
       
    @Resource
    private  LineDisposeService  lineDisposeService;
    
    @Override
    public String submit(SubmitFlowConditionVO vo,Map<String, Object> variables,
            String flowCondition,String submitUser){
	//记录提交人
	variables.put(CodeConstant.SUBMIT_USER, submitUser);
	//流程实例Id
	String executionId= startFlow(vo.getProcessDefinitionKey(),variables);
	
	if(CodeConstant.AUDIT_PASS.equals(executionId)){
	    //审批结束
	    return executionId;
	}else if(executionId==null){
	    //流程错误(没有流程信息)
	    return null;
	}else{
	    Task t=taskService.createTaskQuery().executionId(executionId).singleResult();
	        
	    //存入 中间表 
	    taskFlowDao.insertActToBusiness(vo.getBusinessId(),vo.getBusinessType(),executionId);
	      
	    logger.info("节点ID:"+t.getId()); 
	    
	    JSONObject json = JSONObject.fromObject(vo.getDetailParamMap());
	    Map<String, Object>  paramMap = new HashMap<String, Object>();	    
	    paramMap.put(CodeConstant.BUSINESS_URL, vo.getBusinessURL());
	    paramMap.put(CodeConstant.BUSINESS_PARAM, json.toString());
	    
	    taskService.setVariables(t.getId(), paramMap);
	    
	    //提交进入下一步
	    //记录操作类型为 开始提交(CodeConstant.START)
	    enterNext(t.getId(),t.getProcessInstanceId(),submitUser, 
		    CodeConstant.START_STATUS,flowCondition);
	    //查询下一步审核人,并返回结果
	    return findNextAssignee(t.getProcessInstanceId());
	}	
    }
    
    @Override
    public String  reSubmit(String businessId,String businessType, Map<String, Object> variables,
            		    String flowCondition,String submitUser){
	String executionId = getExeIdByBusinessAndType( businessId,businessType);
	if(executionId!=null&&!"".equals(executionId)){
	    //获得当前任务节点
	    Task t=taskService.createTaskQuery().executionId(executionId).singleResult();
	    
	    //记录提交人
	    variables.put(CodeConstant.SUBMIT_USER, submitUser);
	    //数据权限判断标识存入 
	    taskService.setVariables(t.getId(), variables);
	    
	    //提交进入下一步
	    //记录操作类型为 重整后提交(CodeConstant.RESTART)
	    enterNext(t.getId(),t.getProcessInstanceId(),
	      		submitUser, CodeConstant.START_STATUS,flowCondition);
	    //查询下一步审核人,并返回结果
	    return findNextAssignee(t.getProcessInstanceId());
	}
	return null;
    }
    
    
    @Override
    public String auditFlow(String taskId, String userId, String message,
	    String flowCondition,String  auditUser,String status) {
	
	message=status+CodeConstant.SEPARATION+message;
	
	Task t = taskService.createTaskQuery()
			       .taskId(taskId)
		               .singleResult();
	enterNext(t.getId(),t.getProcessInstanceId(),
		userId,message,flowCondition);
	
	return findNextAssignee(t.getProcessInstanceId(),t.getId(),auditUser);	
    }
    
    /**
     * 进入下一步 
     * @param taskId    任务节点Id
     * @param processInstanceId 流程Id
     * @param userId    操作人
     * @param message   处理意见
     * @param flowCondition 流程走向判断条件
     */
    public void enterNext(String taskId,String processInstanceId,
	    String userId,String message,String flowCondition){
	
	//记录操作人
      	Authentication.setAuthenticatedUserId(userId);
	
      	//记录处理信息
   	taskService.addComment(taskId, processInstanceId, message);
      	
	if("".equals(flowCondition)){
	    taskService.complete(taskId);
	}else{
	    //流程走向判断
	    Map<String, Object> variables = new HashMap<String,Object>();	
	    variables.put(CodeConstant.AUDIT_CONDITION, flowCondition);		    
	    
	    taskService.complete(taskId, variables);
	}
	logger.info("提交成功!");
    }
    

    
    @Override
    public String startFlow(String processDefinitionKey,
	    Map<String, Object> variables) {
	
	ProcessInstance pi = runtimeService.startProcessInstanceByKey(processDefinitionKey,variables);	
	
	if(pi==null){
	    logger.info("流程结束,不需要审批!");
	    return CodeConstant.AUDIT_PASS;
	}else{
	    logger.info("流程ID:"+pi.getId());
	    //流程创建 进入 申请 节点,有且只有一个实例
	    List<Execution> exeList = runtimeService.createExecutionQuery()
	                .processInstanceId(pi.getId()).list();
	    if(!exeList.isEmpty()&&exeList.size()>0){
		logger.info("成功,流程实例ID:"+exeList.get(0).getId());
		return  exeList.get(0).getId();
	    }
	    logger.info("流程实例创建失败!");
	    return null;
	}
    }
    
    
    @Override
    public String findNextAssignee(String processInstanceId){
	
	//在完成任务之后，判断流程是否结束
        //如果流程结束了，更新请假单表的状态从1变成2（审核中-->审核完成）
	ProcessInstance pi = runtimeService.createProcessInstanceQuery()
					   .processInstanceId(processInstanceId)//使用流程实例ID查询
					   .singleResult();	
	
	if(pi==null){
	    //审批结束
	    return CodeConstant.AUDIT_PASS;
	}else{
	    //查找下一步的任务并且分配审核人	    
	    List<Execution> exeList = runtimeService.createExecutionQuery().processInstanceId(pi.getId()).list();
	    //串行流程只有一个实例,并行流程会有多个实例
	    if(exeList!=null&&exeList.size()>0){
		if(exeList.size()==1){
		     logger.info("串行审批任务!");
		     Task t=taskService.createTaskQuery().executionId(exeList.get(0).getId()).singleResult();
		     String roleIds=t.getAssignee();
		     
		     //Candidate groups
		     //驳回到申请节点,不要查询审核人
		     if(CodeConstant.FLOW_START.equals(roleIds)){
			 //记录驳回标识
			 taskService.setAssignee(t.getId(), CodeConstant.AUDIT_RETURN);
			 return CodeConstant.AUDIT_RETURN;
		     }
		     String[] roleId = null;
		    
		     if(roleIds!=null){
			 roleId=roleIds.split(",");		
		     }
		     
		     Map<String, Object> map = runtimeService.getVariables(t.getExecutionId()); 
		     //查询审核人
		     List<String> userList = auditUserService.findFlowUsersByAssignee(roleId,map);
			
		     if(userList!=null&&userList.size()>0){
			//将审核人存入工作流中
			getAuditUser(userList,t.getId());			    
		      }
		     return CodeConstant.AUDIT_AUDITING;
		}else{
		    //驳回 不会有这种情况
		    for(Execution e:exeList){
		       logger.info("并行审批任务!");
		       if(!e.getActivityId().endsWith(CodeConstant.PARALEL_IN)&&!e.getActivityId().endsWith(CodeConstant.PARALEL_OUT)){
			   //授权处理人
			   String roleIds=null;
			   Task t;
			   try{
    		               t=taskService.createTaskQuery().executionId(e.getId()).singleResult();
    			       roleIds=t.getAssignee();
			   }catch(Exception exc){
			       logger.info(exc.getMessage());
			       continue;
			   }
    			   String[] roleId=null;
    			   if(roleIds!=null){
    				roleId=roleIds.split(","); 
    			   }
    			   Map<String, Object> map = runtimeService.getVariables(t.getExecutionId());
    		           //查询审核人
    		           List<String> userList = auditUserService.findFlowUsersByAssignee(roleId,map);
    		           if(userList!=null&&userList.size()>0){
    		             //将审核人存入工作流中
    		             getAuditUser(userList,t.getId());     
    			   }
		       }
		    }
		    logger.info("进入下一步成功!");
		    return CodeConstant.AUDIT_AUDITING;
		}
	    }
	}
	return null;
    }
    
    
    /**
     * 根据流程Id查询下一步审核人并存入 流程表中
     * @param processInstanceId  流程Id
     * @param taskId    任务节点Id
     * @param auditUser 下一步审核人
     * @return 将审核人存入Assignee 返回结果信息:P(流程结束),R(流程退回),A(流程继续,进入下 一 审批节点)
     */
    public String findNextAssignee(String processInstanceId,String taskId,String auditUser){
	
	//在完成任务之后，判断流程是否结束
        //如果流程结束了，更新请假单表的状态从1变成2（审核中-->审核完成）
	ProcessInstance pi = runtimeService.createProcessInstanceQuery()
					   .processInstanceId(processInstanceId)//使用流程实例ID查询
					   .singleResult();	
	
	if(pi==null){
	    //审批结束
	    return CodeConstant.AUDIT_PASS;
	}else{
	    //下一步审核人
	    if(auditUser!=null&&!"".equals(auditUser)){	    
	        taskService.setAssignee(taskId, ","+auditUser+",");
		//指定下一步审核人,则状态一定时审核中
		return CodeConstant.AUDIT_AUDITING;
            }
	    
	    //查找下一步的任务并且分配审核人	    
	    List<Execution> exeList = runtimeService.createExecutionQuery().processInstanceId(pi.getId()).list();
	    //串行流程只有一个实例,并行流程会有多个实例
	    if(exeList!=null&&exeList.size()>0){
		if(exeList.size()==1){
		     logger.info("串行审批任务!");
		     Task t=taskService.createTaskQuery().executionId(exeList.get(0).getId()).singleResult();
		     String roleIds=t.getAssignee();
		     
		     //Candidate groups
		     //驳回到申请节点,不要查询审核人
		     if(CodeConstant.FLOW_START.equals(roleIds)){
			 //记录驳回标识
			 taskService.setAssignee(t.getId(), CodeConstant.AUDIT_RETURN);
			 return CodeConstant.AUDIT_RETURN;
		     }
		     String[] roleId = null;
		    
		     if(roleIds!=null){
			 roleId=roleIds.split(",");		
		     }
		     
		     Map<String, Object> map = runtimeService.getVariables(t.getExecutionId()); 
		     //查询审核人
		     List<String> userList = auditUserService.findFlowUsersByAssignee(roleId,map);
			
		     if(userList!=null&&userList.size()>0){
			//将审核人存入工作流中
			getAuditUser(userList,t.getId());			    
		      }
		     return CodeConstant.AUDIT_AUDITING;
		}else{
		    //驳回 不会有这种情况
		    for(Execution e:exeList){
		       logger.info("并行审批任务!");
		       if(!e.getActivityId().endsWith(CodeConstant.PARALEL_IN)&&!e.getActivityId().endsWith(CodeConstant.PARALEL_OUT)){
			   //授权处理人
			   String roleIds=null;
			   Task t;
			   try{
    		               t=taskService.createTaskQuery().executionId(e.getId()).singleResult();
    			       roleIds=t.getAssignee();
			   }catch(Exception exc){
			       logger.info(exc.getMessage());
			       continue;
			   }
    			   String[] roleId=null;
    			   if(roleIds!=null){
    				roleId=roleIds.split(","); 
    			   }
    			   Map<String, Object> map = runtimeService.getVariables(t.getExecutionId());
    		           //查询审核人
    		           List<String> userList = auditUserService.findFlowUsersByAssignee(roleId,map);
    		           if(userList!=null&&userList.size()>0){
    		             //将审核人存入工作流中
    		             getAuditUser(userList,t.getId());     
    			   }
		       }
		    }
		    logger.info("进入下一步成功!");
		    return CodeConstant.AUDIT_AUDITING;
		}
	    }
	}
	return null;
    }
    
    @Override
    public String getVariables(String taskId,String key) {
        //使用任务ID，查询任务对象
	Task t = taskService.createTaskQuery()
			    .taskId(taskId)
			    .singleResult();
	logger.info("实例 Id:"+t.getExecutionId());
        return (String)runtimeService.getVariable(t.getExecutionId(), key);
   }

    
    /**
     * 将审核人存入Assignee,如审核人不存在则回滚报错
     * @param userList
     * @param taskId
     */
    public void getAuditUser(List<String> userList,String taskId){
	
	String userId="";
	if(userList!=null&&userList.size()>0){
	    for(Object u:userList){
		userId+=u.toString().trim()+",";
	    }
	}
	if(userId!=null&&!"".equals(userId.trim())){
	    logger.info("审核人:"+userId);
	    taskService.setAssignee(taskId, userId);
	}else{
	    throw new RollbackException("审核人不存在!");
	}
    }
    
    @Override
    public List<TaskVO> queryFlowData(String type,String auditUser){
	
	List<Task> taskList = taskService.createTaskQuery()
					 //.processDefinitionId(type)
					 .processDefinitionKeyLike(type+"%")
		                         .taskAssigneeLike(
				          "%"+auditUser.trim()+"%")
				         .orderByTaskCreateTime().desc()
				         .list();
	logger.info("审核信息:"+taskList.get(0).getId());
	
	List<TaskVO> listVO = new ArrayList<TaskVO>();
	
	for(Task t: taskList){    	    
	    listVO.add(getTaskToTaskVO(t));
	}	
	
        return listVO;
    }

    /**
     * 将Task值转到TaskVO对应属性中
     * @param t
     * @return
     */
    public TaskVO getTaskToTaskVO(Task t){	
	TaskVO vo = new TaskVO();
	vo.setAssignee(t.getAssignee());	    
	vo.setBusinessDetailURL((String)runtimeService.getVariable(t.getExecutionId(), CodeConstant.BUSINESS_URL));
	vo.setBusinessDetailParam((String)runtimeService.getVariable(t.getExecutionId(), CodeConstant.BUSINESS_PARAM));
	vo.setCreateDate(t.getCreateTime());
	vo.setCreateUser((String)runtimeService.getVariable(t.getExecutionId(), CodeConstant.SUBMIT_USER));
	vo.setExecutionId(t.getExecutionId());
	vo.setProcessDefinitionId(t.getProcessDefinitionId());
	vo.setProcessInstanceId(t.getProcessInstanceId());
	vo.setTaskCode(t.getTaskDefinitionKey());
	vo.setTaskId(t.getId());
	vo.setTaskName(t.getName());	
	return vo;
    }
    
    @Override
    public List<FlowTypeVO> queryFlowType(String userName) {
	
	return taskFlowDao.queryFlowType(userName);
    }





    @Override
    public List<HistoricActivityInstance> queryHistoricActivityInstanceTest(
	    String processInstanceId) {
	
	HistoryService service = processEngine.getHistoryService();
	HistoricActivityInstanceQuery haiq=service.createHistoricActivityInstanceQuery();
	//设置流程Id
	haiq.processInstanceId(processInstanceId);
	//设置排序模式
	haiq.orderByHistoricActivityInstanceEndTime().asc();
	
	List<HistoricActivityInstance> hais = haiq.list();
	return hais;
    }





    @Override
    public InputStream findImageInputStream(String deploymentId,
	    String imageName) {
	return repositoryService.getResourceAsStream(deploymentId, imageName);
    }



    

    @Override
    public FlowChartVO viewFlowChart(String taskId) {

	/**一：查看流程图*/
	//1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
	ProcessDefinition pd = findProcessDefinitionByTaskId(taskId);
	FlowChartVO flowChartVO = new FlowChartVO();
	flowChartVO.setDeploymentId(pd.getDeploymentId());
	flowChartVO.setChartName(pd.getDiagramResourceName());
	
	/**二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中*/
	Map<String, Object> map = findCoordingByTask(taskId);
	flowChartVO.setPathX(String.valueOf(map.get("x")));
	flowChartVO.setPathY(String.valueOf(map.get("y")));
	flowChartVO.setPathHeight(String.valueOf(map.get("height")));
	flowChartVO.setPathWidth(String.valueOf(map.get("width")));	
	
	return flowChartVO;
    }
    
    /**
     * 查询流程对象
     * @param taskId
     * @return
     */
    public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
	//使用任务ID，查询任务对象
	Task task = taskService.createTaskQuery()//
				.taskId(taskId)//使用任务ID查询
				.singleResult();
	//获取流程定义ID
	String processDefinitionId = task.getProcessDefinitionId();
	//查询流程定义的对象
	ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()//创建流程定义查询对象，对应表act_re_procdef 
				.processDefinitionId(processDefinitionId)//使用流程定义ID查询
				.singleResult();
	return pd;
    }

    /**
     * 查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
		 map集合的key：表示坐标x,y,width,height
		 map集合的value：表示坐标对应的值
     * @param taskId
     * @return
     */
     public Map<String, Object> findCoordingByTask(String taskId) {
	 //存放坐标
         Map<String, Object> map = new HashMap<String,Object>();
         //使用任务ID，查询任务对象
         Task task = taskService.createTaskQuery()//
			.taskId(taskId)//使用任务ID查询
			.singleResult();
        //获取流程定义的ID
        String processDefinitionId = task.getProcessDefinitionId();
        //获取流程定义的实体对象（对应.bpmn文件中的数据）
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);
        //流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        //使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//创建流程实例查询
        			.processInstanceId(processInstanceId)//使用流程实例ID查询
        			.singleResult();
        //获取当前活动的ID
        String activityId = pi.getActivityId();
        //获取当前活动对象
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);//活动ID
        //获取坐标
        map.put("x", activityImpl.getX());
        map.put("y", activityImpl.getY());
        map.put("width", activityImpl.getWidth());
        map.put("height", activityImpl.getHeight());
        return map;
	 				
     }


    @Override
    public List<FlowVO> queryFlowByExeId(String businessId,String businessType){
	String executionId = getExeIdByBusinessAndType( businessId,businessType);
	if(executionId!=null&&!"".equals(executionId)){
	    return queryFlowByExeId(executionId);
	}
	return null;
    }
     

    @Override
    public List<FlowVO> queryFlowByExeId(String executionId) {
	
	Task t=taskService.createTaskQuery().executionId(executionId).singleResult();
	
	List<FlowVO> flowList =  taskFlowDao.queryFlowByProId(t.getProcessInstanceId());
	
	for(FlowVO vo : flowList){
	    
	    if(vo.getUserId()==null||"".equals(vo.getUserId())){
		vo.setUserId(vo.getAssignee());
	    }
	    if(vo.getAssignee()!=null){
		if(CodeConstant.FLOW_START.equals(vo.getAssignee())){
		    vo.setStatus(CodeConstant.START_STATUS);
		}else if(CodeConstant.AUDIT_RETURN.equals(vo.getAssignee())){
		    if(vo.getEndTime()==null){
			vo.setStatus(CodeConstant.ADJUST_STATUS);
		    }else{
			vo.setStatus(CodeConstant.START_STATUS);
		    }
		}else{
		    if(vo.getMessage()!=null){
			
			String[] msg = vo.getMessage().split(CodeConstant.SPLIT);
			String message="";
			
			vo.setStatus(msg[0]);
			
			
			for(int i=1;i<msg.length;i++){
			    message+=msg[i];
			}
			vo.setMessage(message);
			
		    }else{
		         vo.setStatus(CodeConstant.AUDIT_STATUS);
		    }
		}
	    }else{
		vo.setStatus(CodeConstant.UNDEFIEND_STATUS);
	    }
	    
	}
	
	return flowList;
    }
    
    
    @Override
    public List<TaskVO> queryFinishTask(String procDefId,String userId){
	
	List<TaskVO> taskList=taskFlowDao.queryFinishTask(procDefId,userId);
	for(TaskVO vo: taskList){
	    String[] msg = vo.getMessage().split(CodeConstant.SPLIT);
	        
	    vo.setStatus(msg[0]);
	    
	    String message="";
	    for(int i=1;i<msg.length;i++){
	        message+=msg[i];
	    }
	    vo.setMessage(message);	   
	    vo.setBusinessDetailURL((String)runtimeService.getVariable(vo.getExecutionId(), CodeConstant.BUSINESS_URL));
	    vo.setBusinessDetailParam((String)runtimeService.getVariable(vo.getExecutionId(), CodeConstant.BUSINESS_PARAM));
	}	
	return taskList;
    }

    @Override
    public List<FlowTypeVO> queryFinishTaskType(String userId){
	return taskFlowDao.queryFinishTaskType(userId);
    }
    

    @Override
    public String getExeIdByBusinessAndType(String businessId,
	    String businessType) {
	
	return taskFlowDao.getExeIdByBusinessAndType(businessId,businessType);
    }


    @Override
    public String getBusinessIdByExeId(String executionId) {
	
	return taskFlowDao.getBusinessIdByExeId(executionId);
    }


    @Override
    public void insertActToBusiness(String businessId, String businessType,
	    String executionId) {
	
	taskFlowDao.insertActToBusiness(businessId,businessType,executionId);
    }


    @Override
    public void deleteByBusiness(String businessId, String businessType) {
	
	taskFlowDao.deleteByBusiness(businessId, businessType);
    }


    @Override
    public void deleteByExeId(String executionId) {
	
	taskFlowDao.deleteByExeId(executionId);
    }


   @Override
   public List<SelectDataVO> getFlowLineData(String taskId){
       
       Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
       
       List<PvmTransition> listPt=lineDisposeService.queryNatureByTask(task);
       
       List<SelectDataVO> listSelectData = new ArrayList<SelectDataVO>();
       Boolean flag = true;  // 重新设计
       Map<String,String> attribute = new HashMap<String, String>();
       for(PvmTransition p : listPt){
	   SelectDataVO vo  = new  SelectDataVO();
	  
	   String flowCondition = p.getProperty(CodeConstant.CONDITION_TEXT).toString(); 
	   
	   if(flowCondition==null){
	       logger.info("流程线上表达式填写格式错误!");
	       return null;
	   }
	   
	   if(flowCondition.indexOf("==")!=-1){
	       String data = flowCondition.split("==")[1].replace("}", "").replaceAll("\"", ""); 
	       if(p.getProperty("name")!=null){
		   vo.setText(p.getProperty("name").toString());
		   vo.setId(data);
		   attribute.put("audtiStatus", p.getProperty("documentation").toString());
		   attribute.put("flowId", p.getId());  
		   vo.setAttribute(attribute);
		   listSelectData.add(vo);
	       }	       
	   }else{	 
	       /* 重新设计  start*/
	       if(flag){
		   if(p.getProperty("name")!=null){
		       vo.setText(p.getProperty("name").toString()); 		  
		       vo.setId(CodeConstant.CONDITION_PARAM);
		       attribute.put("audtiStatus", p.getProperty("documentation").toString());
	               attribute.put("flowId", p.getId()); 
		       vo.setAttribute(attribute);
		       listSelectData.add(vo); 
		       flag=false; 
		   }	           
	       } 
	       /* 重新设计  end*/
	   }  
       } 
        return listSelectData;
   }
   
   
   public List<String> checkAuditType(String taskId){
       Task t = taskService.createTaskQuery()
	       .taskId(taskId)
               .singleResult();
       if(t.getDescription()!=null&&!t.getDescription().equals("")){
	     
	   String roleIds=t.getDescription();
	   
	   String[] roleId = null;
	    
	   if(roleIds!=null){
	       roleId=roleIds.split(",");		
	   }     
	   Map<String, Object> map = runtimeService.getVariables(t.getExecutionId()); 
	   //查询审核人
	   List<String> userList = auditUserService.findFlowUsersByAssignee(roleId,map);
	   return userList;
       }else{
	   return null;
       }
   }
 
   @Override
   public InputStream viewFlowImage(String  taskId){
       
       //任务节点
       Task task = taskService.createTaskQuery()
		.taskId(taskId)//使用任务ID查询
		.singleResult();
       return  getChartIO(task);
   }
   
   public InputStream viewFlowImageBusiness(String  eId){
       
       //任务节点
       Task task = taskService.createTaskQuery()
		.executionId(eId)//使用实例ID查询
		.singleResult();
       return  getChartIO(task);
   }
   
   public InputStream getChartIO(Task task){
    // 获取流程定义
       ProcessDefinitionEntity processDefinitionEntity = 
	       (ProcessDefinitionEntity)repositoryService
	       .getProcessDefinition(task.getProcessDefinitionId());
    
       // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
       List<HistoricActivityInstance> historicActivityInstanceList = historyService
           .createHistoricActivityInstanceQuery()
           .processInstanceId(task.getProcessInstanceId())
           .orderByHistoricActivityInstanceId()
           .asc()
           .list();

       // 已执行的节点ID集合
       List<String> executedActivityIdList = new ArrayList<String>();
       int index = 1;
       logger.info("获取已经执行的节点ID");
       for (HistoricActivityInstance activityInstance : historicActivityInstanceList)
       {
           executedActivityIdList.add(activityInstance.getActivityId());
           logger.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : "
               + activityInstance.getActivityName());
           index++;
       }

       
       InputStream imageStream =
	           ProcessDiagramGenerator.generateDiagram(
	        	   processDefinitionEntity, "png", 
	        	   executedActivityIdList);
       return imageStream;
   }
   
   
} 
