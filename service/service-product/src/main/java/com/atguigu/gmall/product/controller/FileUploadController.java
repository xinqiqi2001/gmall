package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Xiaoxin
 */
@RestController
@RequestMapping("admin/product/")
public class FileUploadController {

    @PostMapping("fileUpload")
    public Result fileUpload(@RequestPart("file") MultipartFile file){

        System.out.println("multipartFile.getSize() = " + file.getSize());
        return Result.ok();
    }
}
