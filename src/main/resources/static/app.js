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
    });

    var socketQ = new SockJS('/question');
        stompClientQ = Stomp.over(socketQ);
        stompClientQ.connect({
        }
        , function (frame) {
            setConnected(true);
            console.log('Connected: ' + frame);

            stompClient.subscribe('/topic/question', function(message) {
                                                                        showMessage(JSON.parse(message.body).description);
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

function sendQuestion(question) {
    stompClient.send("/app/question", {}, question);
}

function showMessage(message) {
    $("#question").text(message);
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
});

$( document ).ready(function() {
    connect();
});