package com.wisdom.common.activiti.model.vo;

import java.util.Map;

/**
 * 
 * @author shangl
 * @description  提交流程使用参数类
 * @date   2017/8/9
 *
 */
public class SubmitFlowConditionVO {

    
    private String processDefinitionKey; //根据需要决定使用的流程;定义流程是的ID
    
    private String businessURL;          //业务数据详请业务url
     
    private Map<String, Object>    detailParamMap;  //业务数据详情页面查询参数集合;
    
    private String businessId;           //业务数据ID
    
    private String businessType;         //业务数据类型判断字段

    public String getProcessDefinitionKey() {
        return processDefinitionKey;
    }

    public void setProcessDefinitionKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
    }

    public String getBusinessURL() {
        return businessURL;
    }

    public void setBusinessURL(String businessURL) {
        this.businessURL = businessURL;
    }

   

    public Map<String, Object> getDetailParamMap() {
        return detailParamMap;
    }

    public void setDetailParamMap(Map<String, Object> detailParamMap) {
        this.detailParamMap = detailParamMap;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
    
    
    
    
    
    
    
}
