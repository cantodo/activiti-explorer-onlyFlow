package com.wisdom.common.activiti.model.vo;

import java.util.Date;


/**
 * @author shangl
 * @description  流程任务信息
 * @date   2017/8/16
 */
public class TaskVO {
    
    private String taskCode;   //节点编码
    
    private String taskId;     //节点Id
     
    private String taskName;   //节点名字
    
    private String executionId;//实例Id

    private String assignee;   //审核人信息
    
    private String processDefinitionId; //流程定义ID
    
    private String processInstanceId; //流程Id
    
    private Date   createDate; //创建时间
    
    private String createUser; //创建人  
    
    private String businessDetailURL;    //业务数据详请业务url
    
    private String businessDetailParam;  //业务数据详情页面查询参数集合;
    
    
    private String endTime;    //结束时间
    
    private String message;    //处理意见
    
    private String status;     //操作结果

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getBusinessDetailURL() {
	return businessDetailURL;
    }

    public void setBusinessDetailURL(String businessDetailURL) {
	this.businessDetailURL = businessDetailURL;
    }

    public String getBusinessDetailParam() {
	return businessDetailParam;
    }

    public void setBusinessDetailParam(String businessDetailParam) {
	this.businessDetailParam = businessDetailParam;
    }
    
    
    
    
    
    
    

}
