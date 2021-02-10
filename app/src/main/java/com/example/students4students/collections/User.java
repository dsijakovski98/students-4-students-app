package com.example.students4students.collections;

public class User {
    public String id;

    public String firstName;
    public String lastName;

    public String dateOfBirth;
    public String gender;

    public String major;

    public String email;
    public String nickname;

    public String profileUrl;

    public User(String id, String firstName, String lastName,
                String dateOfBirth, String gender, String major,
                String email, String nickname, String profileUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.major = major;
        this.email = email;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }

}
