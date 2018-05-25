package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.CaseBaseMapper;
import com.linklaws.cloudoa.lawcase.dao.CaseFlowMapper;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskMapper;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskUploadRecordMapper;
import com.linklaws.cloudoa.lawcase.model.CaseBase;
import com.linklaws.cloudoa.lawcase.model.CaseFlow;
import com.linklaws.cloudoa.lawcase.model.CaseTask;
import com.linklaws.cloudoa.lawcase.model.CaseTaskUploadRecord;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据资源的拥有者组织的校验器
 * <br/> 如果DataType == CASE ,则传入 案例id
 * <br/> 如果DataType == TASK ,则传入 taskId
 *
 * @author Min.Xu
 * @date 2018-05-22 09:19
 **/
@Component
public class ResOwnOrgLeaderChecker implements DataPremissionChecker {

    @Autowired
    private CaseBaseMapper caseBaseMapper;

    @Autowired
    private CaseTaskMapper caseTaskMapper;

    @Autowired
    private CaseFlowMapper caseFlowMapper;

    @Autowired
    CaseTaskUploadRecordMapper caseTaskUploadRecordMapper;

    @Autowired
    private OrgLeaderChecker orgLeaderChecker;

    /**
     * 校验项目拥有者权限
     *
     * @param caseId
     * @param userId
     * @return
     */
    private boolean checkCaseOwnOrgLeader(Integer caseId, Integer userId) {
        CaseBase dbCaseBase = caseBaseMapper.selectByPrimaryKey(caseId);
        if (dbCaseBase == null) {
            return false;
        }
        Integer teamId = dbCaseBase.getTeamId();
        return orgLeaderChecker.check(DataType.TEAM, teamId, userId);
    }

//    /**
//     * 校验任务的拥有者权限
//     *
//     * @param taskId
//     * @param userId
//     * @return
//     */
//    private Boolean checkTaskOwnOrgLeader(Integer taskId, Integer userId) {
//        CaseTask caseTask = caseTaskMapper.selectByPrimaryKey(taskId);
//        if (caseTask == null) {
//            return false;
//        }
//        Integer caseId = caseTask.getCaseId();
//        if (caseId == null) {
//            return false;
//        }
//        return checkCaseOwnOrgLeader(caseId, userId);
//    }
//
//    /**
//     * 检查是否为流程阶段的所属案件的上级领导
//     *
//     * @param flowId
//     * @param userId
//     * @return
//     */
//    private boolean checkFlowOwnOrgLeader(Integer flowId, Integer userId) {
//        CaseFlow caseFlow = caseFlowMapper.selectByPrimaryKey(flowId);
//        if (caseFlow == null) {
//            return false;
//        }
//        return checkCaseOwnOrgLeader(caseFlow.getCaseId(), userId);
//    }
//
//    private CaseTaskUploadRecord getTaskByFileId(Integer dataId) {
//        Integer fileId = dataId;
//        CaseTaskUploadRecord query = new CaseTaskUploadRecord();
//        query.setState(State.enable);
//        query.setId(fileId);
//        return caseTaskUploadRecordMapper.selectOne(query);
//    }

    @Override
    public boolean check(DataType dataType, Object dataId, Integer userId) {
//        if (DataType.TASK_FILE.equals(dataType)) {
//            Integer taskFileId = (Integer) dataId;
//            CaseTaskUploadRecord query = new CaseTaskUploadRecord();
//            query.setState(State.enable);
//            query.setId(taskFileId);
//            CaseTaskUploadRecord caseTaskUploadRecord =  caseTaskUploadRecordMapper.selectOne(query);
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
//            if(caseTask==null){
//                return false;
//            }
//            dataType = DataType.CASE;
//            dataId = caseTask.getCaseId();
//        }
//
//        if (DataType.FLOW.equals(dataType)) {
//            Integer flowId = (Integer) dataId;
//            CaseFlow caseFlow = caseFlowMapper.selectByPrimaryKey(flowId);
//            if(caseFlow==null){
//                return false;
//            }
//            dataType = DataType.CASE;
//            dataId = caseFlow.getCaseId();
//        }
        if (DataType.CASE.equals(dataType)) {
            Integer caseId = (Integer) dataId;
            return checkCaseOwnOrgLeader(caseId, userId);
        }
        return false;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.CASE.equals(dataType) || DataType.TASK.equals(dataType) || DataType.FLOW.equals(dataType)
                || DataType.TASK_FILE.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.CASE;
    }
}
