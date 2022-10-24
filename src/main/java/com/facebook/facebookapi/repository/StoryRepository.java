package com.facebook.facebookapi.repository;

import com.facebook.facebookapi.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story,Long> {
    public static final String FIND_STORIES ="SELECT *  FROM story s LEFT JOIN story_share_with w ON s.id = w.story_id" +
            " WHERE w.share_with = ?1 " +
            "OR s.type = ?2 OR s.user_id = ?3  " +
            "GROUP BY id " +
            "LIMIT ?4 OFFSET ?5";
    @Query(value = FIND_STORIES,
    nativeQuery = true)
    List<Story> findAllStories(String email,String type, Long id, Integer limit, Integer page);
}
