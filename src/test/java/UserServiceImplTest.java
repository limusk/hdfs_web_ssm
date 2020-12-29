/**
 * @descibe
 * @auther limusk
 * @create 2020-12--22:37
 * @project hdfs_web_ssm
 */


import static org.junit.Assert.assertTrue;

import cn.mumu.demo.model.User;
import cn.mumu.demo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
// @Transactional(transactionManager = "txManager")
// maven test 使用@Transactional注解或extends
// AbstractTransactionalJUnit4SpringContextTests 任一即可完成事务
public class UserServiceImplTest {

    @Autowired
    UserService userService;

    @Test
    public void testSave() {
        User user = new User();
        user.setName("张三");
        user.setAge(32);
        int id = userService.save(user);
        System.out.println(id);
        assertTrue(id > 0);
    }

    @Test
    public void testLoad() {
//        User user = new User();
//        user.setName("张三");
//        user.setAge(32);
//        int id = userService.save(user);

        User user = userService.load(4);
        System.out.println(user);

    }

    @Test
    public void testupdate() {
        User user = new User();
        user.setName("张三");
        user.setAge(32);
        userService.update(user);

    }

    @Test
    public void testdelete() {
        userService.delete(2);

    }
}
