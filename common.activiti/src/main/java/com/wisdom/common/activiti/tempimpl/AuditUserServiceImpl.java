package com.wisdom.common.activiti.tempimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.wisdom.common.activiti.CodeConstant;
import com.wisdom.common.activiti.model.vo.FlowVO;
import com.wisdom.common.activiti.service.AuditUserService;

/**
 * @author shangl
 * @description 虚拟实现类,方便单元测试;
 * @date 2017/8/10
 *
 */
@SuppressWarnings("unused")
@Service("auditUserServiceTemp")
public class AuditUserServiceImpl implements AuditUserService<FlowVO>
{

    @Override
    public List<String> findFlowUsersByAssignee(String[] roleIds, Map<String, Object> map)
    {

        List<String> l = new ArrayList<String>();
        //l.add(CodeConstant.TEMP_TEST);
        l.add("-999");
        return l;
    }

    @Override
    public FlowVO queryFlow(FlowVO vo)
    {

        return vo;
    }
}
