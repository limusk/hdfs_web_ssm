package cn.mumu.ssh.dao;

import cn.mumu.ssh.model.HdfsUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @descibe
 * @auther limusk
 * @create 2020-12--20:40
 * @project hdfs_web_ssm
 */

@Repository
public interface HdfsUserDao {
    @Insert("insert into t_user(name, email, password, authority)" +
            "values(#{name},#{email},#{password},#{authority})")
    public int save(HdfsUser hdfsUser);

    @Update("update table t_user set name=#{name}, email=${password}, authority=#{authority}" +
            "password=#{password}")
    public void update(HdfsUser hdfsUser);

    @Select("select * from t_user where email=#{email}")
    public List<HdfsUser> loadByEamil(String email);
}
