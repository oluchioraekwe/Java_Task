package com.facebook.facebookapi.service;

import com.facebook.facebookapi.entity.Story;
import com.facebook.facebookapi.entity.StoryType;
import com.facebook.facebookapi.exception.ResourceNotFoundException;
import com.facebook.facebookapi.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoryServiceImpl implements StoryService{
    @Autowired
    private StoryRepository storyRepository;

    public StoryServiceImpl(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @Override
    public Story createStory(Story story) {
        return storyRepository.save(story);
    }

    @Override
    public List<Story>  getStories(int page,int size) {
        List<Object> result = new ArrayList<>();
        Pageable pageable = PageRequest.of(page,size);


        Page<Story> pageStory =  storyRepository.findAll( pageable);
        List<Story> stories = pageStory.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("stories",stories);
        response.put("currentPage",pageStory.getNumber());
        response.put("totalItems", pageStory.getTotalElements());
        response.put("totalPages", pageStory.getTotalPages());


        result.add(response);
        return  stories;
    }

    @Override
    public void deleteStory(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Story","id",id));
        storyRepository.delete(story);
    }

    @Override
    public void shareStory(String email, Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Story","id",id));

        StoryType type = story.getType();
        if(type.equals(StoryType.RESTRICTED)){
            List<String> sharedWith = story.getShareWith();
            sharedWith.add(email);
            story.setShareWith(sharedWith);
            storyRepository.save(story);
        }else {
            System.out.println("Cannot share story");
        }

    }

    @Override
    public List<Story> findAllUserStories(String email, String type, Long id, Integer limit, Integer page) {
        return storyRepository.findAllStories(email,type,id,limit,page);
    }

    @Override
    public Story getOneStory(Long id) {
       return storyRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Story","id",id));
    }


}
