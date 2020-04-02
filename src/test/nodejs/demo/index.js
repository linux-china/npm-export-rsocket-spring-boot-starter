const promiseRSocket = require("./rsocketClient").connect("ws://localhost:8080/rsocket")
// const accountService = require("./AccountService").setPromiseRSocket(promiseRSocket);
const accountService = require("@UserService/AccountService").setPromiseRSocket(promiseRSocket);

(async () => {
    let account = await accountService.findById(1);
    console.log(account);
})();