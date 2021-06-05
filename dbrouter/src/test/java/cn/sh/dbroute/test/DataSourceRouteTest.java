package cn.sh.dbroute.test;

import cn.sh.dbrouter.DbRouterApplication;
import cn.sh.dbrouter.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DbRouterApplication.class)
public class DataSourceRouteTest {

    @Autowired private UserService userService;

    @Test
    public void testDataSourceChange() {
        // 增加用户
        userService.dataSourceChange("test8");
    }

    @Test
    public void testTransaction() {
        // 增加用户
        userService.transactionTest("testTransaction1");
    }
}
