package cn.mumu.demo.dao;


import cn.mumu.demo.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @descibe
 * @auther limusk
 * @create 2020-12--18:45
 * @project hdfs_web_ssm
 */

@Repository
public interface UserDAO {
    @Insert("insert into test(name, age) values(#{name},#{age})")
    public int save(User user);

    @Delete("delete from test where id = #{id}")
    public void delete(int id);

    @Update("update test set name=#{name}, age=#{age}")
    public void update(User user);

    @Select("select * from test where id=#{id}")
    public User load(int id);
}
