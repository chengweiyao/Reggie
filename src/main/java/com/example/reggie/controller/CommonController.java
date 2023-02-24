package com.example.reggie.controller;

import com.example.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;


    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;

        //判断路径是否存在
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        try{
            file.transferTo(new File(basePath + fileName));
        }catch (IOException exception){
            exception.printStackTrace();
        }
        return R.success(fileName);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
       try{
           FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
           ServletOutputStream outputStream = response.getOutputStream();
           response.setContentType("image/jpeg");
           int len = 0;
           byte[] bytes = new byte[1024];
           while((len = fileInputStream.read(bytes)) != -1){
               outputStream.write(bytes,0,len);
               outputStream.flush();
           }
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
    }
}
