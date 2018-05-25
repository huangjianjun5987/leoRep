package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawfirm.dao.LawFirmMemberMapper;
import com.linklaws.cloudoa.lawfirm.enums.FirmMemberRole;
import com.linklaws.cloudoa.lawfirm.model.LawFirmMember;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 律所中心总监校验器
 *
 * @author Min.Xu
 * @date 2018-05-21 19:17
 **/
@Component
public class CenterLeaderChecker implements DataPremissionChecker {

    @Autowired
    private LawFirmMemberMapper lawFirmMemberMapper;

    @Override
    public boolean check(DataType dataType, Object dataId, Integer userId) {
        LawFirmMember query = new LawFirmMember();
        query.setState(State.enable);
        query.setUserId(userId);
        query.setCenterId((Integer) dataId);
        query.setRole(FirmMemberRole.center_director);
        int count = lawFirmMemberMapper.selectCount(query);
        return count > 0;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.CENTER.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.CENTER;
    }
}
