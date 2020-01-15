package com.example.filehandle.controller;

import com.example.filehandle.model.FileForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/uploadOneFile")
    public String uploadOneFile(Model model) {
        model.addAttribute("FileForm", new FileForm());
        return "uploadOneFile";
    }

    @PostMapping("/uploadOneFile")
    public String uploadOneFileHandlerPOST(HttpServletRequest request, //
                                           Model model, //
                                           @ModelAttribute("myUploadForm") FileForm myUploadForm) {

        return doUpload(request, model, myUploadForm);
    }

    @GetMapping("/uploadMultiFile")
    public String uploadMultiFileHandler(Model model) {

        FileForm myUploadForm = new FileForm();
        model.addAttribute("FileForm", myUploadForm);

        return "uploadMultiFile";
    }

    @PostMapping("/uploadMultiFile")
    public String uploadMultiFileHandlerPOST(HttpServletRequest request, Model model,
                                             @ModelAttribute("FileForm") FileForm myUploadForm) {

        return doUpload(request, model, myUploadForm);
    }

    public String doUpload(HttpServletRequest request, Model model, FileForm myUploadForm) {
        String description = myUploadForm.getDescription();
        String folderPath = "C:/Users/spiritum/Desktop/uploadFolder";

        File uploadRootDir = new File(folderPath);
        // Create directory if it not exists.
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        MultipartFile[] fileDatas = myUploadForm.getFileDatas();

        List<File> uploadedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile fileData : fileDatas) {

            String name = fileData.getOriginalFilename();
            System.out.println("Client File Name = " + name);

            if (name != null && name.length() > 0) {
                try {
                    // Create the file at server
                    File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                    stream.write(fileData.getBytes());
                    stream.close();
                    uploadedFiles.add(serverFile);
                }
                catch (Exception e) {
                    failedFiles.add(name);
                }
            }
        }
        model.addAttribute("description", description);
        model.addAttribute("uploadedFiles", uploadedFiles);
        model.addAttribute("failedFiles", failedFiles);

        return "uploadResult";
    }
}