package cn.mumu.ssh.service;

import cn.mumu.ssh.model.HdfsUser;


/**
 * @descibe
 * @auther limusk
 * @create 2020-12--20:59
 * @project hdfs_web_ssm
 */

public interface HdfsUserService {
    public Integer save(HdfsUser hdfsUser);
    public HdfsUser getByEmail(String email);
}
