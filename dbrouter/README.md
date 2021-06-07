
**扫码关注微信公众号(Different Java)**

![qrcode_for_gh_1706f00849c9_344.jpg](http://ww1.sinaimg.cn/large/69ad3470gy1gmd7g8h298j209k09kt8n.jpg)

# 摘要

1. 数据库读写分离
2. MySQL主从数据库搭建
3. 基于AbstractRoutingDataSource实现多数据源切换
4. @Transactional
5. 测试


# 1.数据库读写分离

数据库读写分离的实现主要有两种方式：

- 基于中间件
- 基于程序自实现

## 1.1 基于中间件

提供一个统一的中间件，程序连接到中间件，中间件帮我们做读写分离，例如MyCat。

基于中间件的实现在数据库作扩容增加负载节点时，业务应用无感知，不需要修改任何代码都可以获取连接到新的节点，当然实现起来相对复杂。

## 1.2 基于程序自实现

每个业务应用实现自己的读写分离，优点实现简单，但如果读写的负载节点发生变化时，必须要修改业务应用代码。

本文的实现我们基于Spring的AbstractRoutingDataSource来实现。

# 2. 主从数据库搭建
```shell
# 假设你现在已经在dbrouter模块(目录)下
cd master-slave-db
./start.sh
```
使用start.sh可以一键搭建MySQL主从数据库。该脚本的主逻辑如下：

1. 利用docker-compose部署两个MySQL服务
2. 登录Master数据库创建同步账号replication
3. 登录Slave数据库设置需要同步的Master数据库账户和密码(replication)，并且设置开始同步的位置，然后开启同步
4. 设置Slave库为只读模式

## 2.1 创建测试表

```sql
create table t_user(
    id bigint auto_increment primary key,
    name varchar(32) default null,
    create_time timestamp default current_timestamp,
    update_time timestamp default current_timestamp
);
```

## 3. 基于AbstractRoutingDataSource实现多数据源切换

整个实现大约有以下核心类：

- DataSourceName：定义数据源的名称
- TargetDataSource注解：程序在运行时需要选择的数据源
- DynamicDataSourceAspect：用于动态的修改数据源
- DynamicDatasource：AbstractRoutingDataSource实现类，根据DynamicDataSourceAspect设置的DataSourceName来实现数据源的切换
- DynamicDatSourceConfig：动态数据源的配置，用于初始化主从数据源、SqlSessionFactory、PlatformTransactionManager以及DynamicDatasource

# 4. @Transactional

通过@Transactional开启事务以后，在获取到数据源建立连接后，后面不会再对数据源进行切完，直至整个事务完成。

一般我们在开启事务的时候往往是因为业务逻辑中包含多个写操作，需要一起失败或者一起成功，既然需要写操作，我们必须保证我们建立的数据库连接
是与Master库建立。 核心实现是：

**保证我开启事务时拿到的必须是Master数据源，不管方法上是否有自定义的@DataSourceName注解，代码如下：**

```java

public static class DataSourceHolder {

    private static final ThreadLocal<DataSourceName> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 此处假设没有设置数据源的话，默认为写数据源(Master)
     * @return
     */
    public static DataSourceName getDataSource() {
        DataSourceName sourceName = CONTEXT_HOLDER.get();
        return Objects.isNull(sourceName) ? DataSourceName.WRITE : sourceName;
    }
}

/**
 * masterDatasource必须保证是写数据源
 */
@Configuration
public class DynamicDatSourceConfig {

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
}
```

# 5. 测试
测试类位于dbroute模块下的test模块，类名为DataSourceRouteTest

# 扫码关注微信公众号(Different Java)

![qrcode_for_gh_1706f00849c9_344.jpg](http://ww1.sinaimg.cn/large/69ad3470gy1gmd7g8h298j209k09kt8n.jpg)


