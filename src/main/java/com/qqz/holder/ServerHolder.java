package com.qqz.holder;

import com.qqz.pojo.BasicService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qqz @Date:2022/8/26
 * @usage 全局变量保持器
 */
public class ServerHolder {
    public static final ConcurrentHashMap<String, ConcurrentHashMap<String, BasicService>> machineMapping = new ConcurrentHashMap<>();
}
