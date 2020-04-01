package org.mvnsearch.boot.npm.export.demo;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * account service implementation with RSocket exposed
 *
 * @author linux_china
 */
@Controller
@MessageMapping("org.mvnsearch.user.AccountService")
public class AccountServiceImpl implements AccountService {
    @Override
    @MessageMapping("findById")
    public Mono<Account> findById(Integer id) {
        return Mono.just(new Account(id, "nick:" + id));
    }

    @Override
    @MessageMapping("findAll")
    public Flux<Account> findAll() {
        return Flux.just(new Account(1, "Jackie"), new Account(2, "Tom"));
    }

    @ConnectMapping()
    public void handle(RSocketRequester requester) {
        System.out.println("connect mapping");
    }
}
