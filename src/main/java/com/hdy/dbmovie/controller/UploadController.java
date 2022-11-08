package com.hdy.dbmovie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.pojo.User;
import com.hdy.dbmovie.service.UserService;
import com.hdy.dbmovie.utils.UploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/db/user")
@Slf4j
public class UploadController implements WebMvcConfigurer {

    @Autowired
    UserService userService;

    @PostMapping("/upload/{userId}")
    public Result upload(MultipartFile file,@PathVariable String userId) throws IOException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        log.info(path);
        //获取根目录
        String absolutePath = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath();
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //生成一个uuid名称出来
        String uuidFilename = UploadUtil.getUUIDName(originalFilename);
        //若文件夹不存在,则创建出文件夹
        File fileDir = new File(absolutePath+"/static/upload");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        //创建新的文件夹
        File newFile = new File(fileDir, uuidFilename);
        //将文件输出到目标的文件中
        file.transferTo(newFile);
        //将保存的文件路径更新到用户信息headImg中
        String savePath = uuidFilename;
        //调用业务更新user
        userService.update(new LambdaUpdateWrapper<User>()
                                            .eq(User::getUserId,userId)
                                            .set(User::getPic,savePath));
        //返回信息
        return new Result(200,"上传成功！");
    }

    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/getImg/{userId}")
    public Result get(HttpServletRequest request, @PathVariable String userId) {
        //1.根据用户名去获取相应的图片
        String savePath = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId)).getPic();
        //String filePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/uploadFile/"+savePath;
        //InputStream stream = getClass().getClassLoader().getResourceAsStream("upload/"+savePath);
        //2.将文件加载进来
        Resource resource = resourceLoader.getResource(savePath);
        //返回响应实体
        return new Result(200,"加载完成！",resource.toString());
    }
    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    @Value("${file.uploadFolder}")
    private String uploadFolder;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/**").addResourceLocations("file:E:/test/DBMovie/target/classes/static/upload");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
