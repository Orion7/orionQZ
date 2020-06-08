var stompClient = null;

function connect() {
    var socket = new SockJS('/greeting');
    stompClient = Stomp.over(socket);
    stompClient.connect({
    }
    , function (frame) {
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
        stompClient.subscribe('/topic/answer', function(message) {
                                                                             start(message.body);
                                                })

        sendName();
    });
}

function sendName() {
     stompClient.send("/app/greeting", {
     }, $("#nickname").val());
}

function sendAnswer(answer) {
     stompClient.send("/app/answer", {
     }, answer == "" ? "Нет ответа" : answer);
}

function sendReady() {
  stompClient.send("/app/ready", {
  }, "ready");
}

function sendQuestion(question) {
    stompClient.send("/app/question", {}, question);
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
        $(".shadow").hide();
        $(".score-div").show();
        $("#sendBtnDiv").show();
        connect();
    });

    $( ".close-answer" ).click(function() {
        $(".shadow").hide();
        $(".score-div").show();
        $("#sendBtnDiv").show();
        sendAnswer($("#answer").val())
    });

    $( "#disconnect" ).click(function() { disconnect(); });

    $( "#send" ).click(function() {
        sendAnswer("Нажал кнопку")
        $(".score-div").hide();
        $("#sendBtnDiv").hide();
        $("#answer-div").show();
    });

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
    if($("#sendBtnDiv").is(":visible")) {
        connect();
    }
});