package com.linklaws.cloudoa.comm.datapremission.idconverter;

import com.linklaws.cloudoa.comm.datapremission.DataIdConverter;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskMapper;
import com.linklaws.cloudoa.lawcase.model.CaseTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 根据taskid获得caseid转换器
 *
 * @author Min.Xu
 * @date 2018-05-23 16:14
 **/
@Component
public class TaskId2CaseIdConverter implements DataIdConverter {

    @Autowired
    private CaseTaskMapper caseTaskMapper;

    @Override
    public DataType formType() {
        return DataType.TASK;
    }

    @Override
    public DataType toType() {
        return DataType.CASE;
    }

    @Override
    public Object convert(Object dataId) {
        Integer taskId = (Integer) dataId;
        CaseTask caseTask = caseTaskMapper.selectByPrimaryKey(taskId);
        return caseTask == null ? null : caseTask.getCaseId();
    }

}
