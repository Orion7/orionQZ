var stompClient = null;
function connect() {
    var socket = new SockJS('/greeting');
    stompClient = Stomp.over(socket);
    stompClient.connect({
    }
    , function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/answers', function(message) {
            refreshAnswerTable(JSON.parse(message.body));
        })

        stompClient.send("/app/getAnswers", {}, '');
    });
}

function sendAnswer() {
     stompClient.send("/app/answer", {
     }, $("#answer").val());
}

function sendQuestion(question) {
    stompClient.send("/app/question", {}, question);
}

function refresh() {
    stompClient.send("/app/refresh", {}, null);
}

function processAnswer(answer) {
    stompClient.send("/app/processAnswer", {}, answer);
}

function showMessage(message) {
    $("#question").text(message.description);
}

function refreshAnswerTable(ans) {
    $("#answertable").empty();
    for(i = 0; i < ans.length; i++) {
        $("#answertable").append("<tr><td>"
        + (ans[i].date.split("T")[1]).substring(0,11) +
        "</td><td>"
        + ans[i].user.nickname +
        "</td><td>"
        + ans[i].answer +
        "</td><td>"
        + ans[i].question.description +
        "</td><td>"
        + "<input type='text' value='2'  ansid='" + ans[i].id + "' class='margin-left-3' style='width: 30px'>"
        + "<input type='button' value='++' action='0' ansid='" + ans[i].id + "' class='approve margin-left-3'>" +
        "<input type='button' value='+' action='1' ansid='" + ans[i].id + "' class='approve margin-left-3'>" +
        "<input type='button' value='-' action='2' ansid='" + ans[i].id + "' class='approve margin-left-3'>" +
        "<input type='button' value='--' action='3' ansid='" + ans[i].id + "' class='approve margin-left-3'>" +
        "</td></tr>")
    }

    $( ".approve" ).click(function() {
              processAnswer(
                  JSON.stringify({
                           'answerId': $(this).attr('ansid'),
                           'approved' : $(this).attr('action'),
                           'cost' : $(this).siblings('input[type=text]').first().val()
                       })
              );
        });
}

$(function () {

    $( ".question" ).click(function() {
        $(".question").removeClass("active");
        $(this).addClass("active");

        sendQuestion(
            JSON.stringify({
                     'id': $(this).attr('qid'),
                     'description' : $(this).attr('description'),
                     'cost': $(this).attr('cost'),
                     'gameId': $(this).attr('gameid')
                 })
        );
    });

    $( "#stop" ).click(function() {
            $(".question").removeClass("active");

            sendQuestion(
                JSON.stringify({
                    'gameId': $('.question').attr('gameid')
                })
            );
        });

    $( "#refresh" ).click(refresh);

    $( ".approve" ).click(function() {
          processAnswer(
              JSON.stringify({
                       'id': $(this).attr('ansid'),
                       'approve' : $(this).val() == "Approve"
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