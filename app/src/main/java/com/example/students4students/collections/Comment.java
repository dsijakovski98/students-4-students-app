package com.example.students4students.collections;

import com.google.firebase.Timestamp;

public class Comment {
    private String id;
    private String content;
    private Timestamp timestamp;

    private String questionId;

    private String creatorId;
    private String creatorNickname;
    private String creatorProfileUrl;

    public Comment() {
        // Empty constructor needed
    }

    public Comment(String id, String content, Timestamp timestamp,
                   String questionId,
                   String creatorId, String creatorNickname, String creatorProfileUrl) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.questionId = questionId;
        this.creatorId = creatorId;
        this.creatorNickname = creatorNickname;
        this.creatorProfileUrl = creatorProfileUrl;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getCreatorNickname() {
        return creatorNickname;
    }

    public String getCreatorProfileUrl() {
        return creatorProfileUrl;
    }
}
