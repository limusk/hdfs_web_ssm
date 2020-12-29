package cn.mumu.ssh.service.impl;

import cn.mumu.ssh.dao.HdfsUserDao;
import cn.mumu.ssh.model.HdfsUser;
import cn.mumu.ssh.service.HdfsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @descibe
 * @auther limusk
 * @create 2020-12--21:02
 * @project hdfs_web_ssm
 */

@Service("HdfsUserService")
public class HdfsUserServiceImpl implements HdfsUserService {

    @Autowired
    private HdfsUserDao hdfsUserDao;

    private Logger log = LoggerFactory.getLogger(HdfsUserServiceImpl.class);

    @Override
    public Integer save(HdfsUser hdfsUser) {
        if (checkExist(hdfsUser.getEmail())) {
            return -1;
        }else{
            hdfsUserDao.save(hdfsUser);
        }
        return 1;
    }

    @Override
    public HdfsUser getByEmail(String email) {
        List<HdfsUser> hdfsUsers = hdfsUserDao.loadByEamil(email);
        if (hdfsUsers==null || hdfsUsers.size()<1){
            return null;
        }else if (hdfsUsers.size() > 1) {
            log.info("多行email！email:{}"+email);
        }
        return hdfsUsers.get(0);
    }

    private boolean checkExist(String email){
        HdfsUser user = getByEmail(email);
        if (user != null){
            return true;
        }else{
            return false;
        }
    }
}
