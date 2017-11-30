package com.wisdom.common.activiti.service;

import java.util.List;
import java.util.Map;

import com.wisdom.common.activiti.model.vo.FlowVO;

/**
 * 
 * @author shangl
 * @description  由其他模块实现接口方法,方便调用
 * @date   2017/8/9
 *
 */
public interface AuditUserService<T> {
    
    /**
     * 通过角色ID和机构查询审核人
     * @param roleIds 角色ID 来源工作流配置assignee
     * @param map     权限查询条件
     * @return        人员List
     */
    List<String> findFlowUsersByAssignee(String[] roleIds,Map<String, Object>  map);
    
    /**
     * 查询某个流程的审核过程信息
     * @param vo (流程信息)
     * @return
     */
    T  queryFlow(FlowVO vo);
    
}
