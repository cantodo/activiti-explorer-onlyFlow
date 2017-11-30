package com.wisdom.common.activiti.service;

import java.util.List;

import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.task.Task;

/**
 * 
 * @author kongxiangjun
 * @param <K>
 * @param <V>
 *
 */
public interface LineDisposeService
{
    /**
     * 
     * @param proInsId 流程实例ID
     * @param key 要获取的属性，可选值：name(名称),conditionText(表达式)等.例如：pvmTransition.getProperty(key)
     * @return
     */
    public List<PvmTransition> queryNatureByTaskId(String taskId);

    /**
     * 
     * @param proInsId 流程任务
     * @param key 要获取的属性，可选值：name(名称),conditionText(表达式)等.例如：pvmTransition.getProperty(key)
     * @return
     */
    public List<PvmTransition> queryNatureByTask(Task task);
    
    
    /**
     * 
     * @param proInsId 流程ID
     * @param task 任务
     * 
     * @return 唯一入线
     */
    public PvmTransition queryInNatureByTask(Task task, String proInsId);
}
