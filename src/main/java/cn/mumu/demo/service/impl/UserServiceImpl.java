package cn.mumu.demo.service.impl;


import cn.mumu.demo.dao.UserDAO;
import cn.mumu.demo.model.User;
import cn.mumu.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @descibe
 * @auther limusk
 * @create 2020-12--19:34
 * @project hdfs_web_ssm
 */

@Service("UserService")
public class UserServiceImpl extends UserService {
    @Autowired
    private UserDAO userDAO;

    @Override
    public int save(User user) {
        return userDAO.save(user);
    }

    @Override
    public void delete(int id) {
        userDAO.delete(id);
    }

    @Override
    public void update(User user) {
        userDAO.update(user);
    }

    @Override
    public User load(int id) {
        return userDAO.load(id);
    }
}
