package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.enums.CaseStatus;
import com.linklaws.cloudoa.lawcase.dao.*;
import com.linklaws.cloudoa.lawcase.model.*;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 已归档的项目，公开性指向的团队下的用户校验器
 *
 * @author Min.Xu
 * @date 2018-05-24 08:58
 **/
@Component
public class ArchivedCaseOpenToTeamUserChecker implements DataPremissionChecker {

    @Autowired
    private CaseArchiveAuthMapper caseArchiveAuthMapper;
    
    @Autowired
    private CaseBaseMapper caseBaseMapper;

    @Autowired
    private CaseFlowMapper caseFlowMapper;

    @Autowired
    private CaseTaskMapper caseTaskMapper;

    @Autowired
    CaseTaskUploadRecordMapper caseTaskUploadRecordMapper;

    @Autowired
    private TeamMemberChecker teamMemberChecker;

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
//        if (DataType.TASK.equals(dataType)) {
//            Integer taskId = (Integer) dataId;
//            CaseTask caseTask = caseTaskMapper.selectByPrimaryKey(taskId);
//            if (caseTask == null) {
//                return false;
//            }
//            dataType = DataType.CASE;
//            dataId = caseTask.getCaseId();
//        }
//        if (DataType.FLOW.equals(dataType)) {
//            Integer flowId = (Integer) dataId;
//            CaseFlow caseFlow = caseFlowMapper.selectByPrimaryKey(flowId);
//            if (caseFlow == null) {
//                return false;
//            }
//            dataType = DataType.CASE;
//            dataId = caseFlow.getCaseId();
//        }
        if(DataType.CASE.equals(dataType)){
            Integer caseId = (Integer) dataId;
            CaseBase cbQuery = new CaseBase();
            cbQuery.setState(State.enable);
            cbQuery.setStatus(CaseStatus.filing);
            cbQuery.setId(caseId);
            int archivedCaseCount = this.caseBaseMapper.selectCount(cbQuery);
            if(archivedCaseCount >0){
                CaseArchiveAuth query = new CaseArchiveAuth();
                query.setCaseId(caseId);
                List<CaseArchiveAuth> result = caseArchiveAuthMapper.select(query);
                for(CaseArchiveAuth caa: result){
                    boolean isTeamMember = teamMemberChecker.check(DataType.TEAM, caa.getTeamId(), userId);
                    if (isTeamMember) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.CASE.equals(dataType)|| DataType.FLOW.equals(dataType) || DataType.TASK.equals(dataType)
                || DataType.TASK_FILE.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.CASE;
    }
}
