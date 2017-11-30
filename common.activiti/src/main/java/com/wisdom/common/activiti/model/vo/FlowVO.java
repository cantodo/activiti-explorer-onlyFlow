package com.wisdom.common.activiti.model.vo;

/**
 * 
 * @author shangl
 * @description  流程
 * @date   2017/8/9
 *
 */
@SuppressWarnings("unused")
public class FlowVO {
       
    	private static final long serialVersionUID = 7863017440773004716L;
        
    	private String executionId; //实例Id
    	
    	private String actName;//节点名
    	
    	private String userId; //最终操作人Id
    	
    	private String[] userIds; //操作人数组
    	
    	private String assignee;// 审核人
    	
    	private String endTime; //处理时间
    	
    	private String message;// 处理意见
    	
    	private String status; //操作类型

	public String getActName() {
	    return actName;
	}

	public void setActName(String actName) {
	    this.actName = actName;
	}

	public String getUserId() {
	    return userId;
	}

	public void setUserId(String userId) {
	    this.userId = userId;
	}

	public String getAssignee() {
	    return assignee;
	}

	public void setAssignee(String assignee) {
	    this.assignee = assignee;
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
	
	public String[] getUserIds() {
	    return userIds;
	}

	public void setUserIds(String[] userIds) {
	    this.userIds = userIds;
	}

	public String getExecutionId() {
	    return executionId;
	}

	public void setExecutionId(String executionId) {
	    this.executionId = executionId;
	}
    	
    	
    	
    	
}
