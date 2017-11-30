package com.wisdom.common.activiti.model.vo;


/**
 * 
 * @author shangl
 * @description  流程类型
 * @date   2017/8/9
 *
 */
@SuppressWarnings("unused")
public class FlowTypeVO {
    
    private static final long serialVersionUID = 7863017440773004716L;
     
    private String count;       //数量
    private String processKey;  //流程key
    private String processName; //流程名
    
    
    
    public String getCount() {
        return count;
    }
    public void setCount(String count) {
        this.count = count;
    }
    public String getProcessKey() {
        return processKey;
    }
    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }
    public String getProcessName() {
        return processName;
    }
    public void setProcessName(String processName) {
        this.processName = processName;
    }
    
    
    
}
