package com.meanymellow.youdontknowchampion.model;

import java.util.ArrayList;
import java.util.List;

public class StudentCreation {
    private List<Student> students;

    public StudentCreation() {
        students = new ArrayList<>();
    }

    public StudentCreation(List<Student> students) {
        this.students = students;
    }

    public void addStudent(Student student) {
        this.students.add(student);
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}