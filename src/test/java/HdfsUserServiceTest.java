import cn.mumu.ssh.model.HdfsUser;

import cn.mumu.ssh.service.HdfsUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;


/**
 * @descibe
 * @auther limusk
 * @create 2020-12--21:13
 * @project hdfs_web_ssm
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class HdfsUserServiceTest {

    @Autowired
    private HdfsUserService hdfsUserService;

    @Test
    public void testsave(){
        HdfsUser hdfsUser = new HdfsUser();
        hdfsUser.setName("admin");
        hdfsUser.setEmail("admin1@qq.com");
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
