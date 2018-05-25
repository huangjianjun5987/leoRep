package com.linklaws.cloudoa.comm.datapremission;

import com.google.common.collect.ImmutableMap;
import com.linklaws.cloudoa.auth.enums.AuthExceptionEnum;
import com.linklaws.cloudoa.utils.GraphHelper;
import com.linklaws.cloudoa.utils.SpringContextUtil;
import com.linklaws.core.exception.GlobalException;
import com.linklaws.core.utils.Assert;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 数据权限控制AOP拦截器
 * <br/>为什么不放到core基础工程中去？因为DataType随着业务增加会不断增加，如果放到core工程中会导致经常需要修改DataType而重新deploy jar包！
 *
 * @author Min.Xu
 * @date 2018-05-21 13:48
 **/
@Aspect
@Component
@Lazy(false)
public class DataPremissionInterceptor implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(DataPremissionInterceptor.class);

    //id转换器map缓存
    private Map<String, DataIdConverter> converterMap = new HashMap<>();

    //dataType转换帮助类、可以通过它查找dataType的最短转换路径
    private GraphHelper<DataType> graphHelper;

    // 生成数据类型转换器缓存map的key
    private String generatorMapKey(DataType formType, DataType toType) {
        return formType.name() + "->" + toType.name();
    }

    //使用SPEL进行key的解析
    private ExpressionParser parser = new SpelExpressionParser();

    // 构造方法完成之后调用
    //    @PostConstruct
    //    public void init() {
    //        logger.info("init DataPrimissionInterceptor");
    //    }

    /**
     * 初始化，查找所有idConverter对象，并缓存到map，并处理某个DataType到其他DataType的最短转换路径
     *
     * @param
     * @return
     * @author Min.Xu
     * @date 2018/5/24
     */
    private void init() {
        Set<DataType> registerDataType = registerConverters();
        initGraphHelper(registerDataType);
    }

    /**
    *  注册所有id转换器
    * @param
    * @return
    * @author Min.Xu
    * @date 2018/5/24
    */
    private Set<DataType> registerConverters() {
        Map<String, DataIdConverter> beansOfType = SpringContextUtil.getApplicationContext()
                .getBeansOfType(DataIdConverter.class);
        logger.debug("开始注册的数据id转换器");
        Set<DataType> registerDataType = new HashSet<>();
        for (DataIdConverter converter : beansOfType.values()) {
            DataType formType = converter.formType();
            DataType toType = converter.toType();
            String mapKey = generatorMapKey(formType, toType);
            logger.debug("register converter {}:{}", mapKey, converter);
            converterMap.put(mapKey, converter);
            registerDataType.add(formType);
            registerDataType.add(toType);
        }
        return registerDataType;
    }

    /**
     * 初始化用于id多次转换的GraphHelper工具，用来查找最短转换路径
     * @param registerDataType
     */
    private void initGraphHelper(Set<DataType> registerDataType) {
        DataType[] dataTypes = new DataType[registerDataType.size()];
        registerDataType.toArray(dataTypes);
        int[][] weight = new int[dataTypes.length][dataTypes.length];
        for (int i = 0; i < dataTypes.length; i++) {
            DataType formType = dataTypes[i];
            for (int j = 0; j < dataTypes.length; j++) {
                DataType toType = dataTypes[j];
                String mapKey = generatorMapKey(formType, toType);
                if (i == j) {
                    weight[i][j] = 0;
                } else {
                    //1表示可达，GraphHelper.INF表示不可达，0表示自己
                    weight[i][j] = converterMap.containsKey(mapKey) ? 1 : GraphHelper.INF;
                }
            }
        }
        graphHelper = new GraphHelper(weight, dataTypes);
        ImmutableMap<String, ArrayList<DataType>> allShortestPath = graphHelper.getAllShortestPath();
        for (Map.Entry<String, ArrayList<DataType>> entry : allShortestPath.entrySet()) {
            if (!converterMap.containsKey(entry.getKey())) {
                logger.info("{} 可转换的最短路径:{}", entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     *  转换数据Id
     *  <br/>如果当前数据DataType与目标数据DataType一致，则不用转换；如果不一致，则现在转换器mapper中查找直接转换方式转换；如果找不到再寻找最短路径转换
     * @param
     * @return
     * @author Min.Xu
     * @date 2018/5/25
     */
    private Object convertDataId(DataType srcDataType, DataType targetDataType, Object dataId) {
        if(srcDataType == targetDataType){
            return dataId;
        }
        // 执行某一个checker时，dataId要用临时变量存起来，不能直接改变方法入参dataId的值，以免影响下一个Checker使用
        Object tempDataId = dataId;
        //        1.尝试直接转换
        String baseMapKey = generatorMapKey(srcDataType, targetDataType);
        DataIdConverter baseConverter = converterMap.get(baseMapKey);
        if (baseConverter != null) {
            Object newDataId = baseConverter.convert(tempDataId);
            logger.info("{} id {} convert to {} id {}", srcDataType, tempDataId, targetDataType, newDataId);
            tempDataId = newDataId;
            return tempDataId;
        }
        //        2.再尝试查找最短转换路径
        List<DataType> shortestPath = graphHelper.getShortestPath(srcDataType, targetDataType);
        if (CollectionUtils.isEmpty(shortestPath)) {
            logger.warn("Can not support srcDataType {},can not found any convert path.", srcDataType);
            return null;
            //            throw new IllegalArgumentException("checker can not support srcDataType "+srcDataType+" or found any convert path");
        }
        for (int i = 0; i < shortestPath.size() - 1; i++) {
            DataType formDataType = shortestPath.get(i);
            DataType toDataType = shortestPath.get(i + 1);
            String mapKey = generatorMapKey(formDataType, toDataType);
            DataIdConverter converter = converterMap.get(mapKey);
            Object newDataId = converter.convert(tempDataId);
            logger.info("{} id {} convert to {} id {}", formDataType, tempDataId, toDataType, newDataId);
            tempDataId = newDataId;
            // 当转换后获得的数据id为null时，就没必要走下去了，直接返回null
            if (tempDataId == null) {
                break;
            }
        }
        return tempDataId;
    }

    /**
     * 定义拦截规则：拦截有@DataPrimission注解的方法。
     */
    @Pointcut("@annotation(com.linklaws.cloudoa.comm.datapremission.DataPremission)")
    public void pointCut() {
    }

    /**
     * 拦截器具体实现
     *
     * @param point
     * @throws Throwable
     */
    @Around(value = "@annotation(dataPremissionAnnotation)")
    public Object dataPrimissionCheck(ProceedingJoinPoint point, DataPremission dataPremissionAnnotation)
            throws Throwable {
        logger.debug("start data primission check.");
        //注意：这里没法通过代理后的对象的method来获得方法上的注解DataPrimission
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Object[] args = point.getArgs();
        String userIdSpEl = dataPremissionAnnotation.userIdSpEl();
        String dataIdSpEl = dataPremissionAnnotation.dataIdSpEl();
        DataType dataType = dataPremissionAnnotation.dataType();
        Object userIdObj = parseKey(userIdSpEl, method, args);
        Object dataId = parseKey(dataIdSpEl, method, args);
        Class<? extends DataPremissionChecker>[] checkers = dataPremissionAnnotation.checkers();

        Assert.notNull(userIdObj, "数据权限校验器无法获取到操作用户id");
        Assert.notNull(dataId, "数据权限校验器无法获取到数据资源id");
        int userId = ((Integer) userIdObj).intValue();

        executeChecker(checkers, method, dataType, dataId, userId, dataPremissionAnnotation.message());
        logger.debug("data primission check pass.");

        return point.proceed();
    }
    /**
     * 执行校验器，没有通过任何一个执行器则抛异常
     *
     * @param checkers
     * @param method
     * @param srcDataType
     * @param dataId
     * @param userId
     */
    private void executeChecker(Class<? extends DataPremissionChecker>[] checkers, Method method, DataType srcDataType,
            Object dataId, int userId, String message) {
        boolean isPass = false;//标识是否通过校验
        if (checkers == null || checkers.length == 0) {
            logger.error("Method {}.{} DataPremission checkers is empty !", method.getClass(), method.getName());
        }
        Assert.notEmpty(checkers, "DataPremission 的checkers参数不能为空");

        for (Class<? extends DataPremissionChecker> checkerClass : checkers) {
            String checkerClassName = checkerClass.getSimpleName();
            Object checkerObj = SpringContextUtil.getBean(checkerClass);
            if (checkerObj == null) {
                logger.error("Checker of class {} is null", checkerClass);
                throw new IllegalArgumentException("Checker of class " + checkerClass + " is null");
            }
            DataPremissionChecker checker = (DataPremissionChecker) checkerObj;
            //如果当前数据类型不是checker直接支持的，则需要查找转换路径并转换
            DataType targetDataType = checker.getSupportDataType();
            // 转换数据id
            Object dataIdForCheck = convertDataId(srcDataType, targetDataType, dataId);
            if (dataIdForCheck == null) {
                logger.info("Skip check data premission, dataIdForCheck:{}",dataIdForCheck);
                continue;
            }
            //只要通过，就放行
            boolean checkResult = checker.doCheck(targetDataType, dataIdForCheck, userId);
            logger.debug("Checker:{},dataType:{},dataId:{},userId:{},result:{}", checkerClassName,targetDataType,dataIdForCheck,userId,checkResult);
            if (checkResult) {
                isPass = true;
                break;
            }
        }
        //检查校验器校验完后的结果
        if (!isPass) {
            logger.warn("userId:{},srcDataType:{},dataId:{} check data primission fail!", userId, srcDataType, dataId);
            throw new GlobalException(AuthExceptionEnum.OTHER, message);
        }
    }

    /**
     * 解析springEl表达式的值
     *
     * @param key
     * @param method
     * @param args
     * @return
     */
    private Object parseKey(String key, Method method, Object[] args) {
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        try {
            return parser.parseExpression(key).getValue(context);
        } catch (SpelEvaluationException e) {
            logger.error("parse key " + key + " fail on " + method.toString(), e);
        }
        return null;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }

}
