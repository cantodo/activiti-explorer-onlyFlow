package com.wisdom.common.activiti.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.wisdom.common.activiti.service.LineDisposeService;

/**
 * 
 * @author kongxiangjun
 *
 */
@Service("lineDisposeService")
public class LineDisposeServiceImpl implements LineDisposeService
{
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    

    @Override
    public List<PvmTransition> queryNatureByTaskId(String taskId)
    {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return queryNatureByTask(task);
    }

    @Override
    public List<PvmTransition> queryNatureByTask(Task task)
    {
        ProcessDefinitionEntity prode = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
            .getDeployedProcessDefinition(task.getProcessDefinitionId());
        ExecutionEntity exe = (ExecutionEntity) runtimeService.createExecutionQuery()
            .executionId(task.getExecutionId())
            .singleResult();
        ActivityImpl act = prode.findActivity(exe.getActivityId());
        List<PvmTransition> pvmTransitions = act.getOutgoingTransitions();
        return pvmTransitions;
    }
    
    @Override
    public PvmTransition queryInNatureByTask(Task task, String proInsId)
    {
        ProcessDefinitionEntity prode = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
            .getDeployedProcessDefinition(task.getProcessDefinitionId());
        ExecutionEntity exe = (ExecutionEntity) runtimeService.createExecutionQuery()
            .executionId(task.getExecutionId())
            .singleResult();
        ActivityImpl act = prode.findActivity(exe.getActivityId());
        List<PvmTransition> incomingTransitions = act.getIncomingTransitions();

        if (CollectionUtils.isEmpty(incomingTransitions))
        {
            return null;
        }
        else if (incomingTransitions.size() == 1)
        {
            return incomingTransitions.get(0);
        }
        else
        {
            // 获取执行过的节点Id,按顺序排列
            List<String> executedActivityIdList = genActImlIds(proInsId);

            int execu = -1;
            PvmTransition nearPvm = null;
            for (PvmTransition pvmTransition : incomingTransitions)
            {
                TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
                ActivityImpl activityImpl = transitionImpl.getSource();
                String actImlId = activityImpl.getId();
                if (-1 != executedActivityIdList.indexOf(actImlId))
                {
                    int s = executedActivityIdList.indexOf(actImlId);
                    if (execu < s)
                    {
                        execu = s;
                        nearPvm = pvmTransition;
                    }
                }
            }
            return nearPvm;
        }

    }

    
    private List<String> genActImlIds(String proInsId)
    {
        List<HistoricActivityInstance> historicActivityInstanceList =
            historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(proInsId)
                .orderByHistoricActivityInstanceId()
                .asc()
                .list();
        List<String> executedActivityIdList = new ArrayList<String>();
        for (HistoricActivityInstance activityInstance : historicActivityInstanceList)
        {
            executedActivityIdList.add(activityInstance.getActivityId());
        }
        return executedActivityIdList;
    }

}
