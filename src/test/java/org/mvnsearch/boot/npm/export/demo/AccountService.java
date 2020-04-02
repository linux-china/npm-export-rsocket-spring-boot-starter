package org.mvnsearch.boot.npm.export.demo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * account service
 *
 * @author linux_china
 */
public interface AccountService {

    Mono<Account> findById(Integer id);

    Mono<Account> findByNick(String nick);

    Flux<Account> findAll();

}
