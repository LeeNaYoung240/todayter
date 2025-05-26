package com.todayter.domain.comment.service;

import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import com.todayter.global.filter.BadWordFilter;
import com.todayter.global.filter.BadWordRegexFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentFilterService {

    private final BadWordFilter badWordFilter;
    private final BadWordRegexFilter badWordRegexFilter;

    public void validateCommentContent(String content) {

        if (badWordFilter.containsBadWord(content)) {
            throw new CustomException(ErrorCode.BAD_WORD_DETECTED);
        }

        if (badWordRegexFilter.containsBadWord(content)) {
            throw new CustomException(ErrorCode.BAD_WORD_DETECTED);
        }
    }
}
