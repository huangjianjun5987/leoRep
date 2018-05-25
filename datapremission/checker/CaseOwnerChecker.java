package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.auth.enums.CaseRole;
import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.CaseJoinUserMapper;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskMapper;
import com.linklaws.cloudoa.lawcase.model.CaseJoinUser;
import com.linklaws.cloudoa.lawcase.model.CaseTask;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 案件负责人校验器
 *
 * @author Min.Xu
 * @date 2018-05-21 16:37
 **/
@Component
public class CaseOwnerChecker implements DataPremissionChecker {

    @Autowired
    private CaseJoinUserMapper caseJoinUserMapper;

    @Autowired
    private CaseTaskMapper caseTaskMapper;


    private boolean isCaseLeader(Integer caseId, Integer userId) {
        CaseJoinUser query = new CaseJoinUser();
        query.setState(State.enable);
        query.setUserId(userId);
        query.setCaseId(caseId);
        query.setRole(CaseRole.leader);
        int count = caseJoinUserMapper.selectCount(query);
        return count > 0;
    }

    @Override
    public boolean check(DataType dataType, Object dataId, Integer userId) {
        if (DataType.TASK.equals(dataType)) {
            Integer taskId = (Integer) dataId;
            CaseTask caseTask = caseTaskMapper.selectByPrimaryKey(taskId);
            if (caseTask == null) {
                return false;
            }
            dataType = DataType.CASE;
            dataId = caseTask.getCaseId();
        }
        if (DataType.CASE.equals(dataType)) {
            Integer caseId = (Integer) dataId;
            return isCaseLeader(caseId, userId);
        }
        return false;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.CASE.equals(dataType) || DataType.TASK.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.CASE;
    }
}
