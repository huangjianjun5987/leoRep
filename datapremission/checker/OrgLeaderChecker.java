package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawfirm.dao.LawFirmOrgMapper;
import com.linklaws.cloudoa.lawfirm.model.LawFirmOrg;
import com.linklaws.cloudoa.team.dao.TeamMapper;
import com.linklaws.cloudoa.team.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 组织(团队、部门、中心、律所的负责人)校验器，只接收团队或部门id
 * <br/> DataType == TEAM ,传入 团队或部门id
 *
 * @author Min.Xu
 * @date 2018-05-21 18:19
 **/
@Component
public class OrgLeaderChecker implements DataPremissionChecker {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private LawFirmOrgMapper lawFirmOrgMapper;

    @Autowired
    private TeamLeaderChecker teamLeaderChecker;

    @Autowired
    private DepartLeaderChecker departLeaderChecker;

    @Autowired
    private CenterLeaderChecker centerLeaderChecker;

    @Autowired
    private LawfirmLeaderChecker lawfirmLeaderChecker;

    @Override
    public boolean check(DataType dataType, Object dataId, Integer userId) {
        // 校验团队、部门负责人
        if (DataType.TEAM.equals(dataType)) {
            Integer teamId = (Integer) dataId;
            Team team = teamMapper.selectByPrimaryKey(teamId);
            if(team == null){
                return false;
            }
            Integer centerId  = team.getCenterId();
            if (centerId == null) {//centerId为null则为团队
                boolean isTeamLeader = teamLeaderChecker.check(DataType.TEAM, teamId, userId);
                if(isTeamLeader){
                    return true;
                }
            } else {//中心部位null则是律所的部门
                boolean isDepartLeader = departLeaderChecker.check(DataType.TEAM, teamId, userId);
                if (isDepartLeader) {
                    return true;
                }
                dataType = DataType.CENTER;
                dataId = centerId;
            }
        }

        //        校验中心负责人
        if (DataType.CENTER.equals(dataType)) {
            Integer centerId = (Integer) dataId;
            boolean isCenterLeader = centerLeaderChecker.check(DataType.CENTER, centerId, userId);
            if (isCenterLeader) {
                return true;
            }
            //获得律所id
            LawFirmOrg lawFirmCenterOrg = lawFirmOrgMapper.selectByPrimaryKey(centerId);
            if(lawFirmCenterOrg != null){
                dataType = DataType.LAWFIRM;
                dataId = lawFirmCenterOrg.getLawfirmId();
            }
        }

        //        校验律所负责人
        if (DataType.LAWFIRM.equals(dataType)) {
            Integer lawfirmId = (Integer) dataId;
            boolean isLawfirmLeader = lawfirmLeaderChecker.check(DataType.LAWFIRM, lawfirmId, userId);
            if (isLawfirmLeader) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.TEAM.equals(dataType) || DataType.CENTER.equals(dataType) || DataType.LAWFIRM.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.TEAM;
    }
}
