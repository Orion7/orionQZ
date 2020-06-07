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

    });
}

function sendAnswer() {
     stompClient.send("/app/answer", {
     }, $("#answer").val());
}

function sendQuestion(question) {
    stompClient.send("/app/question", {}, question);
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
        + "<input type='button' value='Approve' ansid='" + ans[i].id + "' class='approve'>" +
        "<input type='button' value='Reject' ansid='" + ans[i].id + "' class='approve'>" +
        "</td></tr>")
    }

    $( ".approve" ).click(function() {
              processAnswer(
                  JSON.stringify({
                           'answerId': $(this).attr('ansid'),
                           'approved' : $(this).val() == "Approve"
                       })
              );
        });
}

$(function () {

    $( ".question" ).click(function() {



        sendQuestion(
            JSON.stringify({
                     'id': $(this).attr('qid'),
                     'description' : $(this).attr('description'),
                     'cost': $(this).attr('cost')
                 })
        );
    });

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