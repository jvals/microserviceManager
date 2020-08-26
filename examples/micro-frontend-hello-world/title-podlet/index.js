const express = require('express');
const Podlet = require('@podium/podlet');
const cors = require('cors')
const browserify = require('browserify-middleware')


const app = express();

const podlet = new Podlet({
    name: 'title-podlet',
    version: '1.0.0',
    pathname: '/',
    content: '/',
    fallback: '/fallback',
    development: true,
    logger: console
});

app.use(podlet.middleware());
app.use(cors())

app.use('/js', browserify(__dirname + '/js'))

podlet.js({
    value: '/js/script.js',
    defer: true
});

app.get(podlet.content(), (req, res) => {
    res.status(200).send(`
        <div>
            <h1 id="hello-world-header">
                Hello World
            </h1>
        </div>
    `);
});

app.get(podlet.manifest(), (req, res) => {
    res.status(200).send(podlet);
});

app.listen(8001);
