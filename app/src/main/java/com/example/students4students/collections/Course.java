package com.example.students4students.collections;


public class Course {
    private String id;
    private String name;
    private String description;
    private String major; // TODO: Android 2: Change to list

    private String pictureUrl;

    private int semester;

    public Course() {
        // Empty constructor needed
    }

    public Course(String id, String name, String description, String major, String pictureUrl, int semester) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.major = major;
        this.pictureUrl = pictureUrl;
        this.semester = semester;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMajor() {
        return major;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public int getSemester() {
        return semester;
    }
}
