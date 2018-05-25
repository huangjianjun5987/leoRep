package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.CaseFlowMapper;
import com.linklaws.cloudoa.lawcase.dao.CaseJoinUserMapper;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskMapper;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskUploadRecordMapper;
import com.linklaws.cloudoa.lawcase.model.CaseFlow;
import com.linklaws.cloudoa.lawcase.model.CaseJoinUser;
import com.linklaws.cloudoa.lawcase.model.CaseTask;
import com.linklaws.cloudoa.lawcase.model.CaseTaskUploadRecord;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 案件参与人校验器
 *
 * @author Min.Xu
 * @date 2018-05-21 16:37
 **/
@Component
public class CaseJoinUserChecker implements DataPremissionChecker {

    @Autowired
    private CaseJoinUserMapper caseJoinUserMapper;

    @Autowired
    private CaseFlowMapper caseFlowMapper;

    @Autowired
    private CaseTaskMapper caseTaskMapper;

    @Autowired
    CaseTaskUploadRecordMapper caseTaskUploadRecordMapper;

    private boolean isCaseJoinUser(Integer caseId, Integer userId) {
        CaseJoinUser query = new CaseJoinUser();
        query.setState(State.enable);
        query.setUserId(userId);
        query.setCaseId(caseId);
        int count = caseJoinUserMapper.selectCount(query);
        return count > 0;
    }

    private CaseTaskUploadRecord getTaskByFileId(Integer dataId) {
        Integer fileId = dataId;
        CaseTaskUploadRecord query = new CaseTaskUploadRecord();
        query.setState(State.enable);
        query.setId(fileId);
        return caseTaskUploadRecordMapper.selectOne(query);
    }
//
//    private boolean isTaskCaseJoinUser(Integer taskId, Integer userId) {
//        CaseTask caseTask = caseTaskMapper.selectByPrimaryKey(taskId);
//        if (caseTask == null) {
//            return false;
//        }
//        return isCaseJoinUser(caseTask.getCaseId(), userId);
//    }

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
//
//        if (DataType.TASK.equals(dataType)) {
//            Integer taskId = (Integer) dataId;
//            CaseTask caseTask = caseTaskMapper.selectByPrimaryKey(taskId);
//            if (caseTask == null) {
//                return false;
//            }
//            dataType = DataType.CASE;
//            dataId = caseTask.getCaseId();
//        }
//
//        if (DataType.FLOW.equals(dataType)) {
//            Integer flowId = (Integer) dataId;
//            CaseFlow caseFlow = caseFlowMapper.selectByPrimaryKey(flowId);
//            if (caseFlow == null) {
//                return false;
//            }
//            dataType = DataType.CASE;
//            dataId = caseFlow.getCaseId();
//        }

        if (DataType.CASE.equals(dataType)) {
            Integer caseId = (Integer) dataId;
            return isCaseJoinUser(caseId, userId);
        }
        return false;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.CASE.equals(dataType) || DataType.FLOW.equals(dataType) || DataType.TASK.equals(dataType)
                || DataType.TASK_FILE.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.CASE ;
    }
}
