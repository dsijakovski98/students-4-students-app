package com.example.students4students.collections;

import java.util.List;

public class UserCourses {
    private List<String> courses;

    public UserCourses() {
        // Empty constructor needed
    }

    public UserCourses(List<String> courses) {
        this.courses = courses;
    }

    public List<String> getCourses() {
        return courses;
    }
}
