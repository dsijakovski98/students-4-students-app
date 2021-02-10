package com.example.students4students.collections;

import com.google.firebase.Timestamp;

public class Question {
    private String id;
    private String title;
    private String content;

    private Timestamp timestamp;

    private String creatorId;
    private String creatorNickname;
    private String creatorProfileUrl;

    private String topicId;
    private String topicName;


    public Question() {
        // Empty constructor needed
    }

    public Question(String id, String title, String content, Timestamp timestamp,
                    String creatorId, String creatorNickname, String creatorProfileUrl,
                    String topicId, String topicName) {
        this.id = id;
        this.title = title;
        this.content = content;

        this.timestamp = timestamp;

        this.creatorId = creatorId;
        this.creatorNickname = creatorNickname;
        this.creatorProfileUrl = creatorProfileUrl;

        this.topicId = topicId;
        this.topicName = topicName;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
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

    public String getTopicId() {
        return topicId;
    }

    public String getTopicName() {
        return topicName;
    }
}
