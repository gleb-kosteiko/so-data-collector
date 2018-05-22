package com.soc;

import com.soc.service.UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SoDataCollector class.
 * <p>
 * Date: May 21, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class SoDataCollector {
    private static final String PARAM_VALUE_SEPARATOR = "=";
    private static final String KEY_PARAM = "key";
    private static Map<String, String> namedParams = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Collecting is started...");
        collectArgs(args);
        UserService userService = new UserService(namedParams.get(KEY_PARAM));
        userService.collectUsers();
        System.out.println("Collecting is finished!");
    }

    private static void collectArgs(String[] args) {
        Set<String> params = new HashSet<>(Arrays.asList(args));
        for (String s : params) {
            String[] paramValue = s.split(PARAM_VALUE_SEPARATOR);
            namedParams.put(paramValue[0], paramValue[1]);
        }
    }
}
