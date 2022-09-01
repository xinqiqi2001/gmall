package com.atguigu.gmall.item.cache.aspect;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.atguigu.gmall.item.cache.annotation.GmallCache;
import com.atguigu.gmall.model.to.SkuDetailTo;
import javassist.bytecode.SignatureAttribute;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.*;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @Author Xiaoxin
 * @Date 2022/9/1 20:41
 * @Version 1.0
 */
@Aspect //声明切面
@Component
public class CacheAspect {

    @Autowired
    CacheOpsService cacheOpsService;

    //创建一个表达式解析器，这个是线程安全的
    SpelExpressionParser parser = new SpelExpressionParser();

    ParserContext context=new TemplateParserContext();

    //编写通知方法

    //在标注了 @GmallCache注解的目标方法之前执行 测试
    @Before("@annotation(com.atguigu.gmall.item.cache.annotation.GmallCache)")
    public void xxxx() {
        //System.out.println("前置通知");生效

    }

    /**
     * 环绕通知
     * 所有目标方法的信息都在 连接点  ProceedingJoinPoint 可推进的连接点
     * try{
     * //前置通知
     * 目标方法.invoke(args);
     * }catch(Exception e){
     * //异常通知
     * }finally{
     * 后置通知
     * }
     * 在代理对象可以魔改任何东西 例如返回值,方法参数
     */


    @Around("@annotation(com.atguigu.gmall.item.cache.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取传来的第一个参数
//        Object arg = joinPoint.getArgs()[0];
        Object result = null;
        //key不同 方法可能不同
        String cacheKey = determinCacheKey(joinPoint);

        //1.先查缓存  不同方法返回数据也可能不一样
        Type returnType = getMethodGenericReturnType(joinPoint);

        Object cacheData = cacheOpsService.getCacheData(cacheKey, returnType);
        //2.判断缓存是否存在
        if (cacheData == null) {
            //3缓存没有 准备回源
            //4询问布隆里是否存在
//            boolean contains = cacheOpsService.bloomContains(arg);
            String bloomName = determinBloomName(joinPoint);
            if(!StringUtils.isEmpty(bloomName)){
                //开启了指定布隆
                Object bVal = determinBloomValue(joinPoint);
                boolean contains = cacheOpsService.bloomContains(bloomName,bVal);
                if(!contains){
                    return null;
                }
            }
            //6布隆说有 回源 有击穿风险 需要加锁
            boolean tryLock = false;
            String lockName = "";
            try {
                lockName = determinLockName(joinPoint);
                tryLock = cacheOpsService.tryLock(lockName);
                if (tryLock) {
                    //6.1获取到锁  回源
                    //执行这个目标方法里的调用的方法
                    result = joinPoint.proceed(joinPoint.getArgs());
                    //7.调用成功 重新保存进redis数据库
                    cacheOpsService.saveData(cacheKey, result);
                    return result;
                } else {
                    //6.2没获取到锁 证明可能有线程正在执行 睡1s然后查询缓存数据库
                    Thread.sleep(1000);
                    return cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
                }

            } finally {
                //判断是否加上了锁 如果加上了 就解锁
                if (tryLock) {
                    cacheOpsService.unlock(lockName);
                }
            }


        }
        //缓存中有直接返回
        return cacheData;
    }

    /**
     * 根据当前连接点获取注解上的CacheKey参数
     *
     * @param joinPoint
     * @return
     */
    private String determinCacheKey(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上@GmallCache注解里的参数
        //1.1先拿到目标方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //1.2通过目标方法拿到注解上的值
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        String expression = cacheAnnotation.cacheKey();

        //1.3 根据表达式计算缓存键  (创建一个表达式解析器 解析表达式)
        String cacheKey = evaluationExpression(expression, joinPoint, String.class);

        return cacheKey;
    }

    /**
     * 解析表达式方法
     * @param expression
     * @param joinPoint
     * @param clz
     * @return
     */
    private <T> T evaluationExpression(String expression, ProceedingJoinPoint joinPoint, Class<T> clz) {

        //1.创建表达式解析器
        //得到表达式
        Expression parseExpression = parser.parseExpression(expression, context);

        //2.上下文sku:info:#{#params[0]}
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

        //3.取出所有参数绑定上下文解析解析
        Object[] args = joinPoint.getArgs();
        evaluationContext.setVariable("params",args);

        //获取表达式解析器解析的值
        T value = parseExpression.getValue(evaluationContext, clz);

        //得到表达式解析的值
        return value;
    }

    /**
     * 获取目标方法的精确返回值类型
     * @param joinPoint
     * @return
     */
    private Type getMethodGenericReturnType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();
        Type type = method.getGenericReturnType();
        return type;
    }

    /**
     * 获取布隆过滤器的名字
     * @param joinPoint
     * @return
     */
    private String determinBloomName(ProceedingJoinPoint joinPoint) {
        //1、拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        //2、拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        String bloomName = cacheAnnotation.bloomName();

        return bloomName;
    }

    /**
     * 根据布隆过滤器值表达式计算出布隆需要判定的值
     * @param joinPoint
     * @return
     */
    private Object determinBloomValue(ProceedingJoinPoint joinPoint) {
        //1、拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        //2、拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        //3、拿到布隆值表达式
        String bloomValue = cacheAnnotation.bloomValue();

        Object expression = evaluationExpression(bloomValue, joinPoint, Object.class);

        return expression;
    }

    /**
     * 根据表达式计算出要用的锁的名字
     * @param joinPoint
     * @return
     */
    private String determinLockName(ProceedingJoinPoint joinPoint) {
        //1、拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //2、拿到注解
        Method method = signature.getMethod();
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        //3、拿到锁表达式
        //lock-方法名
        String lockName = cacheAnnotation.lockName();
        if(StringUtils.isEmpty(lockName)){
            //没指定锁用方法级别的锁
            return SysRedisConst.LOCK_PREFIX+method.getName();
        }

        //4、计算锁值
        String lockNameVal = evaluationExpression(lockName, joinPoint, String.class);
        return lockNameVal;
    }

    //@Around("@annotation(com.atguigu.gmall.item.cache.annotation.GmallCache)")
    public Object aroundtest(ProceedingJoinPoint joinPoint) {
        //1.获取切面签名  将要执行的目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //2.获取这个目标方法的传来的所有参数
        Object[] args = joinPoint.getArgs();

        //3.放行目标方法
        //joinPoint.proceed(args);第一种方法
        Method method = signature.getMethod();

        Object result = null;
        try {
            //joinPoint.getTarget()执行这个类的所有方法
            //目标方法执行完之后返回一个返回值
            result = method.invoke(joinPoint.getTarget(), args);
            //返回通知
        } catch (Exception e) {
            // 遇见异常不能在这里处理 继续往上抛出
            // 因为在这个代理对象上面可能还有代理对象 在这里处理掉之后 上面的代理对象就感知不到这个异常了
            // e.printStackTrace();
            throw new RuntimeException();
            //异常通知
        } finally {
            //后置通知
        }

        return result;
    }
}
