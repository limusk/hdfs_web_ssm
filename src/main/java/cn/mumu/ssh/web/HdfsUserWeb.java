package cn.mumu.ssh.web;

import cn.mumu.ssh.model.Authority;
import cn.mumu.ssh.model.HdfsUser;
import cn.mumu.ssh.service.HdfsUserService;

import cn.mumu.ssh.util.HadoopUtils;
import cn.mumu.ssh.util.Utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class HdfsUserWeb implements Serializable {
    @Autowired
    private HdfsUserService hdfsUserService;

    private Logger log = LoggerFactory.getLogger(HdfsUserWeb.class);

    private static final long serialVersionUID = 1L;

    private String hadoopUserName;
    private String hadoopPassword;
    private String sessionProperty;

    /**
     * 登陆
     * @param
     * @return
     */
    @RequestMapping("/login.action")
    public void login(Map<String, Object> map,
                        String email, String password,
                        HttpSession session) {
        HdfsUser hUser = hdfsUserService.getByEmail(email);
        if (hUser == null) {
            map.put("flag","false");
            map.put("msg","登陆失败，用户名不存在");
            Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
            return;

        }
        if (!password.equals(hUser.getPassword())){
            map.put("flag","false");
            map.put("msg", "登陆成功，用户密码不正确!");
            Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
            return;
        } else {
            map.put("flag", "true");
            map.put("msg", "登陆成功");
            session.setAttribute("user",hUser.getName());
            session.setAttribute("email",hUser.getEmail());
            session.setAttribute("hUser", HadoopUtils.getHadoopUserName());
            log.info("用户：{}，email：{}登陆！", new Object[] {hUser.getName(),hUser.getEmail()});
        }
        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
//        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 注册
     */
    @RequestMapping("/register.action")
    public void register(Map<String, Object> map,
                         HdfsUser hdfsUser){
        hdfsUser.setAuthority(Authority.USER.ordinal());
        Integer ret = hdfsUserService.save(hdfsUser);
        if (ret > 0){
            map.put("flag", "true");
        } else {
            map.put("flag", "false");
            map.put("msg", "注册失败，请联系管理员");
        }
        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
//        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 注册前的检查，是否有重email
     */
    @RequestMapping("/registerCheck.action")
    public void registerCheck(Map<String, Object> map, String email){
        HdfsUser hUser = hdfsUserService.getByEmail(email);
        if (hUser != null) {
            map.put("flag", "false");
            map.put("msg","该邮件名已经注册!");
        } else {
            map.put("flag", "true");
        }
        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
//        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 注销
     */
    @RequestMapping("/logout.action")
    public void logout(Map<String, Object> map, HttpSession session) {
         if (session.getAttribute("user")!=null || session.getAttribute("eamil") != null){
             map.put("flag","true");
             map.put("msg","注销成功");
             log.info("用户：{}, email:{} 注销!", new Object[] { session.getAttribute("user"),
                     session.getAttribute("email") });
             session.invalidate();
         } else {
             map.put("flag", "false");
             map.put("msg","注销失败!");
         }
        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
//        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 权限验证
     */
    public void authCheck(Map<String ,Object> map,
                          HttpSession session) {
        // 进行ssh权限验证
        boolean hashHdfsLoginAuth = Utils.canLogin(hadoopUserName, hadoopPassword);
        if (!hashHdfsLoginAuth){
            map.put("flag", "false");
            map.put("msg","HDFS用户名或密码错误");
            session.setAttribute("authCheck","false");
        } else {
            map.put("flag", "true");
            session.setAttribute("authCheck", "true");
            session.setAttribute("tmpHadoopUserName", hadoopUserName); //临时存储，防止验证和更新使用的是不一样的用户
            session.setAttribute("tmpHadoopPassword",hadoopPassword);
        }
        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 更新hdfsuser表数据
     */
    public void authUpdate(Map<String, Object> map, HttpSession session){
        if (session.getAttribute("authCheck") == null ||
                "true".equals(session.getAttribute("authCheck"))) {
            map.put("flag", "false");
            map.put("msg","权限验证没有通过!");
            Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
            return;
        }
        if (!session.getAttribute("tmpHadoopUserName").equals(hadoopUserName)
                || !session.getAttribute("tmpHadoopPassword").equals(hadoopPassword)){
            map.put("flag", "false");
            map.put("msg","验证用户名或密码被修改");
            Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
            return;
        }
        String email = (String) session.getAttribute("email");
        HadoopUtils.updateHadoopUserNamePassword(hadoopUserName,hadoopPassword);
        map.put("flag", "true");
        map.put("msg","更新成功!");
        session.setAttribute("hUser", hadoopUserName);

        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 获取session中的sessionProperty对应的值
     */
    public void getSessionValue(Map<String, Object> map, HttpSession session) {
        int authority = -1;
        if (session.getAttribute("sessionProperty")!=null){
            authority = (Integer) session.getAttribute(sessionProperty);
        }
        map.put("authority", authority);
        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }
}