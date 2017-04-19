var http = require('http');
var fs = require('fs');
var crypto = require('crypto');
var pedigree_DB = {'num_of_pedigree': 0, 'element': [] };
var crypto = require('crypto');
var xml2js = require('xml2js');
var Xparser = new xml2js.Parser({ preserveChildrenOrder: true, explicitArray: false });
var Xbuilder = new xml2js.Builder({ headless: true, renderOpts: { 'pretty': false, 'indent': '', 'newline': '\n' } });
var dpms_http_server_address;
var cpms_ipaddr = "143.248.97.71";
var cpms_port = "8090";
    
this.send = function (msg, callback) {

    if (msg.command == 'generate_pedigree') {
        var element;
        for(var i=0; i<pedigree_DB.num_of_pedigree; i++) {
            element = pedigree_DB.element[i];
            if(msg.id == element.serial_num) {
                callback( { 'command': 'duplicate_code_generation', 'status': 'failure' });
            }
        }
        
        element = { 'serial_num': msg.id, 'pedigree': {} };
        element.pedigree.product_name = msg.product_name;
        element.pedigree.gtin = msg.gtin;
        element.pedigree.serial = msg.serial;
        element.pedigree.final_mod = msg.final_mod;
        element.pedigree.pedigree_type = msg.pedigree_type;
        
        element.pedigree.pedigree_xml = Xbuilder.buildObject(msg.pedigree_xml);
        
        var pem = fs.readFileSync('keys/dpms_private_key.pem');
        var private_key = pem.toString('ascii');
        var sign = crypto.createSign('RSA-SHA256');
        sign.update(element.pedigree.pedigree_xml);  // data from your file would go here
        var signature = sign.sign(private_key, 'base64');

        //console.log('original msg: ' + element.pedigree.pedigree_xml);
        //console.log('signature: ' + signature);

        var temp_xml = element.pedigree.pedigree_xml;
        var signed_pedigree_xml = "<pedigree>" + element.pedigree.pedigree_xml + "<signature>" + signature + "</signature>" + "</pedigree>";
        element.pedigree.pedigree_xml = signed_pedigree_xml;

        ////////////////////////////////////////////////////////////////////////
        var crt = fs.readFileSync('./keys/dpms_public_key.pem');
        var certificate = crt.toString('ascii');
        var verify = crypto.createVerify('RSA-SHA256');
        verify.update(temp_xml);
        var result = verify.verify(certificate, signature, 'base64');


        console.log('original msg: ' + temp_xml);
        console.log('signature: ' + signature);
        console.log('result: ' + result);

        /////////////////////////////////////////////////////////////////////////////

        pedigree_DB.element[pedigree_DB.num_of_pedigree] = element;
        pedigree_DB.num_of_pedigree++;
        this.writeDB();
        cpmshttpRequest({ 'msg_type': 'notify_possession', 'id': msg.id, 'owner': dpms_http_server_address });
        callback({ 'command': 'refresh_pedigree', 'status': 'success', 'pedigree': pedigree_DB });
       
    }
    else if (msg.command == 'export_pedigree') {
        var element;
        var isfound = 'false';
        for (var i = 0; i < pedigree_DB.num_of_pedigree; i++) {
            element = pedigree_DB.element[i];
            if (msg.id == element.serial_num) {
                isfound = 'true';
                var previous_pedigree = Xparser.parseString(element.pedigree.pedigree_xml, function (err, obj) {
                    if (element.pedigree.pedigree_type != "shipped") {
                        element.pedigree.pedigree_type = msg.pedigree_type;
                        element.pedigree.final_mod = msg.final_mod;
                        
                        msg.pedigree_xml.shippedPedigree.pedigree = obj.pedigree;
                        element.pedigree.pedigree_xml = Xbuilder.buildObject(msg.pedigree_xml);

                        var pem = fs.readFileSync('keys/dpms_private_key.pem');
                        var private_key = pem.toString('ascii');
                        var sign = crypto.createSign('RSA-SHA256');
                        sign.update(element.pedigree.pedigree_xml);  // data from your file would go here
                        var signature = sign.sign(private_key, 'base64');
                        var signed_pedigree_xml = "<pedigree>" + element.pedigree.pedigree_xml + "<signature>" + signature + "</signature>" + "</pedigree>";
                        element.pedigree.pedigree_xml = signed_pedigree_xml;

                        pedigree_DB.element[i] = element;
                        
                        
                        writeDB();
                        cpmshttpRequest({ 'msg_type': 'notify_possession', 'id': msg.id, 'owner': dpms_http_server_address });
                        callback({ 'command': 'refresh_pedigree', 'status': 'success', 'pedigree': pedigree_DB });
                    }
                    else {
                        element.pedigree.pedigree_type = msg.pedigree_type;
                        element.pedigree.final_mod = msg.final_mod;

                        msg.pedigree_xml.shippedPedigree.pedigree = obj.pedigree.shippedPedigree.pedigree;
                        element.pedigree.pedigree_xml = Xbuilder.buildObject(msg.pedigree_xml);

                        var pem = fs.readFileSync('keys/dpms_private_key.pem');
                        var private_key = pem.toString('ascii');
                        var sign = crypto.createSign('RSA-SHA256');
                        sign.update(element.pedigree.pedigree_xml);  // data from your file would go here
                        var signature = sign.sign(private_key, 'base64');
                        var signed_pedigree_xml = "<pedigree>" + element.pedigree.pedigree_xml + "<signature>" + signature + "</signature>" + "</pedigree>";
                        element.pedigree.pedigree_xml = signed_pedigree_xml;

                        pedigree_DB.element[i] = element;


                        writeDB();
                        cpmshttpRequest({ 'msg_type': 'notify_possession', 'id': msg.id, 'owner': dpms_http_server_address });
                        callback({ 'command': 'refresh_pedigree', 'status': 'success', 'pedigree': pedigree_DB });
                    }

                    fs.readFile('./keys/dpms_info.txt', { encoding: 'utf8' }, function (err, data) { // 인코딩 utf8로 얻어오고 저장도 동일하게 해야 한다.
                        var dpms_info = JSON.parse(data);
                        var partner = dpms_info.partner;
                        var destination;
                        for (key in partner) {
                            if (partner[key].name == msg.destination) {
                                destination = partner[key].addr;
                            }
                        }
                        destination = destination.split("http://")[1].split(":");
                        var ipaddr = destination[0];
                        var port = destination[1];
                        var options = {
                            host: ipaddr,
                            path: '/',
                            port: port,
                            method: 'POST'
                        };
                        console.log("options");
                        console.log(options);

                        var callback = function (response) {
                            console.log(response);
                            var str = '';
                            response.on('data', function (chunk) {
                                str += chunk;
                            });

                            response.on('end', function () {
                                console.log(str);
                            });
                        }

                        var req = http.request(options, callback);
                        var http_msg = { 'msg_type': 'pedigree_transfer', 'pedigree': element };
                        req.write(JSON.stringify(http_msg));
                        req.end();
                        
                    });
                    return;
                });
            }
        }
        if (isfound == 'false') {
            callback({ 'command': 'not_found', 'status': 'failure' });
        }
        return;
    }
    else if (msg.command == 'pedigree_transfer') {
        var element;
        for (var i = 0; i < pedigree_DB.num_of_pedigree; i++) {
            element = pedigree_DB.element[i];
            if (msg.pedigree.serial_num == element.serial_num) {
                element = msg.pedigree;
                element.pedigree.pedigree_type = 'disabled';
                pedigree_DB.element[i] = element;
                writeDB();
                callback({ 'command': 'refresh_pedigree', 'status': 'success', 'pedigree': pedigree_DB });
                return;
            }
        }
        
        element = msg.pedigree;
        element.pedigree.pedigree_type = 'disabled';
        pedigree_DB.element[pedigree_DB.num_of_pedigree] = element;
        pedigree_DB.num_of_pedigree++;
        writeDB();
        callback({ 'command': 'refresh_pedigree', 'status': 'success', 'pedigree': pedigree_DB });

    }
    else if (msg.command == 'import_pedigree') {
        var element;
        var isfound = 'false';
        for (var i = 0; i < pedigree_DB.num_of_pedigree; i++) {
            element = pedigree_DB.element[i];
            if (msg.id == element.serial_num) {
                isfound = 'true';
                var previous_pedigree = Xparser.parseString(element.pedigree.pedigree_xml, function (err, obj) {
                    console.log(element.pedigree.pedigree_type);
                    if (element.pedigree.pedigree_type == "disabled") {
                        element.pedigree.pedigree_type = msg.pedigree_type;
                        element.pedigree.final_mod = msg.final_mod;

                        msg.pedigree_xml.receivedPedigree.pedigree = obj.pedigree;
                        element.pedigree.pedigree_xml = Xbuilder.buildObject(msg.pedigree_xml);

                        var pem = fs.readFileSync('keys/dpms_private_key.pem');
                        var private_key = pem.toString('ascii');
                        var sign = crypto.createSign('RSA-SHA256');
                        sign.update(element.pedigree.pedigree_xml);  // data from your file would go here
                        var signature = sign.sign(private_key, 'base64');
                        var signed_pedigree_xml = "<pedigree>" + element.pedigree.pedigree_xml + "<signature>" + signature + "</signature>" + "</pedigree>";
                        element.pedigree.pedigree_xml = signed_pedigree_xml;

                        pedigree_DB.element[i] = element;


                        writeDB();
                        cpmshttpRequest({ 'msg_type': 'notify_possession', 'id': msg.id, 'owner': dpms_http_server_address });
                        callback({ 'command': 'refresh_pedigree', 'status': 'success', 'pedigree': pedigree_DB });
                    }
                    else if (element.pedigree.pedigree_type == "received") {
                        element.pedigree.pedigree_type = msg.pedigree_type;
                        element.pedigree.final_mod = msg.final_mod;

                        msg.pedigree_xml.receivedPedigree.pedigree = obj.pedigree.shippedPedigree.pedigree;
                        element.pedigree.pedigree_xml = Xbuilder.buildObject(msg.pedigree_xml);

                        var pem = fs.readFileSync('keys/dpms_private_key.pem');
                        var private_key = pem.toString('ascii');
                        var sign = crypto.createSign('RSA-SHA256');
                        sign.update(element.pedigree.pedigree_xml);  // data from your file would go here
                        var signature = sign.sign(private_key, 'base64');
                        var signed_pedigree_xml = "<pedigree>" + element.pedigree.pedigree_xml + "<signature>" + signature + "</signature>" + "</pedigree>";
                        element.pedigree.pedigree_xml = signed_pedigree_xml;

                        pedigree_DB.element[i] = element;


                        writeDB();
                        cpmshttpRequest({ 'msg_type': 'notify_possession', 'id': msg.id, 'owner': dpms_http_server_address });
                        callback({ 'command': 'refresh_pedigree', 'status': 'success', 'pedigree': pedigree_DB });
                    }
                    console.log("found disabled pedi");
                    return;
                });
            }
        }
        if (isfound == 'false') {
            callback({ 'command': 'not_found', 'status': 'failure' });
        }
        return;
    }
}



var cpmshttpRequest = this.cpmshttpRequest = function (msg) {
    var options = {
        host: cpms_ipaddr,
        path: '/',
        port: cpms_port,
        method: 'POST'
    };

    callback = function (cpms_resp) {
        var str = ''
        cpms_resp.on('data', function (chunk) {
            str += chunk;
        });

        cpms_resp.on('end', function () {
            console.log(str);
        });
    }

    var req = http.request(options, callback);
    req.write(JSON.stringify(msg));
    req.end();
}

this.setAddress = function (addr) {
    dpms_http_server_address = this.dpms_http_server_address = addr;
}

this.getPedigree = function (msg) {
    if (msg.command == 'get_last_pedigree') {
        var element;
        for (var i = 0; i < pedigree_DB.num_of_pedigree; i++) {
            element = pedigree_DB.element[i];
            if (msg.id == element.serial_num) {
                return element.pedigree.pedigree_xml
            }
        }
        return "not_found";
    }
}

var readDB = this.readDB = function () {
    fs.readFile('./pedigree/db.txt', { encoding: 'utf8' }, function (error, data) { // 인코딩 utf8로 얻어오고 저장도 동일하게 해야 한다.
        if (error) {
            console.error("read error:  " + error.message);
        } else {
            pedigree_DB = JSON.parse(data);
        }
    });
}

var writeDB = this.writeDB = function () {
    fs.writeFile('./pedigree/db.txt', JSON.stringify(pedigree_DB), { encoding: 'utf8' }, function (error) {
        if (error) {
            console.error("write error:  " + error.message);
        } else {
            console.log("Successful Write to DB!");
        }
    });
}

this.initDB = function () {
    fs.readFile('./pedigree/db.txt', { encoding: 'utf8' }, function (error, data) { // 인코딩 utf8로 얻어오고 저장도 동일하게 해야 한다.
        if (error) {
            console.error("read error:  " + error.message);
        } else {
            pedigree_DB = JSON.parse(data);
        }

        fs.writeFile('./pedigree/db.txt', JSON.stringify(pedigree_DB), { encoding: 'utf8' }, function (error) {
            if (error) {
                console.error("write error:  " + error.message);
            } else {
                console.log("Successful Write to DB!");
            }
        });
    });
}

this.initDB();