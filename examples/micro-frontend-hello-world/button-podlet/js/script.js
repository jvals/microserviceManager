const Browser = require('@podium/browser');

const messageBus = new Browser.MessageBus();

function getRandomColor() {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function handleButtonClick() {
    messageBus.publish('color-channel', 'header-topic', getRandomColor())
}

document.getElementById('update-color-button').addEventListener('click', handleButtonClick)