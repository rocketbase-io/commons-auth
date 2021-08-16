package org.company.sample.controller;

import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/sample")
@Transactional
@RequiredArgsConstructor
public class SampleController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String hello(@RequestParam(name = "name", required = false) String name) {
        return String.format("hello: %s", Nulls.notNull(name, "world"));
    }

}
