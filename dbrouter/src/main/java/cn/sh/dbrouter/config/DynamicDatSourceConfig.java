package cn.sh.dbrouter.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/** @author sh */
@Configuration
public class DynamicDatSourceConfig {

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDatasource(getMasterSource(), getSlaveSource()));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            @Qualifier("dynamicDatasource") DynamicDatasource dynamicDatasource) {
        return new DataSourceTransactionManager(dynamicDatasource);
    }

    @Bean(name = "dynamicDatasource")
    @Primary
    public DynamicDatasource dynamicDatasource(
            @Qualifier("masterDatasource") DataSource masterSource,
            @Qualifier("slaveDatasource") DataSource slaveSource) {
        Map<Object, Object> dataSources = new HashMap<>(2);
        dataSources.put(DataSourceName.WRITE, masterSource);
        dataSources.put(DataSourceName.READ, slaveSource);
        return new DynamicDatasource(masterSource, dataSources);
    }

    @Bean(name = "masterDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource getMasterSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "slaveDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource getSlaveSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
