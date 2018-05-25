package com.linklaws.cloudoa.comm.datapremission;

import java.lang.annotation.*;

/**
 * 数据访问权限注解，被注解的方法会被校验数据权限
 * <br/> 应该将role检查，简单的配置在前，复杂的配置在后，这样便于提高效率。
 * @author Min.Xu
 * @date 2018-05-21 13:49
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DataPremission {

    /**
     * 用户id的SpringEl表达式
     * @return
     */
    String userIdSpEl();

    /**
     * 数据id的SpringEl表达式
     * @return
     */
    String dataIdSpEl();

    /**
     * 数据id对应的数据类型
     * @return
     */
    DataType dataType();

    /**
     * 用户需要进行该操作需要通过的校验器数组。
     * <br/>做校验时，只要任何一个校验器校验通过，即通过；当遍历了所有校验器校验后还没有通过，则说明无权访问，则拦截！
     * @return
     */
    Class<? extends DataPremissionChecker>[] checkers();

    /**
     * 校验失败，无权访问的提示信息
     * @return
     */
    String message() default  "您无权对该数据进行该操作";

}
