package com.facebook.facebookapi.controller;

import com.facebook.facebookapi.entity.Story;
import com.facebook.facebookapi.entity.StoryType;
import com.facebook.facebookapi.entity.User;
import com.facebook.facebookapi.filter.CustomAuthenticationFilter;
import com.facebook.facebookapi.service.StoryService;
import com.facebook.facebookapi.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StoryController {

    @Autowired
    private StoryService storyService;

    @Autowired
    private UserServiceImpl userService;

    public StoryController() {
    }

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    public StoryController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/stories")
    public ResponseEntity<?> getAllStories(@RequestBody GetStoriesBody body){
        Authentication authenticationFilter = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationFilter.getPrincipal().toString();
        User user = userService.getUser(username);
        String type = "PUBLIC";
        String email = user.getEmail();
        Long id = user.getId();
        Integer limit = body.getCount();
        Integer page;
        if(body.getStart() == 0){
            page = 0;
        }else {
            page = body.getStart()-1;
        }

        Integer skip = limit*page;

        List<Story> savedStories = storyService.findAllUserStories(email,type,id,limit,skip);

        Map<String, Object> response = new HashMap<>();
        response.put("status",200);
        response.put("stories",savedStories );

        CustomAuthenticationFilter.getCleanJetCookie();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/story")
    public ResponseEntity<Story> saveStory(@RequestBody Story story){
        return new ResponseEntity<Story>(storyService.createStory(story), HttpStatus.CREATED);
    }

    @DeleteMapping("/story")
    public ResponseEntity<?> deleteStory(@RequestBody DeleteStoryBody body){
        Map<String,Object> response = new HashMap<>();
        Authentication authenticationFilter = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationFilter.getPrincipal().toString();
        User user = userService.getUser(username);
        Story story = storyService.getOneStory(body.getStoryId());
        Long userId = user.getId();
        if(userId != story.getUserId()){
            response.put("error_message","Cannot delete Story");
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }
        try{
            storyService.deleteStory(body.getStoryId());
            response.put("status", 200);
            response.put("message","Story deleted successfully.");
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }catch (Exception exception){
            response.put("error_message",exception.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping("/story/share")
    public void shareStory(@RequestBody ShareStoryBody body){

        try {
            storyService.shareStory(body.getEmail(), body.getStoryId());
        }catch (Exception exception){
            System.out.println(exception);
        }
    }
}
class ShareStoryBody{
    private Long storyId;
    private String email;

    public ShareStoryBody(Long storyId, String email) {
        this.storyId = storyId;
        this.email = email;
    }

    public Long getStoryId() {
        return storyId;
    }

    public String getEmail() {
        return email;
    }
}
class DeleteStoryBody{
    private Long storyId;

    public DeleteStoryBody() {
    }

    public DeleteStoryBody(Long storyId) {
        this.storyId = storyId;
    }

    public Long getStoryId() {
        return storyId;
    }
}

class GetStoriesBody{
    private Integer count;
    private Integer start;

    public GetStoriesBody() {
    }

    public GetStoriesBody(Integer count, Integer start) {
        this.count = count;
        this.start = start;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getStart() {
        return start;
    }
}
