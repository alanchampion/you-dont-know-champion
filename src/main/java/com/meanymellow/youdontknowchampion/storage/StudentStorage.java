package com.meanymellow.youdontknowchampion.storage;

import com.meanymellow.youdontknowchampion.Util;
import com.meanymellow.youdontknowchampion.model.Student;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentStorage {
    final private List<Student> students = new ArrayList<>();
    private int currentId;

    public StudentStorage() {
        currentId = 280;
    }

    public void saveAll(List<Student> students) {
        for(Student student : students) {
            save(student);
        }
    }

    public void save(Student student) {
        Student clean = Util.cleanUp(student);
        clean.setId(currentId);
        currentId++;
        this.students.add(clean);
    }

    public Student getStudent(int id) {
        Iterator<Student> i = this.students.iterator();
        while(i.hasNext()) {
            Student student = i.next();
            if(student.getId() == id) {
                return student;
            }
        }
        return null;
    }

    public boolean update(int id, Student student) {
        for(int i = 0; i < students.size(); i++) {
            if(students.get(i).getId() == id) {
                students.remove(i);
                students.add(i, student);
                return true;
            }
        }

        return false;
    }

    public boolean delete(int id) {
        Iterator<Student> i = this.students.iterator();
        while(i.hasNext()) {
            Student student = i.next();
            if(student.getId() == id) {
                i.remove();
                return true;
            }
        }
        return false;
    }

    public List<Student> getAll() {
        return new ArrayList<>(this.students);
    }

    public Map<String, List<Student>> getAllGrades() {
        Map<String, List<Student>> grades = new HashMap<>();
        for(Student student : students) {
            // System.out.println(student.getFirstName());
            grades.computeIfAbsent(student.getGrade().toLowerCase(), k -> new ArrayList<>()).add(student);
        }
        return grades;
    }

    public List<Student> getGrade(String g) {
        List<Student> grade = new ArrayList<>();
        for(Student student : students) {
            // System.out.println(student.getFirstName());
            if(student.getGrade().toLowerCase().equals(g.toLowerCase()))
                grade.add(student);
        }
        return grade;
    }

    public Map<String, List<Student>> getAllGenders() {
        Map<String, List<Student>> genders = new HashMap<>();
        for(Student student : students) {
            // System.out.println(student.getFirstName());
            genders.computeIfAbsent(student.getGender().toLowerCase(), k -> new ArrayList<>()).add(student);
        }
        return genders;
    }

    public List<Student> getGender(String g) {
        List<Student> gender = new ArrayList<>();
        for(Student student : students) {
            // System.out.println(student.getFirstName());
            if(student.getGender().toLowerCase().equals(g.toLowerCase()))
                gender.add(student);
        }
        return gender;
    }

    public Map<String, List<Student>> getAllSchools() {
        Map<String, List<Student>> schools = new HashMap<>();
        for(Student student : students) {
            // System.out.println(student.getFirstName());
            schools.computeIfAbsent(student.getSchool().toLowerCase(), k -> new ArrayList<>()).add(student);
        }
        return schools;
    }

    public List<Student> getSchool(String s) {
        List<Student> school = new ArrayList<>();
        for(Student student : students) {
            // System.out.println(student.getFirstName());
            if(student.getSchool().toLowerCase().equals(s.toLowerCase()))
                school.add(student);
        }
        return school;
    }

    public void removeAll() {
        this.students.clear();
    }
}