package com.meanymellow.youdontknowchampion;

import com.meanymellow.youdontknowchampion.model.Group;
import com.meanymellow.youdontknowchampion.model.GroupSorter;
import com.meanymellow.youdontknowchampion.model.Quality;
import com.meanymellow.youdontknowchampion.model.Student;
import com.meanymellow.youdontknowchampion.storage.StudentStorage;

import java.util.List;

public class Util {
    public static List<Group> createGroups(StudentStorage studentStorage) {
        List<Student> students = studentStorage.getAll();
        GroupSorter sorter = new GroupSorter();
        sorter.addStudents(studentStorage);
        sorter.sort();

        /*Iterator<Student> i = students.iterator();
        while(i.hasNext()) {
            Student student = i.next();

            if(sorter.tryAddStudent(student)){
                i.remove();
            }
        }

        i = students.iterator();
        while(i.hasNext()) {
            Student student = i.next();
            sorter.forceAddStudent(student);
            i.remove();
        }*/
        return sorter.getGroups();
    }

    public static Student cleanUp(Student student) {
        student.getGrade();

        if(student.getGender().toLowerCase().equals("m") || student.getGender().toLowerCase().equals("male")) {
            student.setGender("M");
        }
        else if(student.getGender().toLowerCase().equals("f") || student.getGender().toLowerCase().equals("female")) {
            student.setGender("F");
        } else {
            student.setGender("U");
        }

        if(student.getGrade().toLowerCase().equals("k") || student.getGrade().toLowerCase().equals("kinder") || student.getGrade().toLowerCase().equals("kindergarten")) {
            student.setGrade("0");
        }
        if(!isInteger(student.getGrade())) {
            student.setGrade("-1");
        }

        return student;
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static Quality combineQualities(Quality... qualities) {
        Quality quality = qualities[0];
        for(int i = 1; i < qualities.length; i++) {
            quality = combineTwoQualities(quality, qualities[i]);
        }
        return quality;
    }

    private static  Quality combineTwoQualities(Quality q1, Quality q2) {
        Quality quality = Quality.BAD;
        int compare = q1.compareTo(q2);

        if(compare == 0) {
            quality = q1;
        } else if(compare == -3 || compare == 3) {
            quality = Quality.BAD;
        } else if(compare == -2 || compare == 2) {
            if(q1 == Quality.PERFECT || q2 == Quality.PERFECT)
                quality = Quality.GOOD;
            else
                quality = Quality.OK;
        } else if(compare == -1 || compare == 1) {
            if(q1 == Quality.PERFECT || q2 == Quality.PERFECT)
                quality = Quality.GOOD;
            else if(q1 == Quality.GOOD || q2 == Quality.GOOD)
                quality = Quality.OK;
            else
                quality = Quality.BAD;
        }

        return quality;
    }

}
