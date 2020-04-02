npm-export-rsocket-spring-boot-starter
======================================

A Spring Boot starter, generates npm package for Node.js and Browser to call Spring Boot RSocket Service.

# Features

* Generate rsocket-js stub to call remote Spring Boot RSocket Service
* JSDoc support for code completion

# How to use?


### Create RSocket Service in Spring Boot

* Add spring-boot-starter-rsocket in pom.xml

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-rsocket</artifactId>
</dependency>
```

* Add rsocket configuration in application.properties

```
spring.rsocket.server.mapping-path=/rsocket
spring.rsocket.server.transport=websocket
```

* Create RSocket Service

```
@Controller
@MessageMapping("org.mvnsearch.user.AccountService")
public class AccountServiceImpl implements AccountService {
    @Override
    @MessageMapping("findById")
    public Mono<Account> findById(Integer id) {
        return Mono.just(new Account(id, "nick:" + id));
    }

    @Override
    @MessageMapping("findByNick")
    public Mono<Account> findByNick(String nick) {
        return Mono.just(new Account(1, nick));
    }
}
```

### Call RSocket Service from Node or Browser

* Include dependency in your package.json and run "yarn install"

```
 "dependencies": {
    "@UserService/AccountService": "http://localhost:8080/npm/@UserService/AccountService"
  }
```

* Call service api in your JS code:

```
const promiseRSocket = require("./rsocketClient").connect("ws://localhost:8080/rsocket")
const accountService = require("@UserService/AccountService").setPromiseRSocket(promiseRSocket);

(async () => {
    let account = await accountService.findById(1);
    console.log(account);
})();
```

rsocketClient.js, please refer https://github.com/linux-china/npm-export-rsocket-spring-boot-starter/blob/master/src/test/nodejs/demo/rsocketClient.js

# References

* RSocket: https://rsocket.io/
* RSocket-JS: https://github.com/rsocket/rsocket-js
* Spring RSocket: https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket
* Apache Commons Compress: http://commons.apache.org/proper/commons-compress/
* JSDoc 3: https://jsdoc.app/
