package com.example.students4students;

public class Firebase {
    // Collections
    public static final String USER_COLLECTION = "users";
    public static final String COURSE_COLLECTION = "courses";
    public static final String QUESTION_COLLECTION = "questions";
    public static final String USER_COURSES_COLLECTION = "user_courses";
    public static final String COMMENT_COLLECTION = "comments";


    // ======================================================
    // User collection field names
    public static final String USER_COLLECTION_NICKNAME = "nickname";
    public static final String USER_COLLECTION_EMAIL = "email";

    // Course collection field names
    public static final String COURSE_COLLECTION_NAME = "name";
    public static final String COURSE_COLLECTION_ID = "id";

    // Question collection field names
    public static final String QUESTION_COLLECTION_ID = "id";
    public static final String QUESTION_COLLECTION_CREATOR_ID = "creatorId";
    public static final String QUESTION_COLLECTION_TIMESTAMP = "timestamp";
    public static final String QUESTION_COLLECTION_TOPIC_NAME = "topicName";

    // Comment collection field names
    public static final String COMMENT_COLLECTION_TIMESTAMP = "timestamp";
    public static final String COMMENT_COLLECTION_QUESTION_ID = "questionId";
}
