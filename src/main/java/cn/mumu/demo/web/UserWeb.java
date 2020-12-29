package cn.mumu.demo.web;


import cn.mumu.demo.model.User;
import cn.mumu.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @descibe
 * @auther limusk
 * @create 2020-12--19:25
 * @project hdfs_web_ssm
 */

@Controller
@RequestMapping("/test")
public class UserWeb {
    @Autowired
    private UserService userService;

    @RequestMapping("/save")
    public ModelAndView save(User user) {
        userService.save(user);
        return new ModelAndView("/success");
    }

}
