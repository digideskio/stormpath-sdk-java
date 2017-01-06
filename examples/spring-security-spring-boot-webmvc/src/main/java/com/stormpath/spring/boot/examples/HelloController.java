/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.boot.examples;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC4.4
 */
@Controller
public class HelloController {

    @Autowired
    private HelloService helloService;

    @Autowired
    AccountResolver accountResolver;

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {

        String name = "World";

        Account account = accountResolver.getAccount(request);
        if (account != null) {
            name = account.getGivenName();
            model.addAttribute(account);
        }

        model.addAttribute("name", name);

        return "hello";
    }

    @RequestMapping("/restricted")
    String restricted(HttpServletRequest request, Model model) {
        Account account = accountResolver.getAccount(request);
        String msg = helloService.sayHello(account);
        model.addAttribute("msg", msg);

        return "restricted";
    }

}
