package com.recursive.edu.backend.service;

import com.recursive.edu.backend.controller.request.AddTagRequest;
import com.recursive.edu.backend.controller.response.SimilarTags;

/**
 * @author PrantikGuha
 * CreatedAt: {22-11-2025}
 */
public interface TagService {
    SimilarTags findSimilarTags(String searchTag);
    String addTag(AddTagRequest addTagRequest);
}
