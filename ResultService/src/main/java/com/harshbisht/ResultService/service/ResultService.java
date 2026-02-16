package com.harshbisht.ResultService.service;

import com.harshbisht.ResultService.repository.AnswerRepository;
import com.harshbisht.ResultService.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final AnswerRepository answerRepository;


}
