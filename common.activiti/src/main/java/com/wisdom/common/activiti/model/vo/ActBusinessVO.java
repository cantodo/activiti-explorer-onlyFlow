package com.wisdom.common.activiti.model.vo;

/**
 * 
 * @author shangl
 * @description  流程业务中间表
 * @date   2017/8/14
 *
 */
@SuppressWarnings("unused")
public class ActBusinessVO {
    
    private static final long serialVersionUID = 1L;
    
    private String  wsdId;
    
    private String  executionId;
    
    private String  businessId;
    
    private String  businessType;

    public String getWsdId() {
        return wsdId;
    }

    public void setWsdId(String wsdId) {
        this.wsdId = wsdId;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
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
