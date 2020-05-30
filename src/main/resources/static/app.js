var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/greeting');
    stompClient = Stomp.over(socket);
    stompClient.connect({
    }
    , function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue/reply', function(message) {
                                                            showMessage(JSON.parse(message.body));
                                                        });
        stompClient.subscribe('/topic/info', function(message) {
                                                                    refreshScoreTable(JSON.parse(message.body));
                                                                })
        stompClient.subscribe('/topic/question', function(message) {
                                                                     start(message.body);
                                                                        })

        sendName();
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
     stompClient.send("/app/greeting", {
     }, $("#nickname").val());
}

function sendReady() {
  stompClient.send("/app/ready", {
  }, "ready");
}

function sendQuestion(question) {
    stompClient.send("/app/question", {}, question);
}

function showMessage(message) {
    $("#question").text(message.description);
}

function refreshScoreTable(scores) {
    $("#scoretable").empty();
    for(i = 0; i < scores.length; i++) {
        $("#scoretable").append("<tr><td>" + (i + 1) + "</td><td>" + scores[i].nickname + "</td><td>" + scores[i].score + "</td></tr>")
    }
}

function startAudio() {
    var audio = $("#player");
    audio[0].play();
}

function start(message) {
        message = JSON.parse(message)
        $("#question").text(message.description);

        var audio = $("#player");
        $("#playerSrc").attr('src', '/files/' + message.id)
        audio[0].pause();
        audio[0].load();
        audio[0].oncanplaythrough = audio[0].play();
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( ".close" ).click(function() {
        $("#shadow").hide();
        $("#sendBtnDiv").show();
        connect();
    });

    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });

    $( ".question" ).click(function() {

        sendQuestion(
            JSON.stringify({
                     'id': $(this).attr('qid'),
                     'description' : $(this).attr('description'),
                     'cost': $(this).attr('cost')
                 })
        );
    });

    $( "#upload" ).click(function() {
        var f = document.getElementById('file').files[0];
        var file = {name: f.name, summary: 'kek'};

        jQuery.ajax ({
            url: '/files/',
            type: "POST",
            data: JSON.stringify(file),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            success: function(data, textStatus, response){
                var fd = new FormData();
                fd.append('file', f);

                jQuery.ajax ({
                            url: response.getResponseHeader("Location"),
                            type: "PUT",
                            data: fd,
                            processData: false,
                            contentType: false,
                            success: function(data, textStatus, response){
                            }
                        });
            }
        });


    })
});

$( document ).ready(function() {
    if($("#sendBtnDiv").is(":visible")) {
        connect();
    }
});