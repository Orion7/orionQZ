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
                                                                    showMessage(message.body);
                                                                })

        stompClient.subscribe('/topic/question', function(message) {
                                                                     start(message.body);
                                                                            })

         stompClient.subscribe('/topic/ready', function(message) {
            startAudio();
         })
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
     }, JSON.stringify({
         'name': $("#name").val(),
         'toUser' : $("#name").val()
     }));
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
    $( "#connect" ).click(function() { connect(); });
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
    connect();
});