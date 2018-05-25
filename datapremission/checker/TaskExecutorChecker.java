package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.enums.TaskUserStatus;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskUploadRecordMapper;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskUserMapper;
import com.linklaws.cloudoa.lawcase.model.CaseTaskUploadRecord;
import com.linklaws.cloudoa.lawcase.model.CaseTaskUser;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务执行人校验器
 *
 * @author Min.Xu
 * @date 2018-05-21 17:37
 **/
@Component
public class TaskExecutorChecker implements DataPremissionChecker {

    @Autowired
    private CaseTaskUserMapper caseTaskUserMapper;

    @Autowired
    CaseTaskUploadRecordMapper caseTaskUploadRecordMapper;

//    private CaseTaskUploadRecord getTaskByFileId(Integer dataId) {
//        Integer fileId = dataId;
//        CaseTaskUploadRecord query = new CaseTaskUploadRecord();
//        query.setState(State.enable);
//        query.setId(fileId);
//        return caseTaskUploadRecordMapper.selectOne(query);
//    }

    private boolean isComfirmedTaskExecutor(Integer taskId, Integer userId) {
        CaseTaskUser query = new CaseTaskUser();
        query.setStatus(TaskUserStatus.confirmed);
        query.setUserId(userId);
        query.setTaskId(taskId);
        int count = caseTaskUserMapper.selectCount(query);
        return count > 0;
    }

    @Override
    public boolean check(DataType dataType, Object dataId, Integer userId) {
//        if (DataType.TASK_FILE.equals(dataType)) {
//            Integer fileId = (Integer) dataId;
//            CaseTaskUploadRecord query = new CaseTaskUploadRecord();
//            query.setState(State.enable);
//            query.setId(fileId);
//            CaseTaskUploadRecord caseTaskUploadRecord = caseTaskUploadRecordMapper.selectOne(query);
//            if (caseTaskUploadRecord == null) {
//                return false;
//            }
//            dataType = DataType.TASK;
//            dataId = caseTaskUploadRecord.getTaskId();
//        }
        if (DataType.TASK.equals(dataType)) {
            Integer taskId = (Integer) dataId;
            return isComfirmedTaskExecutor(taskId,userId);
        }
        return false;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.TASK.equals(dataType) || DataType.TASK_FILE.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.TASK;
    }
}
