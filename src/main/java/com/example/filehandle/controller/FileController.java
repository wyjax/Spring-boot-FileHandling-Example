package com.example.filehandle.controller;

import com.example.filehandle.model.FileForm;
import com.example.filehandle.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/uploadOneFile")
    public String uploadOneFile(Model model) {
        model.addAttribute("fileForm", new FileForm());
        return "uploadOneFile";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(FileForm form) {
        MultipartFile[] files = form.getFileDatas();

        for (MultipartFile file : files) {
            if (file.getOriginalFilename() == null || file.getSize() == 0) continue;
            String filename = fileStorageService.storeFile(file);
        }
        return "redirect:/";
    }

    @GetMapping("/uploadMultiFile")
    public String uploadMultiFileHandler(Model model) {
        model.addAttribute("FileForm", new FileForm());
        return "uploadMultiFile";
    }

//    @PostMapping("/uploadMultiFile")
//    public String uploadMultipleFiles(FileForm form) {
//        MultipartFile[] files = form.getFileDatas();
//
//        for (MultipartFile file : files) {
//            if (file.getOriginalFilename() == null || file.getSize() == 0) continue;
//            String filename = fileStorageService.storeFile(file);
//        }
//        return "redirect:/";
//    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFile(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }
        catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("fileList")
    public String fileList(Model model) {
//        Resource resource = fileStorageService.lo

        return "ListForm";
    }
}