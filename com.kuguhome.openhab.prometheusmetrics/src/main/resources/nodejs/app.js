const express = require('express')
const app = express()

app.get('/metrics', (req, res) => res.send('HTTP passthrough'))

app.listen(9100, () => console.log('Node.js app listening on port 9100.'))

