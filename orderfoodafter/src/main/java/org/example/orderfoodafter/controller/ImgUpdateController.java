package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.DefaultApplicationProperties;
import org.example.orderfoodafter.common.DefaulteProperties;
import org.example.orderfoodafter.common.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
@RestController
@RequestMapping("/imgupdate")
public class ImgUpdateController {
    @Autowired
    private DefaulteProperties defaulteProperties;

    @PostMapping("/updateimage")
    public R updateImage(@RequestParam("file") MultipartFile file) throws Exception{
        String  filename = System.currentTimeMillis()+file.getOriginalFilename();
        System.out.println("文件名: " + filename);
        file.transferTo(new File(defaulteProperties.getUploadfilepath()+filename));
        return new R().addData("imgpath",filename);
    }
}
