package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskMapper;
import com.linklaws.cloudoa.lawcase.model.CaseTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务创建人校验器
 *
 * @author Min.Xu
 * @date 2018-05-23 10:16
 **/
@Component
public class TaskCreatorChecker implements DataPremissionChecker {

    @Autowired
    private CaseTaskMapper caseTaskMapper;

    private boolean isTaskCreator(Integer userId, Integer taskId) {
        CaseTask caseTask = caseTaskMapper.selectByPrimaryKey(taskId);
        if (caseTask != null) {
            return userId.intValue() == caseTask.getCreateBy().intValue();
        }
        return false;
    }

    @Override
    public boolean check(DataType dataType, Object dataId, Integer userId) {
        if (DataType.TASK.equals(dataType)) {
            Integer taskId = (Integer) dataId;
            CaseTask caseTask = caseTaskMapper.selectByPrimaryKey(taskId);
            if (caseTask != null) {
                return userId.intValue() == caseTask.getCreateBy().intValue();
            }
        }
        return false;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.TASK.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.TASK;
    }
}
