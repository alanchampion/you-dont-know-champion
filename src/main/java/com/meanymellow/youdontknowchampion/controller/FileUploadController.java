package com.meanymellow.youdontknowchampion.controller;

import com.meanymellow.youdontknowchampion.model.Student;
import com.meanymellow.youdontknowchampion.storage.StorageFileNotFoundException;
import com.meanymellow.youdontknowchampion.storage.StorageService;
import com.meanymellow.youdontknowchampion.storage.StudentStorage;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final StorageService storageService;
    private final StudentStorage studentStorage;
    private String currentFile;

    @Autowired
    public FileUploadController(StorageService storageService, StudentStorage studentStorage) {
        this.storageService = storageService;
        this.studentStorage = studentStorage;
    }

    @GetMapping("/upload")
    public String uploadForm(Model model) throws IOException {

        return "uploadForm";
    }

    @GetMapping("/files")
    public String displayFiles(Model model) {
        model.addAttribute("files", storageService.loadAll().map(
                path -> path.toString())
                .collect(Collectors.toList()));
        return "displayFiles";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/files/{filename:.+}/delete")
    public String deleteFile(@PathVariable String filename, RedirectAttributes redirectAttributes) {

        storageService.delete(filename);
        redirectAttributes.addFlashAttribute("message", "You successfully deleted " + filename + "!");
        return "redirect:/files";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if(file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message","Please select a file before uploading!");
            return "redirect:upload";
        }
        storageService.store(file);
        currentFile = StringUtils.cleanPath(file.getOriginalFilename());;
        redirectAttributes.addFlashAttribute("message",
                "You successfully added students from " + file.getOriginalFilename() + "!");

        return "redirect:/addupload";
    }

    @GetMapping("/addupload")
    public String saveStudents(RedirectAttributes redirectAttributes) throws IOException {
        List<Student> students = new ArrayList<Student>();

        /*model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));*/

        // model.addAttribute("students", students);

        storageService.loadAll().forEach(path -> {
            try {
                File file = storageService.loadAsResource(path.getFileName().toString()).getFile();
                // BufferedReader br = new BufferedReader(new FileReader(file));
                // System.out.println(br.readLine());

                try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
                    String[] values = null;
                    int grade = -1, firstName = -1, lastName = -1, school = -1, gender = -1;
                    if((values = csvReader.readNext()) != null) {
                        for(int i = 0; i < values.length; i++) {
                            if(values[i].toLowerCase().equals("grade")) {
                                grade = i;
                            }
                            else if(values[i].toLowerCase().equals("first name")) {
                                firstName = i;
                            }
                            else if(values[i].toLowerCase().equals("last name")) {
                                lastName = i;
                            }
                            else if(values[i].toLowerCase().equals("school")) {
                                school = i;
                            }
                            else if(values[i].toLowerCase().equals("gender")) {
                                gender = i;
                            }
                        }
                    }
                    if(grade == -1 || firstName == -1 || lastName == -1 || school == -1 || gender == -1) {
                        redirectAttributes.addFlashAttribute("message", "Error reading " + path.getFileName() + ". CSV not formatted correctly.");
                        System.out.println("Error. CSV is not formatted correctly");
                    }
                    while ((values = csvReader.readNext()) != null) {
                        students.add(new Student(values[grade], values[firstName], values[lastName], values[school], values[gender]));
                        redirectAttributes.addFlashAttribute("message", "Finished uploading " + path.getFileName() + "!");
                        // System.out.println(Arrays.toString(values));
                    }
                }
            } catch (FileNotFoundException e) {
                redirectAttributes.addFlashAttribute("message", "Error reading file. File not found.");
                e.printStackTrace();
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("message", "Error reading " + path.getFileName() + ". CSV not formatted correctly.");
                e.printStackTrace();
            }
        });

        storageService.delete(currentFile);
        currentFile = "";
        // storageService.init();
        studentStorage.saveAll(students);

        return "redirect:/";
    }

    @GetMapping("/deleteall")
    public String deleteAllStudents(Model model, RedirectAttributes redirectAttributes) throws IOException {
        redirectAttributes.addFlashAttribute("message", "Removed all students!");
        studentStorage.removeAll();
        return "redirect:/";
    }

    @GetMapping("/files/deleteall")
    public String deleteAllFiles(Model model, RedirectAttributes redirectAttributes) throws IOException {
        redirectAttributes.addFlashAttribute("message", "Removed all previous files!");
        storageService.deleteAll();
        storageService.init();
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}