package cn.mumu.ssh.test;

import cn.mumu.ssh.model.HdfsUser;
import cn.mumu.ssh.service.HdfsUserService;
import cn.mumu.ssh.service.impl.HdfsUserServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;


/**
 * @descibe
 * @auther limusk
 * @create 2020-12--21:13
 * @project hdfs_web_ssm
 */

@Configuration("classpath:/applicationContext.xml")
public class HdfsUserServiceTest {
    HdfsUserServiceImpl hdfsUserService = new HdfsUserServiceImpl();

    @Test
    public void testsave(){
        HdfsUser hdfsUser = new HdfsUser();
        hdfsUser.setId(null);
        hdfsUser.setName("admin");
        hdfsUser.setEmail("admin@qq.com");
        hdfsUser.setPassword("123123");
        hdfsUser.setAuthority(1);
        Integer res = hdfsUserService.save(hdfsUser);
        System.out.println(res);
    }

    @Test
    public void getByEamil(){
        String email = "admin@qq.com";
        HdfsUser user = hdfsUserService.getByEmail(email);
        System.out.println(user);

    }


}
