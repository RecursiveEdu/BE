package com.recursive.edu.backend.service.impl;

import com.recursive.edu.backend.controller.request.AddTagRequest;
import com.recursive.edu.backend.controller.response.SimilarTags;
import com.recursive.edu.backend.model.postgres.Tag;
import com.recursive.edu.backend.repository.TagRepository;
import com.recursive.edu.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author PrantikGuha
 * CreatedAt: {22-11-2025}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();

    @Override
    public SimilarTags findSimilarTags(String searchTag) {
        try {
            String normalized = normalize(searchTag);
            List<Tag> matches = tagRepository.findSimilarTags(normalized);
            if (!CollectionUtils.isEmpty(matches)) {
                Set<String> bestMatches = findBestMatches(normalized, matches);
                if (!CollectionUtils.isEmpty(bestMatches)) {
                    return SimilarTags.builder()
                            .similarTags(bestMatches.stream().toList())
                            .build();
                }
            }
        } catch (Exception exp) {
            log.error("Exception in TagServiceImpl.findSimilarTags: {}", exp.getMessage(), exp);
            throw exp;
        }
        return SimilarTags.builder()
                .similarTags(Collections.emptyList())
                .build();
    }

    @Override
    public String addTag(AddTagRequest addTagRequest) {
        try {
            String normalized = normalize(addTagRequest.getTag());
            Optional<Tag> existingTag = tagRepository.findByNameIgnoreCase(normalized);
            if (existingTag.isEmpty()) {
                Tag tag = Tag.builder().name(normalized).build();
                tagRepository.saveAndFlush(tag);
                return "Tag added successfully.";
            } else {
                String message = String.format("Tag: %s already exists", normalized);
                log.trace(message);
                return message;
            }
        } catch (Exception exp) {
            log.error("Exception in TagServiceImpl.findSimilarTags: {}", exp.getMessage(), exp);
            throw exp;
        }
    }

    private String normalize(String tag) {
        return tag.trim().toLowerCase();
    }

    private Set<String> findBestMatches(String keyword, List<Tag> candidates) {
        Set<String> matched = new HashSet<>();

        int threshold = 3; // Levenshtein distance tolerance

        for (Tag tag : candidates) {
            String name = tag.getName().toLowerCase();

            // 1. Exact
            if (name.equals(keyword)) {
                matched.add(tag.getName());
                continue;
            }

            // 2. Contains / Partial match
            if (name.contains(keyword) || keyword.contains(name)) {
                matched.add(tag.getName());
                continue;
            }

            // 3. Fuzzy match using Levenshtein
            int distance = levenshtein.apply(keyword, name);
            if (distance <= threshold) {
                matched.add(tag.getName());
            }
        }
        return matched;
    }
}
