package cn.mumu.ssh.service;

import cn.mumu.ssh.model.HdfsResponseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @descibe
 * @auther limusk
 * @create 2020-12--19:41
 * @project hdfs_web_ssm
 */

public interface HdfsService {
    /**
     * 目录显示
     * @param folder
     * @return
     */
    public List<HdfsResponseProperties> listFolder(String folder) throws Exception;

    /**
     * 删除目录
     * @param folder
     * @param recursive
     * @return
     */
    public boolean deleteFolder(String folder, boolean recursive) throws Exception;

    /**
     * 搜索目录
     * @param folder
     * @param name
     * @param nameOp
     * @param owner
     * @param ownerOp
     * @return
     */
    public List<HdfsResponseProperties> searchFolder(String folder, String name, String nameOp,
                                                     String owner, String ownerOp) throws Exception;

    /**
     * 检查名字与给定名字是否符合op关系
     * @param fileName
     * @param name
     * @param op
     * @return
     */
    public boolean checkName(String fileName, String name, String op);

    /**
     * 创建目录
     * @param folder
     * @param recursive
     * @return
     */
    public boolean createFolder(String folder, boolean recursive) throws Exception;

    /**
     * 检查目录是否存在
     * @param folder
     * @return
     */
    public boolean checkExist(String folder) throws Exception;

    /**
     * 是否是目录
     * @param dir
     * @return
     */
    public boolean isDir(String dir) throws Exception;

    /**
     * 上传数据
     * @param dir
     * @return
     */
    public boolean upload(String src, String des) throws Exception;

    /**
     * 删除文件
     * @param fileName
     * @return
     */
    public boolean deleteFile(String fileName) throws Exception;

    /**
     * 下载文件
     * @param fileName
     * @param localFile
     * @return
     */
    public boolean download(String fileName, String localFile) throws Exception;

    /**
     * 数据读取
     * @param fileName
     * @param textSeq
     * @param records
     * @return
     */
    public String read(String fileName, String textSeq, int records) throws Exception;

}
