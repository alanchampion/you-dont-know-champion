package com.meanymellow.youdontknowchampion.controller;

import com.meanymellow.youdontknowchampion.Util;
import com.meanymellow.youdontknowchampion.model.Group;
import com.meanymellow.youdontknowchampion.model.Student;
import com.meanymellow.youdontknowchampion.model.StudentCreation;
import com.meanymellow.youdontknowchampion.storage.StorageFileNotFoundException;
import com.meanymellow.youdontknowchampion.storage.StorageService;
import com.meanymellow.youdontknowchampion.storage.StudentStorage;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class StudentInputController {
    private final StudentStorage studentStorage;
    private final StorageService storageService;
    private List<Group> groups;

    @Autowired
    public StudentInputController(StudentStorage studentStorage, StorageService storageService) {
        this.studentStorage = studentStorage;
        this.storageService = storageService;
    }

    @GetMapping("/input")
    public String showCreateForm(Model model) {
        StudentCreation studentsForm = new StudentCreation();

        // for (int i = 1; i <= 20; i++) {
        studentsForm.addStudent(new Student());
        // }

        model.addAttribute("form", studentsForm);
        return "createStudentsForm";
    }

    @PostMapping("/save")
    public String saveStudents(@ModelAttribute StudentCreation form, Model model) {
        studentStorage.saveAll(form.getStudents());

        model.addAttribute("students", studentStorage.getAll());
        return "redirect:/input";
    }

    @GetMapping("/groups")
    public String showAll(Model model) {
        groups = Util.createGroups(studentStorage);
        Collections.sort(groups);
        model.addAttribute("students", studentStorage.getAll());
        model.addAttribute("groups", groups);
        return "showGroups";
    }

    @GetMapping("/student/{id}")
    public String getStudent(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        Student studentForm = studentStorage.getStudent(id);
//        model.addAttribute("form", studentForm);
        model.addAttribute("student", studentStorage.getStudent(id));
        return "student";
    }

    @GetMapping("/student/{id}/delete")
    public String deleteStudent(@PathVariable int id, RedirectAttributes redirectAttributes) {
        boolean success = studentStorage.delete(id);
        if(success)
            redirectAttributes.addFlashAttribute("message", "You successfully deleted the student!");
        else
            redirectAttributes.addFlashAttribute("message", "Unable to find student to delete!");
        return "redirect:/";
    }

    @PostMapping("/student/{id}/save")
    public String saveStudent(@PathVariable int id, @ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        boolean success = studentStorage.update(id, student);
        if(success)
            redirectAttributes.addFlashAttribute("message", "You successfully updated the student!");
        else
            redirectAttributes.addFlashAttribute("message", "Unable to find student to update!");
        return "redirect:/";
    }

    @GetMapping("/groups/export")
    @ResponseBody
    public ResponseEntity<Resource> exportGroups(Model model, RedirectAttributes redirectAttributes) {
        // TODO Don't throw exceptions
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MMMdd_HH-mm");
        String fileName = "group_" + dateFormat.format(date) + ".csv";
        String contentType = "text/csv";
        byte[] content = null;

        try {
            // Resource file = storageService.loadAsResource(filename);
            File newFile = new File("temp.csv");
            FileWriter outputfile = new FileWriter(newFile);
            CSVWriter csvWriter = new CSVWriter(outputfile);
            String[] header = {"Title", "First Name", "Last Name", "Grade", "School", "Gender"};
            csvWriter.writeNext(header);
            for (Group group : groups) {
                boolean firstStudent = true;
                for (Student student : group.getStudents()) {
                    if (firstStudent) {
                        String[] line = {group.getName(), student.getFirstName(), student.getLastName(), student.getGrade(), student.getSchool(), student.getGender()};
                        csvWriter.writeNext(line);
                        firstStudent = false;
                    } else {
                        String[] line = {"", student.getFirstName(), student.getLastName(), student.getGrade(), student.getSchool(), student.getGender()};
                        csvWriter.writeNext(line);
                    }
                }
            }
            csvWriter.close();
            outputfile.close();
            content = Files.readAllBytes(newFile.toPath());
            MultipartFile file = new MockMultipartFile(fileName, fileName, contentType, content);
            storageService.store(file);
            newFile.delete();

            Resource resource = storageService.loadAsResource(fileName);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
