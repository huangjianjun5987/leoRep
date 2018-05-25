package com.linklaws.cloudoa.comm.datapremission;

/**
 * 数据校验的数据类型
 *
 * @author Min.Xu
 * @date 2018-05-21 16:26
 **/
public enum DataType {
    /**
     * 项目
     */
    CASE,
    /**
     * 任务文件
     */
    CASE_FILE,
    /**
     * 团队的项目归档标签
     */
    CASE_TEAMTAG,
    /**
     * 项目阶段
     */
    FLOW,
    /**
     * 任务
     */
    TASK,
    /**
     * 任务文件
     */
    TASK_FILE,
    /**
     * 团队或部门
     */
    TEAM,
    /**
     * 中心
     */
    CENTER,
    /**
     * 律所
     */
    LAWFIRM;
}
