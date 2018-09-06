package com.hex.bigdata.udsp.common.controller;

import com.hex.goframe.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * Created by PC on 2017/6/8.
 */
@Controller
@RequestMapping("/com/file")
public class ComFileDownLoadController {

    @RequestMapping("/download")
    public void download(String filePath, HttpServletResponse rsp){
        OutputStream os = null;
        FileInputStream stream = null;
        try {
            os = rsp.getOutputStream();
            rsp.reset();
            String fileName = FileUtil.getFileName(filePath);
            rsp.setHeader("Content-Disposition", "attachment;filename="
                    + fileName);
            rsp.setContentType("application/octet-stream;charset=utf-8");
            File file = new File(filePath);
            // 判断文件是否存在
            if (file.exists()) {
                stream = new FileInputStream(file);
                IOUtils.copy(stream, os);
            } else {
                rsp.sendError(204);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(stream);
        }
    }

}
