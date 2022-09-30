package com.wangzaiplus.test.service;

import com.wangzaiplus.test.common.ServerResponse;
import com.wangzaiplus.test.dto.TestDTO;
import com.wangzaiplus.test.pojo.Mail;

public interface TestService {

    ServerResponse test(TestDTO testDTO);

    ServerResponse testIdempotence(String id);

    ServerResponse accessLimit();

    ServerResponse send(Mail mail);
}
