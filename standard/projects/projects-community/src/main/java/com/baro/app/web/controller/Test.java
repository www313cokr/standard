package com.baro.app.web.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class Test {
	
	@RequestMapping(value="/test")
	public void test(ModelMap map, HttpServletRequest request){
		map.addAttribute("test", "테스트입니다.");
	}

}
