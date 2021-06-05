package cn.sh.dbrouter.config;

import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/** @author sh */
public class DynamicDatasource extends AbstractRoutingDataSource {

    public DynamicDatasource(DataSource defaultSource, Map<Object, Object> targetSources) {
        super.setDefaultTargetDataSource(defaultSource);
        super.setTargetDataSources(targetSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.getDataSource();
    }

    public static class DataSourceHolder {

        private static final ThreadLocal<DataSourceName> CONTEXT_HOLDER = new ThreadLocal<>();

        public static DataSourceName getDataSource() {
            DataSourceName sourceName = CONTEXT_HOLDER.get();
            return Objects.isNull(sourceName) ? DataSourceName.WRITE : sourceName;
        }

        public static void setDataSource(DataSourceName dataSource) {
            CONTEXT_HOLDER.set(dataSource);
        }

        public static void clearDatasource() {
            CONTEXT_HOLDER.remove();
        }
    }
}
