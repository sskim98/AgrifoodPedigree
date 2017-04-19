var httpRequest;
var DPMS_public_key;
var DPMS_private_key;
var DPMS_crt;
var DPMS_signature;

function makeRequest(command) {
    if (window.XMLHttpRequest) { // Mozilla, Safari, ...
        httpRequest = new XMLHttpRequest();
    } else if (window.ActiveXObject) { // IE
        try {
            httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (e) {
            try {
                httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e) { }
        }
    }

    if (!httpRequest) {
        alert('Giving up :( Cannot create an XMLHTTP instance');
        return false;
    }
    httpRequest.onreadystatechange = alertContents;

    if (command == 'keys') {
        var post_data = {
            'msg_type': 'certificate_request',
            'company_info': {
                'country_name': 'KR',
                'state_name': 'Daejeon',
                'locality_name': 'Yousung',
                'organization_name': 'KAIST',
                'organization_unit_name': 'ITC',
                'hostname': 'ped.kaist.ac.kr/sskim98@itc.kaist.ac.kr',
                'email': 'sskim98@itc.kaist.kr'
            }
        };
    }
    else if (command == 'signature') {
        var post_data = { 'msg_type': 'signature_request', 'product_info': 'dpms/signature/xml_data' };
    }
    else if (command == 'verify') {
        var post_data = { 'msg_type': 'verification_request', 'pedigree_info': ped_data };
    }

    httpRequest.open("POST", "http://localhost", true);
    httpRequest.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    httpRequest.send(JSON.stringify(post_data));
}

function alertContents() {
    if (httpRequest.readyState === 4) {
        if (httpRequest.status === 200) {
            //alert(httpRequest.responseText);
            var json = JSON.parse(httpRequest.responseText);
            if (json.msg_type == 'keys') {
                DPMS_public_key = json.public_key;
                document.getElementById("public_key").textContent = json.public_key;
                DPMS_private_key = json.private_key;
                document.getElementById("private_key").textContent = json.private_key;
                DPMS_crt = json.crt;
                document.getElementById("crt").textContent = json.crt;
            }
            else if (json.msg_type == 'signature') {
                alert(json.pedigree_info.pedigree);
                alert(json.pedigree_info.signature);
                ped_data = json.pedigree_info;
                document.getElementById("signature").textContent = json.pedigree_info.signature;
            }
            else if (json.msg_type == 'verification_result') {
                document.getElementById("verify").textContent = json.result;
            }

        } else {
            alert('There was a problem with the request.');
        }
    }
}

var str_xml = "";

$(function(){

    $("#validation_false").hide();
    $("#validation_true").hide();

    $("#btn_go_step2").click(function(){
        $("#btn_step2").click();
    });
    $("#btn_go_step3").click(function(){

        //TODO : Pedigree copy

       $("#btn_step3").click();
    });

    $("#btn_step0").click(function () {
        $("nav").removeClass("navbar-inverse")
        $("#btn_step0").parent().addClass("active");
        $("#btn_step1").parent().removeClass("active");
        $("#btn_step2").parent().removeClass("active");
        $("#btn_step3").parent().removeClass("active");
        $("#step0").show()
        $("#step1").hide()
        $("#step2").hide()
        $("#step3").hide()
    });
    $("#btn_step1").click(function(){
        $("nav").removeClass("navbar-inverse")
        $("#btn_step0").parent().removeClass("active");
        $("#btn_step1").parent().addClass("active");
        $("#btn_step2").parent().removeClass("active");
        $("#btn_step3").parent().removeClass("active");
        $("#step0").hide()
        $("#step1").show()
        $("#step2").hide()
        $("#step3").hide()
    });
    $("#btn_step2").click(function(){
        $("nav").removeClass("navbar-inverse")
        $("#btn_step0").parent().removeClass("active");
        $("#btn_step1").parent().removeClass("active");
        $("#btn_step2").parent().addClass("active");
        $("#btn_step3").parent().removeClass("active");
        $("#step0").hide()
        $("#step1").hide()
        $("#step2").show()
        $("#step3").hide()
    });
    $("#btn_step3").click(function(){
        $("nav").addClass("navbar-inverse")
        $("#btn_step0").parent().removeClass("active");
        $("#btn_step1").parent().removeClass("active");
        $("#btn_step2").parent().removeClass("active");
        $("#btn_step3").parent().addClass("active");
        $("#step0").hide()
        $("#step1").hide()
        $("#step2").hide()
        $("#step3").show()
    });

    $("#btn_step0").click()



    $("#btn_submit_step1").click( function(){
        var data= {
            'msg_type': 'certificate_request',
            'company_info': {
                'country_name': $("#CountryCode").val(),
                'state_name': $("#StateName").val(),
                'locality_name': $("#LocalityName").val(),
                'organization_name': $("#CompanyName").val(),
                'organization_unit_name': $("#OrganizationalUnitName").val(),
                'domainname': $("#DomainName").val(),
                'emailaddress': $("#EmailAddress").val()
            }
        };
        var partner_info = $("#Partner").val().split("\n");
        data.company_info.partner = [];
        for (i = 0; i < partner_info.length; i++) {
            var info;
            var name;
            var addr;
            info = partner_info[i].split(" ");
            name = info[0];
            addr = info[1];
            data.company_info.partner[i] = { 'name': name, 'addr': addr };
        }

        if (($("#CountryCode").val() != "") && ($("#StateName").val() != "") && ($("#LocalityName").val() != "") && ($("#CompanyName").val() != "") && ($("#OrganizationalUnitName").val() != "") && ($("#DomainName").val() != "") && ($("#EmailAddress").val() != "") && ($("#Partner").val() != "")) {
            $.ajax({
                type: "POST",
                url: "",
                data: JSON.stringify(data)
            }).done(function (data) {
                json = JSON.parse(data);
                $("#private_key").text(json.private_key);
                $("#public_key").text(json.public_key);
                $("#certificate").text(json.crt);
                console.log(data)
            }); 
        }
        else {
            alert("input company information");
        }
    });

    $("#btn_submit_delete_info").click(function () {
        var data = {
            'msg_type': 'delete_dpms_info',
        };

            $.ajax({
                type: "POST",
                url: "",
                data: JSON.stringify(data)
            }).done(function (data) {
                $("#CountryCode").val("");
                $("#StateName").val("");
                $("#LocalityName").val("");
                $("#CompanyName").val("");
                $("#OrganizationalUnitName").val("");
                $("#DomainName").val("");
                $("#EmailAddress").val("");
                $("#Partner").text("");
                $("#private_key").text("");
                $("#public_key").text("");
                $("#certificate").text("");
                console.log(data)
            });

    });

    $("#btn_genXML").click( function(){
        
        var json = { 
            initialPedigree:{
                serialNumber: $("#serialNumber").val(),
                productInfo: {
                    drugName: $("#drugName").val(),
                    manufacturer: $("#manufacturer").val(),
                    productCode: $("#productCode").val(),
                    containerSize: $("#containerSize").val()
                },
                itemInfo:{
                    lot: $("#lot").val(),
                    expirationDate:$("#expirationDate").val(),
                    quantity:$("#quantity").val(),
                    itemSerialNumber:$("#itemSerialNumber").val()
                }
            }
        }
        var x2js = new X2JS();
        var xml = x2js.json2xml(json)
        str_xml = new XMLSerializer().serializeToString(xml);
        var beautified_str_xml = vkbeautify.xml(str_xml)

        $("#generated_xml").text(vkbeautify.xml(str_xml));

        $('#generated_xml').each(function(i, block) {
            hljs.highlightBlock(block);
        });

    })

    $("#btn_sign").click( function(){
        
        var data = {
            'msg_type': 'signature_request',
            'product_info': str_xml
        };

        $.ajax({
            type: "POST",
            url: "",
            data: JSON.stringify(data)
        }).done(function(data) {
          
            json = JSON.parse(data)

            $("#received_pedigree").val(json.pedigree_info)
            $("#generated_xml").text(json.pedigree_info);

            console.log(data)

        });
                
    })

    $("#btn_verify").click(function(){
        url = 'dpms/verify/' + $("#received_pedigree").val() + '/' + $("#public_key").text();

        var data = {
            'msg_type': 'verification_request',
            'pedigree_info': $("#received_pedigree").val()
        };

        $.ajax({
            type: "POST",
            url: "",
            data: JSON.stringify(data)
        }).done(function (data) {
            json=JSON.parse(data)
            if( json.result ){
                $("#validation_true").show();
                $("#validation_false").hide();
            }
            else {
                $("#validation_true").hide();
                $("#validation_false").show();
            }
            //$("#verification_result").text(json.result)
            console.log(data)
        });
    })

  
})