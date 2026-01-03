const http = require('http');
const fs = require('fs');
const url = require('url');

const PORT = 3000;
const DB_FILE = './licenses.json';

const server = http.createServer((req, res) => {
    const reqUrl = url.parse(req.url, true);

    if (reqUrl.pathname === '/verify-license' && req.method === 'GET') {
        const key = reqUrl.query.key;
        console.log(`[${new Date().toISOString()}] Checking Key: ${key}`);

        if (!key) {
            res.writeHead(400, { 'Content-Type': 'text/plain' });
            res.end('Missing key parameter');
            return;
        }

        // Read DB fresh on every request
        fs.readFile(DB_FILE, 'utf8', (err, data) => {
            if (err) {
                console.error("Error reading DB:", err);
                res.writeHead(500);
                res.end("Internal Server Error");
                return;
            }

            try {
                const licenses = JSON.parse(data);
                const license = licenses[key];

                if (license && license.active === true) {
                    console.log(`✅ Key Valid: ${key}`);
                    res.writeHead(200, { 'Content-Type': 'application/json' });
                    res.end(JSON.stringify({ status: 'VALID', ...license }));
                } else {
                    console.log(`❌ Key Invalid or Inactive: ${key}`);
                    res.writeHead(403, { 'Content-Type': 'application/json' });
                    res.end(JSON.stringify({ status: 'INVALID', reason: 'Key not found or inactive' }));
                }
            } catch (parseErr) {
                console.error("Error parsing DB:", parseErr);
                res.writeHead(500);
                res.end("Internal DB Error");
            }
        });
    } else {
        res.writeHead(404);
        res.end("Not Found");
    }
});

server.listen(PORT, () => {
    console.log(`🔐 License Authority Server running on port ${PORT}`);
    console.log(`📂 Database: ${DB_FILE}`);
    try {
        const data = fs.readFileSync(DB_FILE, 'utf8');
        const licenses = JSON.parse(data);
        const key = 'FISH_BUSINESS_WA_SERVICE_PROVIDER';
        const isActive = licenses[key] ? licenses[key].active : 'NOT FOUND';
        if (isActive === true) {
            console.log(`✅ status: ${isActive}`);
        } else {
            console.log(`❌ status: ${isActive}`);
        }
    } catch (e) {
        console.log('🛑 status: ERROR reading DB');
    }

});
