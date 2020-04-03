import * as rsocketClient from "./rsocketClient"
import accountService from "@UserService/AccountService"

let promiseRSocket = rsocketClient.connect("ws://localhost:8080/rsocket");
accountService.setPromiseRSocket(promiseRSocket)

accountService.findById(1).then(account => {
    console.log(account.nick)
});
