package com.wisdom.common.activiti.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstance;

import com.wisdom.common.activiti.model.vo.FlowChartVO;
import com.wisdom.common.activiti.model.vo.FlowTypeVO;
import com.wisdom.common.activiti.model.vo.FlowVO;
import com.wisdom.common.activiti.model.vo.SelectDataVO;
import com.wisdom.common.activiti.model.vo.SubmitFlowConditionVO;
import com.wisdom.common.activiti.model.vo.TaskVO;


/**
 * 
 * @author shangl
 * @description  工作流接口
 * @date   2017/8/9
 *
 */
public interface TaskFlowService {
  
    /**
     * 编制状态数据提交方法
     * @param vo            启动流程需要的一些参数
     * @param variables     存入参数集合(数据权限标识,查询审核人需要参数)
     * @param flowCondition 判断条件(可以为空)
     * @param submitUser    提交人
     * @return P(流程结束),R(流程退回),A(流程继续,进入下 一 审批节点),null(流程不存在) 
     */
    String submit(SubmitFlowConditionVO vo,Map<String, Object> variables,
                  String flowCondition,String submitUser);
    
    /**
     * 退回状态数据提交方法
     * @param businessId        业务Id
     * @param businessType      业务数据类型判断字段
     * @param variables         存入参数集合(数据权限标识,查询审核人需要参数)
     * @param flowCondition     判断条件(可以为空)
     * @param submitUser        提交人
     * @return  P(流程结束),R(流程退回),A(流程继续,进入下 一 审批节点),null(流程不存在) 
     */
    String reSubmit(String businessId,String businessType, Map<String, Object> variables,
                    String flowCondition,String submitUser);
    

    /**
     * 检查流程类型,符合要求返回角色下所有人
     * @param taskId
     * @return
     */
    List<String> checkAuditType(String taskId);
    
    /**
     * 审批操作
     * @param taskId  节点ID
     * @param userId  当前审核人
     * @param message 审核信息
     * @param flowCondition 判断条件(可以为空)
     * @Param auditUser 下一步审核人
     * @Param status   流程审核状态
     * @return P(流程结束),R(流程退回),A(流程继续,进入下 一 审批节点),null(流程不存在)
     */
    String auditFlow(String taskId,String userId,
   	     String message,String flowCondition,
   	     String auditUser,String status);

    
    
    /**
     * 分类查询统计审核人当前的任务
     * @param userName
     * @return 类别和数量
     */
    List<FlowTypeVO> queryFlowType(String userId);
    
    /**
     * 根据审核人和流程类型查询审核任务
     * @param procDefId
     * @param auditUser
     * @return
     */
    List<TaskVO> queryFlowData(String procDefId,String auditUser);
    
    
    /**
     * 查询某个流程的审核过程信息
     * @param processInstanceId
     * @return
     */
    List<FlowVO> queryFlowByExeId(String executionId);
    
    /**
     * 业务界面操作查询历史审批情况的方法
     * @param businessId 
     * @param businessType
     * @return
     */
    List<FlowVO> queryFlowByExeId(String businessId,String businessType);
    
    /**
     * 审核人已办结任务查询
     * @param userName
     * @return
     */
    List<TaskVO> queryFinishTask(String procDefId, String userId);
    
    /**
     * 审核人已办结任务分类
     * @param userId
     * @return
     */
    List<FlowTypeVO> queryFinishTaskType(String userId);
    
    
    /**
     * 获得流程图信息
     * @param taskId
     * @return (图片名,部署 ID,活动节点位置)
     */
    FlowChartVO viewFlowChart(String taskId);
    
    
    /**
     * 查询流程图流
     * @param deploymentId
     * @param imageName
     * @return
     */
    InputStream findImageInputStream(String deploymentId,
		String imageName);
    

    
    /**
     * 根据key到的存入的value值
     * @param taskId 
     * @param key 
     * @return value
     */
    String getVariables(String taskId,String key);
    
    
   /*************************************内部使用方法*********************************************/
    
    
    /**
    * 开启新的流程 (提交时调用方法)
    * @param processDefinitionKey(流程keyId)
    * @param variables (存入参数)
    * @return 流程Id
    */
    String  startFlow(String processDefinitionKey,
	              Map<String, Object> variables);
    
    /**
     * 根据流程Id查询下一步审核人并存入 流程表中
     * @param processInstanceId  流程Id
     * @return 将审核人存入Assignee 返回结果信息:P(流程结束),R(流程退回),A(流程继续,进入下 一 审批节点)
     */
    String findNextAssignee(String processInstanceId);
  
    
    
    /**
     * 查询某一流程全部审核信息
     * @param processInstanceId 流程ID
     * @return 
     */
    List<HistoricActivityInstance> queryHistoricActivityInstanceTest(String processInstanceId);
    
    
    
    //操作中间表
    /**
     * 新增中间表数据将工作流和业务数据关联
     * @param businessId
     * @param businessType
     * @param executionId
     */
    void insertActToBusiness(String  businessId,String businessType,String executionId);
    
    /**
     * 删除中间表信息
     * @param businessId    业务Id
     * @param businessType  业务类型
     */
    void deleteByBusiness(String  businessId,String businessType); 
    
    
    /**
     * @param executionId   实例Id
     */
    void deleteByExeId(String executionId);
    
    
    /**
     * 通过业务信息查询工作流实例Id
     * @param businessId    业务Id
     * @param businessType  业务类型
     * @return
     */
    String getExeIdByBusinessAndType(String  businessId,String businessType);
    
    /**
     * 通过实例Id获得业务Id
     * @param executionId
     * @return
     */
    String getBusinessIdByExeId(String executionId);
    
    /**
     * 获得流程节点出口线的属性
     * @param taskId
     * @return
     */
    List<SelectDataVO> getFlowLineData(String taskId);
   
    /**
     * 通过任务Id获得流程图
     * @param taskId
     * @return
     */
    InputStream viewFlowImage(String  taskId);
    
    /**
     * 业务界面查询流程图
     * @param eId
     * @return
     */
    InputStream viewFlowImageBusiness(String  eId);
}
