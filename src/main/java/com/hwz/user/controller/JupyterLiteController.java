package com.hwz.user.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JupyterLiteController {

    @GetMapping("/lite/lab")
    public String redirectToLab(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (requestUri != null && requestUri.endsWith("/")) {
            return "forward:/lite/lab/index.html";
        }
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            return "redirect:/lite/lab/?" + query;
        }
        return "redirect:/lite/lab/";
    }
}
