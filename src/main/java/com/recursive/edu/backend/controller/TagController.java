package com.recursive.edu.backend.controller;

import com.recursive.edu.backend.controller.request.AddTagRequest;
import com.recursive.edu.backend.helper.ControllerHelper;
import com.recursive.edu.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

/**
 * @author PrantikGuha
 * CreatedAt: {22-11-2025}
 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final ControllerHelper controllerHelper;
    private final @Qualifier("addTagValidator") Validator addTagValidator;

    @GetMapping("/search")
    public ResponseEntity<?> resolveTags(@RequestParam(name = "q") String searchTag) {
        return controllerHelper.execute(() -> tagService.findSimilarTags(searchTag));
    }

    @PostMapping
    public ResponseEntity<?> addTag(@RequestBody AddTagRequest addTagRequest, BindingResult bindingResult) {
        return controllerHelper.validateAndExecute(addTagValidator, bindingResult, addTagRequest, () -> tagService.addTag(addTagRequest));
    }
}
