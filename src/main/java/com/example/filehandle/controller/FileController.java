package com.example.filehandle.controller;

import com.example.filehandle.model.FileForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {
    private static final String dir = "C:/Users/spiritum/Desktop/uploadFolder";

    @Autowired
    private ServletContext servletContext;

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

        File uploadRootDir = new File(dir);
        // Create directory if it not exists.
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
        }
        MultipartFile[] fileDatas = myUploadForm.getFileDatas();

        List<File> uploadedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile fileData : fileDatas) {

            String name = StringUtils.cleanPath(fileData.getOriginalFilename());
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

    @GetMapping("/download/{filename}")
    public ResponseEntity<InputStreamResource> download(@PathVariable("filename") String filename) throws IOException {
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(servletContext, filename);
        System.out.println("fileName: " + filename);
        System.out.println("mediaType: " + mediaType);

        File file = new File(dir + "/" + filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                // Content-Type
                .contentType(mediaType)
                // Contet-Length
                .contentLength(file.length()) //
                .body(resource);
    }
}