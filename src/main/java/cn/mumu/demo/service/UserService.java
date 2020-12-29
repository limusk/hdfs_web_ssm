package cn.mumu.demo.service;


import cn.mumu.demo.model.User;

/**
 * @descibe
 * @auther limusk
 * @create 2020-12--19:27
 * @project hdfs_web_ssm
 */

public abstract class UserService {
    public abstract int save(User user);
    public abstract void delete(int id);
    public abstract void update(User user);
    public abstract User load(int id);
}
