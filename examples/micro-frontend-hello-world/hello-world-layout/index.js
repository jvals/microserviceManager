const express = require('express');
const Layout = require('@podium/layout');

const app = express();
const DEFAULT_TITLE_PODLET_ADDR = "http://localhost:8001"
const DEFAULT_BUTTON_PODLET_ADDR = "http://localhost:8002"
const {TITLE_PODLET_ADDR = DEFAULT_TITLE_PODLET_ADDR, BUTTON_PODLET_ADDR = DEFAULT_BUTTON_PODLET_ADDR} = process.env;

const layout = new Layout({
    name: 'hello-world-layout',
    pathname: '/',
    logger: console
});

const titlePodlet = layout.client.register({
    name: 'title-podlet',
    uri: `${TITLE_PODLET_ADDR}/manifest.json`,
});

const buttonPodlet = layout.client.register({
    name: 'button-podlet',
    uri: `${BUTTON_PODLET_ADDR}/manifest.json`,
});

app.use(layout.middleware());

app.get('/', async (req, res) => {
    const incoming = res.locals.podium;

    const response = await Promise.all([
            titlePodlet.fetch(incoming),
            buttonPodlet.fetch(incoming)
        ]
    );
    incoming.podlets = response
    const [titleContent, buttonContent] = response
    incoming.view.title = 'Micro Hello World';


    let content = layout.render(incoming, `
        <div>${titleContent}</div>
        <div>${buttonContent}</div>
    `);

    // Hack for resolving addresses from browser to container.
    // The podlet address inside the containers can't be localhost because of network isolation. A solution would be to set
    // network_mode in docker to "host", but that is not supported for docker-for-mac. See https://github.com/docker/for-mac/issues/1031
    // Also, the podlet address can't be the service name because that isn't resolvable by the browser.
    // Therefore we need a mix between both approaches, which involves hacking the default document template from podium.
    content = content
        .replace(TITLE_PODLET_ADDR, DEFAULT_TITLE_PODLET_ADDR)
        .replace(BUTTON_PODLET_ADDR, DEFAULT_BUTTON_PODLET_ADDR)

    res.send(content);
});

app.listen(8000);