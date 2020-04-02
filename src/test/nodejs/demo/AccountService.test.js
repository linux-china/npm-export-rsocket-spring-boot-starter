const promiseRSocket = require("./rsocketClient").connect("ws://localhost:8080/rsocket")
const accountService = require("./AccountService").setPromiseRSocket(promiseRSocket);

test('findNickById', async () => {
    let account = await accountService.findById(1);
    console.log(account);
});