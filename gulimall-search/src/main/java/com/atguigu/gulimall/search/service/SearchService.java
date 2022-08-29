package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/5 19:15
 *
 * @author Control.
 * @since JDK 1.8
 */
public interface SearchService {
    /**
     * 检索所有参数
     */
    SearchResult search(SearchParam Param);
}
