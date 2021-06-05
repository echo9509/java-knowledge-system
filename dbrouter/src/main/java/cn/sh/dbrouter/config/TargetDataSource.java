package cn.sh.dbrouter.config;

import java.lang.annotation.*;

/** @author sh */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {

    /**
    * 默认写数据源
    *
    * @return
    */
    DataSourceName name() default DataSourceName.WRITE;
}
