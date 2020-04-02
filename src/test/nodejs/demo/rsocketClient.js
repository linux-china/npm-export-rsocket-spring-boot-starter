const {BufferEncoders, MESSAGE_RSOCKET_COMPOSITE_METADATA, RSocketClient} = require('rsocket-core');
const {ReactiveSocket} = require('rsocket-types')
const RSocketWebSocketClient = require('rsocket-websocket-client').default;
let isBrowser = new Function('try {return this===window;}catch(e){ return false;}');

// rsocket connection settings
const keepAlive = 60000;
const lifetime = 180000;
const dataMimeType = 'application/json';
const metadataMimeType = MESSAGE_RSOCKET_COMPOSITE_METADATA.string;

/**
 * construct web socket transport
 * @param {string} rsocketUri
 * @return {RSocketWebSocketClient}
 */
function constructWebSocketTransport(rsocketUri) {
    let rsocketWsCreator;
    if (isBrowser()) {
        rsocketWsCreator = (uri) => new WebSocket(uri);
    } else {
        let WebSocket = require('ws');
        rsocketWsCreator = (uri) => new WebSocket(uri);
    }
    return new RSocketWebSocketClient(
            {
                url: rsocketUri,
                wsCreator: rsocketWsCreator
            },
            BufferEncoders,
    )
}

/**
 * make Promise RSocket
 * @return {Promise<ReactiveSocket>}
 */
function makePromiseRSocket(rsocketUri) {
    let rsocketClient = new RSocketClient({
        setup: {
            keepAlive,
            lifetime,
            dataMimeType,
            metadataMimeType,
        },
        transport: constructWebSocketTransport(rsocketUri),
    });
    return new Promise(function (resolve, reject) {
        rsocketClient.connect().then(rsocket => {
            resolve(rsocket);
        }, error => {
            reject(error);
        });
    });
}

module.exports = {
    connect: makePromiseRSocket
} 

