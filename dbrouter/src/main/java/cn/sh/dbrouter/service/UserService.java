package cn.sh.dbrouter.service;

import cn.sh.dbrouter.domain.User;
import cn.sh.dbrouter.mapper.UserMapper;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** @author sh */
@Service
public class UserService {

    @Autowired private UserMapper userMapper;

    public void dataSourceChange(String name) {
        User user = findUser(name);
        if (!Objects.isNull(user)) {
            return;
        }
        user = new User(name);
        userMapper.insert(user);
        System.out.println("用户创建成功，userID:" + user.getId());
    }

    @Transactional
    public void transactionTest(String name) {
        User dbUser = findUser(name);
        User user = new User(name);
        userMapper.insert(user);
        System.out.println("插入新用户：" + name);
        User newUser = userMapper.findUser(name);
        System.out.println("新用户存在：" + !Objects.isNull(newUser));
    }

    public User findUser(String name) {
        return userMapper.findUser(name);
    }
}
