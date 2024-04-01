package br.com.microsservice.statefulanyapi.core.controller;

import br.com.microsservice.statefulanyapi.core.dto.AnyResponse;
import br.com.microsservice.statefulanyapi.core.service.AnyService;
import lombok.AllArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/resource")
public class AnyController {

    private final AnyService service;

    @GetMapping
    public AnyResponse getResource(@RequestHeader String accessToken) throws AuthenticationException {
        return service.getData(accessToken);
    }
}
