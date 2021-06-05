package cn.sh.dbrouter.mapper;

import cn.sh.dbrouter.config.DataSourceName;
import cn.sh.dbrouter.config.TargetDataSource;
import cn.sh.dbrouter.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/** @author sh */
@Mapper
@TargetDataSource(name = DataSourceName.READ)
public interface UserMapper {

    @TargetDataSource(name = DataSourceName.WRITE)
    @Insert("insert into t_user(name) value (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    long insert(User user);

    @Select("select * from t_user where name = #{name} order by id desc limit 1")
    User findUser(String name);
}
