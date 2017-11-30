package com.wisdom.common.activiti.dao;

import java.util.List;

import com.wisdom.common.activiti.model.vo.FlowTypeVO;
import com.wisdom.common.activiti.model.vo.FlowVO;
import com.wisdom.common.activiti.model.vo.TaskVO;

/**
 * 
 * @author shangl
 * @description  jdbcTemplate  查询操作数据库
 * @date   2017/8/9
 *
 */

public interface TaskFlowDao {
    
    /**
     * 分类查询统计审核人当前的任务
     * @param userName
     * @return 类别和数量
     */
    List<FlowTypeVO> queryFlowType(String userId);
    
    /**
     * 查询某个流程的审核过程信息
     * @param processInstanceId
     * @return
     */
    List<FlowVO> queryFlowByProId(String processInstanceId);
    
    
    
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
    String getExeIdByBusinessAndType(String businessId, String businessType);
    
    
    /**
     * 通过实例Id获得业务Id
     * @param executionId
     * @return
     */
    String getBusinessIdByExeId(String executionId);
    
    
    
    /**
     * 审核人已办结任务查询
     * @param userName
     * @return
     */
    List<TaskVO> queryFinishTask(String procDefId,String userId);
    
    
    /**
     * 审核人已办结任务分类
     * @param userId
     * @return
     */
    List<FlowTypeVO> queryFinishTaskType(String userId);
}
