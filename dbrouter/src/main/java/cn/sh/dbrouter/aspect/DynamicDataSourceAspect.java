package cn.sh.dbrouter.aspect;

import cn.sh.dbrouter.config.DataSourceName;
import cn.sh.dbrouter.config.DynamicDatasource;
import cn.sh.dbrouter.config.TargetDataSource;
import java.lang.reflect.Method;
import java.util.Objects;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/** @author sh */
@Aspect
@Component
public class DynamicDataSourceAspect {

    @Pointcut(
            "@within(cn.sh.dbrouter.config.TargetDataSource) || @annotation(cn.sh.dbrouter.config.TargetDataSource)")
    public void pointcut() {}

    @Before("pointcut()")
    public void changeDatasource(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        TargetDataSource annotation = method.getAnnotation(TargetDataSource.class);
        annotation =
                Objects.isNull(annotation)
                        ? joinPoint.getTarget().getClass().getAnnotation(TargetDataSource.class)
                        : annotation;
        annotation =
                Objects.isNull(annotation)
                        ? method.getDeclaringClass().getAnnotation(TargetDataSource.class)
                        : annotation;
        if (Objects.isNull(annotation)) {
            return;
        }
        DataSourceName dataSourceName = annotation.name();
        DynamicDatasource.DataSourceHolder.setDataSource(dataSourceName);
        System.out.println("切换数据源成功：" + dataSourceName.name());
    }

    @After("pointcut()")
    public void restoreDataSource(JoinPoint point) {
        DynamicDatasource.DataSourceHolder.clearDatasource();
        System.out.println("清除数据源成功");
    }
}
