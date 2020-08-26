const Browser = require('@podium/browser');

const messageBus = new Browser.MessageBus();

messageBus.subscribe('color-channel', 'header-topic', event => {
    const header = document.getElementById("hello-world-header");
    header.style.color = event.payload
})
