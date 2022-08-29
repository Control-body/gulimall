package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.SearchService;
import com.atguigu.gulimall.search.vo.SearchParam;

import com.atguigu.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/5 19:08
 *
 * @author Control.
 * @since JDK 1.8
 */
@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request){
        String queryString = request.getQueryString();
        searchParam.set_queryString(queryString);
        SearchResult result = searchService.search(searchParam);
        model.addAttribute("result", result);
        System.out.println("------------返回的数据-"+result);
        return "list";
    }
}
