package cn.mumu.demo.test; /**
 * @descibe
 * @auther limusk
 * @create 2020-12--22:37
 * @project hdfs_web_ssm
 */


import cn.mumu.demo.model.User;
import cn.mumu.demo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
// @Transactional(transactionManager = "txManager")
// maven test 使用@Transactional注解或extends
// AbstractTransactionalJUnit4SpringContextTests 任一即可完成事务
public class UserServiceImplTest extends
        AbstractTransactionalJUnit4SpringContextTests {

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

        User user = userService.load(2);
        System.out.println(user);
        assertTrue(user.getAge() == user.getAge()
                && user.getName().equals(user.getName()));
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
