package com.meanymellow.youdontknowchampion.model;

import java.util.Objects;

public class Student {
    private String firstName, lastName, school, gender, grade;
    private int id;

    public Student() {}

    public Student(String grade, String firstName, String lastName, String school, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.grade = grade;
        this.school = school;
        this.gender = gender;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Student{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", school='" + school + '\'' +
                ", gender='" + gender + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(getFirstName(), student.getFirstName()) &&
                Objects.equals(getLastName(), student.getLastName()) &&
                Objects.equals(getSchool(), student.getSchool()) &&
                Objects.equals(getGender(), student.getGender()) &&
                Objects.equals(getGrade(), student.getGrade());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getSchool(), getGender(), getGrade());
    }
}
