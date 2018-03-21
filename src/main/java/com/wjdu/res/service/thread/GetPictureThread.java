package com.wjdu.res.service.thread;

import com.wjdu.res.dto.ImageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Callable;

public class GetPictureThread implements Callable<ImageDTO> {

    private String remoteShortSrc;
    private String remoteSrc;
    private String filePath;

    public GetPictureThread(String remoteShortSrc, String remoteSrc,String filePath){
        super();
        this.remoteShortSrc = remoteShortSrc;
        this.remoteSrc = remoteSrc;
        this.filePath = filePath;
    }

    @Override
    public ImageDTO call() throws Exception {
        ImageDTO imageDTO = new ImageDTO();
        try {
            // 获取图片名
            String fileName = this.remoteSrc.substring(this.remoteSrc.lastIndexOf("/")).replace("/", "");
            if(fileName.contains(".jpg")||fileName.contains(".gif")||fileName.contains(".png")){
                fileName = fileName;
            }else{
                fileName = fileName+".jpg";
            }
            File file=new File(this.filePath + "\\" + fileName);
            if(!file.exists()){
                // 初始化url对象
                URL url = new URL(this.remoteSrc);
                URLConnection conn = url.openConnection();
                // 初始化in对象，也就是获得url字节流
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                InputStream in = conn.getInputStream();
                byte[] bs = new byte[1024];
                int len;
                OutputStream os = new FileOutputStream(this.filePath + "\\" + fileName);
                // 开始读取
                while ((len = in.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                os.close();
                in.close();
            }
            imageDTO.setRemoteShortSrc(this.remoteShortSrc);
            imageDTO.setRemoteSrc(this.remoteSrc);
            imageDTO.setLocalScr(this.filePath + "\\" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageDTO;
    }
}
