package cn.mumu.ssh.service.impl;

import cn.mumu.ssh.model.HdfsResponseProperties;
import cn.mumu.ssh.service.HdfsService;
import cn.mumu.ssh.util.HadoopUtils;
import cn.mumu.ssh.util.Utils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @descibe
 * @auther limusk
 * @create 2020-12--21:04
 * @project hdfs_web_ssm
 */

@Service("HdfsService")
public class HdfsServiceImpl implements HdfsService {
    private static final Logger log = LoggerFactory.getLogger(HdfsServiceImpl.class);

    @Override
    public List<HdfsResponseProperties> listFolder(String folder) throws Exception {
        List<HdfsResponseProperties> files = new ArrayList<>();
        FileSystem fs = HadoopUtils.getFs();
        FileStatus[] fileStatuses = fs.listStatus(new Path(folder));

        for (FileStatus file: fileStatuses){
            files.add(Utils.getDataFromLocatedFileStatus(file));
        }
        return files;
    }

    @Override
    public boolean deleteFolder(String folder, boolean recursive) throws Exception {
        FileSystem fs = HadoopUtils.getFs();
        try {
            return fs.delete(new Path(folder), recursive);
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public List<HdfsResponseProperties> searchFolder(String folder, String name, String nameOp, String owner, String ownerOp) throws Exception {
        List<HdfsResponseProperties> files = new ArrayList<>();
        FileSystem fs = HadoopUtils.getFs();
        FileStatus[] fileStatuses = fs.listStatus(new Path(folder));
        for (FileStatus file: fileStatuses){
            if (!checkName(file.getPath().getName(), name, nameOp))
                continue;
            if (!checkName(file.getOwner(),owner,ownerOp))
                continue;
            files.add(Utils.getDataFromLocatedFileStatus(file));
        }
        return files;
    }

    @Override
    public boolean checkName(String fileName, String name, String op) {
        switch (op) {
            case "no":
                return true;
            case "contains":
                return fileName.contains(name) ? true : false;
            case "equals":
                return fileName.equals(name);
            case "noequals":
                return fileName.contains(name) ? false : true;

            default:
                log.info("wrong op:{}", op);
                break;
        }
        return false;
    }

    @Override
    public boolean createFolder(String folder, boolean recursive) throws Exception {
        FileSystem fs = HadoopUtils.getFs();
        try {
            return fs.mkdirs(new Path(folder));
        } catch (IOException e) {
           throw e;
        }
    }

    @Override
    public boolean checkExist(String folder) throws IllegalArgumentException, Exception {
        FileSystem fs = HadoopUtils.getFs();
        boolean exists = fs.exists(new Path(folder));
        if (!exists){
            log.info("文件或目录：{}不存在！", folder);
        }
        return exists;
    }

    @Override
    public boolean isDir(String dir) throws IOException {
        boolean flag = HadoopUtils.getFs().isDirectory(new Path(dir));
        return flag;
    }

    @Override
    public boolean upload(String src, String des) throws Exception {
        try {
            FileSystem fs = HadoopUtils.getFs();
            fs.copyFromLocalFile(new Path(src), new Path(des));
        } catch (IOException e) {
            log.info("数据下载异常, src:{}, des:{}",
                    new Object[] { src, des});
            throw e;
        }
        return true;
    }

    @Override
    public boolean deleteFile(String fileName) throws Exception {
        boolean flag = false;
        try {
            flag = HadoopUtils.getFs().delete(new Path(fileName), false);
        } catch (IllegalArgumentException | IOException e) {

            log.info("数据删除异常，fileName:{}", new Object[] { fileName });
            throw e;
        }
        return flag;
    }

    @Override
    public boolean download(String fileName, String localFile) throws Exception {
        boolean flag = true;
        try {
            HadoopUtils.getFs().copyToLocalFile(new Path(fileName),
                    new Path(localFile));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("数据下载异常，src:{},des:{}",
                    new Object[] { fileName, localFile });
            throw e;
        }
        return flag;
    }

    @Override
    public String read(String fileName, String textSeq, int records) throws Exception {
        String data = null;
        try{
            if("text".equals(textSeq)){
                data = HadoopUtils.readText(fileName,records);
            }else if("seq".equals(textSeq)){
                data = HadoopUtils.readSeq(fileName,records);
            }else{
                log.info("数据读取参数设置错误,textSeq:{}",textSeq);
            }
        }catch(Exception exception){
            log.info("数据读取异常:fileName:{},textSeq:{}",new Object[]{fileName,textSeq});
            throw exception;
        }
        return data;
    }

}
