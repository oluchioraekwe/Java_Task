package com.facebook.facebookapi.service;

import com.facebook.facebookapi.entity.Story;
import com.facebook.facebookapi.entity.StoryType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface StoryService {
    Story createStory(Story story);

    List<Story>  getStories(int page, int size);

    void deleteStory(Long id);

    void shareStory(String email, Long id);

    List<Story> findAllUserStories(String email, String type, Long id, Integer limit, Integer page);

    Story getOneStory(Long id);

}
