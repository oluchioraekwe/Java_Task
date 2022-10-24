package com.facebook.facebookapi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "story")
public class Story {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "heading",nullable = false)
    private String heading;

    @Column(name = "body")
    private String body;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private StoryType type;

   @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "user_id")
    private Long userId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "share_with")
    @ElementCollection(targetClass = String.class,fetch = FetchType.EAGER)
//    @ManyToMany(fetch = FetchType.EAGER)
    private List<String> shareWith = new ArrayList<>();

    public Story() {
    }

    public Story(Long id, String heading, String body, StoryType type, Long userId, List<String> shareWith) {
        this.id = id;
        this.heading = heading;
        this.body = body;
        this.type = type;
        this.userId = userId;
        this.shareWith = shareWith;
    }

    public Story(String heading, String body, StoryType type, Long userId, List<String> shareWith) {
        this.heading = heading;
        this.body = body;
        this.type = type;
        this.userId = userId;
        this.shareWith = shareWith;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public StoryType getType() {
        return type;
    }

    public void setType(StoryType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getShareWith() {
        return shareWith;
    }

    public void setShareWith(List<String> shareWith) {
        this.shareWith = shareWith;
    }

    @Override
    public String toString() {
        return "Story{" +
                "id=" + id +
                ", heading='" + heading + '\'' +
                ", body='" + body + '\'' +
                ", type=" + type +
                ", userId=" + userId +
                ", shareWith=" + shareWith +
                '}';
    }
}
