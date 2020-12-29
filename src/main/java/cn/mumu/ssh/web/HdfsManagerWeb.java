package cn.mumu.ssh.web;

import cn.mumu.ssh.model.HdfsRequestProperties;
import cn.mumu.ssh.model.HdfsResponseProperties;
import cn.mumu.ssh.service.HdfsService;
import cn.mumu.ssh.util.HadoopUtils;
import cn.mumu.ssh.util.Utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.security.AccessControlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @descibe
 * @auther limusk
 * @create 2020-12--19:27
 * @project hdfs_web_ssm
 */

@Controller
@RequestMapping("/hdfsManager")
public class HdfsManagerWeb  {
    private static final Logger log = LoggerFactory
            .getLogger(HdfsManagerWeb.class);

    private static final long serialVersionUID = 1L;


    @Autowired
    private HdfsService hdfsService;

//    private int rows;
//    private int page;
//    private File file;
//    private String fileFileName;
    /**
     * 读取文件夹下面的文件和文件夹
     */
    @RequestMapping("/listFolder.action")
    public void listFolder(Map<String, Object> map, HdfsRequestProperties hdfsFile,
                           int rows, int page) throws Exception {
        List<HdfsResponseProperties> files = hdfsService.listFolder(hdfsFile.getFolder());
        map.put("total", files.size());
        map.put("rows", getProperFiles(files, page, rows));
        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }


    private List<HdfsResponseProperties> getProperFiles(List<HdfsResponseProperties> files,
                                                        int page, int rows) {
        return files.subList((page-1)*rows, page*rows>files.size()?files.size():page*rows);

    }

    /**
     * ???
     *
     * @param map
     * @param hdfsFile
     */
    @RequestMapping("/checkExistAndAuth.action")
    public void checkExistAndAuth(Map<String, Object> map, HdfsRequestProperties hdfsFile) throws Exception {
        boolean exist = hdfsService.checkExist(hdfsFile.getFolder());
        if (!exist) {
            map.put("flag", "nodir");
            Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        }
        boolean hasAuth = true;
        if (hdfsFile.getAuth() == null
                || hdfsFile.getAuth().length() < 1
                || hdfsFile.getAuth().length() > 3) {
            log.info("权限设置异常!authString:{}", hdfsFile.getAuth());
            map.put("flag", "false");
            map.put("msg", "后台错误，请联系管理员！");
            Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
            return;
        }
        for (char a : hdfsFile.getAuth().toCharArray()) {
            hasAuth = hasAuth
                    && HadoopUtils.checkHdfsAuth(hdfsFile.getFolder(),
                    String.valueOf(a));
        }
        if (!hasAuth) {
            map.put("flag", "false");
            map.put("msg", "目录操作没有权限！");
        }
        if (map.get("flag") == null) {
            map.put("flag", "true");
        }
        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 移除文件夹
     */
    @RequestMapping("/deleteFolder.action")
    public void deleteFolder(Map<String, Object> map, HdfsRequestProperties hdfsFile) throws Exception {
        boolean flag = false;
        boolean exist = hdfsService.checkExist(hdfsFile.getFolder());
        if (!exist) {
            map.put("flag", "false");
            map.put("msg", "目录不存在！");
            Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
            return;
        }
        // 读取并且执行权限
        boolean auth = HadoopUtils.checkHdfsAuth(hdfsFile.getFolder(), "r")
                && HadoopUtils.checkHdfsAuth(hdfsFile.getFolder(), "x");
        if (!auth) {
            map.put("msg", "没有权限");
            map.put("flag", "false");
            Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
            return;
        }

        try {
            flag = hdfsService.deleteFolder(hdfsFile.getFolder(),
                    hdfsFile.isRecursive());
        } catch (RemoteException e) {
            if (e.getClassName().equals(
                    "org.apache.hadoop.fs.PathIsNotEmptyDirectoryException")) {
                map.put("msg", "目录下有子目录!");
            }
        }
        if (flag) {// 目录删除成功
            map.put("flag", "true");
        } else {// 目录删除失败
            map.put("flag", "false");
        }
        Utils.write2PrintWriter(JSON.toJSONString(map, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 检索文件夹
     */
    @RequestMapping("/searchFolder.action")
    public void searchFolder(Map<String, Object> map, HdfsRequestProperties hdfsFile,
                             int page, int rows) throws Exception {
        List<HdfsResponseProperties> files = hdfsService.searchFolder(
                hdfsFile.getFolder(), hdfsFile.getName(), hdfsFile.getNameOp(),
                hdfsFile.getOwner(), hdfsFile.getOwnerOp());
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("total", files.size());
        jsonMap.put("rows", getProperFiles(files, page, rows));
        Utils.write2PrintWriter(JSON.toJSONString(jsonMap, SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 新建文件夹
     */
    @RequestMapping("/createFolder.action")
    public void createFolder(Map<String, Object> map, HdfsRequestProperties hdfsFile) throws Exception {
        boolean exist = hdfsService.checkExist(hdfsFile.getFolder());
        if (exist) {
            map.put("flag", "hasdir");
            Utils.write2PrintWriter(JSON.toJSONString(map,SerializerFeature.IgnoreNonFieldGetter));
            return;
        }
        boolean flag = false;
        try {
            flag = hdfsService.createFolder(hdfsFile.getFolder(),
                    hdfsFile.isRecursive());
        } catch (AccessControlException e) {
            map.put("msg", "没有权限！");
        } catch (Exception e) {
            map.put("msg", "创建目录异常，请联系管理员！");
        }
        if (flag) {// 目录删除成功
            map.put("flag", "true");
        } else {// 目录删除失败
            map.put("flag", "false");
        }
        Utils.write2PrintWriter(JSON.toJSONString(map,SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    /**
     * 上传文件
     */
    @RequestMapping("/upload.action")
    public void upload(Map<String, Object> map, HdfsRequestProperties hdfsFile,
                       File file, String fileFileName) throws Exception {
        boolean flag = false;

        try {
            flag = hdfsService.upload(file.getAbsolutePath(),
                    hdfsFile.getFolder() + "/" + fileFileName);
        } catch (Exception e) {
            map.put("msg", "请联系管理员!");
            flag = false;
        }

        if (flag) {// 上传成功
            map.put("flag", "true");
        } else {// 失败
            map.put("flag", "false");

        }
        Utils.write2PrintWriter(JSON.toJSONString(map,SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    @RequestMapping("/download.action")
    public void download(Map<String, Object> map, HdfsRequestProperties hdfsFile) throws Exception {
        boolean flag = false;
        boolean dir = false;
        try {
            dir = this.hdfsService.isDir(hdfsFile.getFileName());
        } catch (Exception e) {
            map.put("msg", "请联系管理员!");
        }
        if (dir) {
            map.put("flag", "false");
            if (map.get("msg") == null)
                map.put("msg", "不能下载目录!");
            Utils.write2PrintWriter(JSON.toJSONString(map,SerializerFeature.IgnoreNonFieldGetter));
            return;
        }
        try {
            flag = hdfsService.download(hdfsFile.getFileName(),
                    hdfsFile.getLocalFile());
        } catch (Exception e) {
            map.put("msg", "请联系管理员!");
            flag = false;
        }

        if (flag) {// 成功
            map.put("flag", "true");
        } else {// 失败
            map.put("flag", "false");

        }
        Utils.write2PrintWriter(JSON.toJSONString(map,SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    @RequestMapping("/deleteFile.action")
    public void deleteFile(Map<String, Object> map, HdfsRequestProperties hdfsFile) throws Exception {
        boolean flag = false;

        try {
            flag = hdfsService.deleteFile(hdfsFile.getFileName());
        } catch (Exception e) {
            map.put("msg", "不能删除目录！");
            flag = false;
        }

        if (flag) {// 成功
            map.put("flag", "true");
        } else {// 失败
            map.put("flag", "false");
        }
        Utils.write2PrintWriter(JSON.toJSONString(map,SerializerFeature.IgnoreNonFieldGetter));
        return;
    }

    @RequestMapping("/read.action")
    public void read(Map<String, Object> map, HdfsRequestProperties hdfsFile) throws Exception {
        boolean dir = false;
        try {
            dir = hdfsService.isDir(hdfsFile.getFileName());
        } catch (Exception e) {
            map.put("msg", "请联系管理员!");
        }
        if (dir) {
            map.put("flag", "false");
            if (map.get("msg") == null)
                map.put("msg", "不能读取目录!");
            Utils.write2PrintWriter(JSON.toJSONString(map,SerializerFeature.IgnoreNonFieldGetter));
            return;
        }
        String data = null;

        try {
            data = this.hdfsService.read(hdfsFile.getFileName(),hdfsFile.getTextSeq(),hdfsFile.getRecords());
        } catch (Exception e) {
            map.put("msg", "请检查文件！");
            data = null;
        }

        if (data!=null) {// 成功
            map.put("flag", "true");
            map.put("data", data);
        } else {// 失败
            map.put("flag", "false");
        }
        Utils.write2PrintWriter(JSON.toJSONString(map,SerializerFeature.IgnoreNonFieldGetter));
        return;
    }
}
