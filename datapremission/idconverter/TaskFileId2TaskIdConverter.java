package com.linklaws.cloudoa.comm.datapremission.idconverter;

import com.linklaws.cloudoa.comm.datapremission.DataIdConverter;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.CaseTaskUploadRecordMapper;
import com.linklaws.cloudoa.lawcase.model.CaseTaskUploadRecord;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 任务文件id转任务id
 *
 * @author Min.Xu
 * @date 2018-05-24 14:42
 **/
@Component
public class TaskFileId2TaskIdConverter implements DataIdConverter {

    @Autowired
    CaseTaskUploadRecordMapper caseTaskUploadRecordMapper;

    @Override
    public Object convert(Object dataId) {
        Integer fileId = (Integer) dataId;
        CaseTaskUploadRecord query = new CaseTaskUploadRecord();
        query.setState(State.enable);
        query.setId(fileId);
        CaseTaskUploadRecord caseTaskUploadRecord = caseTaskUploadRecordMapper.selectOne(query);
        return caseTaskUploadRecord == null ? null : caseTaskUploadRecord.getTaskId();
    }

    @Override
    public DataType formType() {
        return DataType.TASK_FILE;
    }

    @Override
    public DataType toType() {
        return DataType.TASK;
    }
}
