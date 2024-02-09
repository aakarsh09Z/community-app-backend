'use strict';

document.querySelector('#welcomeForm').addEventListener('submit', connect, true)
document.querySelector('#dialogueForm').addEventListener('submit', sendMessage, true)

var stompClient = null;
var name = null;
var channelId="941885628981379073";
function connect(event) {
    name = document.querySelector('#name').value.trim();

    if (name) {
        document.querySelector('#welcome-page').classList.add('hidden');
        document.querySelector('#dialogue-page').classList.remove('hidden');

        var socket = new SockJS('/websocketApp');
        // var headers = {
        //     'Authorization': `Bearer ${token}`
        // };
        stompClient = Stomp.over(socket);
        // stompClient.connect({"Authorization": "Bearer " + token}, function(frame) {
        stompClient.connect({}, connectionSuccess);
    }
    event.preventDefault();
}
function connectionSuccess() {
    stompClient.subscribe(`/topic/${channelId}`, onMessageReceived);
    stompClient.send(`/app/chat.newUser/${channelId}`, {}, JSON.stringify({
        sender : name,
        content: 'JOIN',
        type : 'newUser',
        token : name
    }))

}

function sendMessage(event) {
    var messageContent = document.querySelector('#chatMessage').value.trim();

    if (messageContent && stompClient) {
        var chatMessage = {
            sender : name,
            content : document.querySelector('#chatMessage').value,
            type : 'CHAT',
            token: name,
            id: channelId
        };

        stompClient.send("/app/chat.sendMessage/"+channelId, {}, JSON
            .stringify(chatMessage));
        document.querySelector('#chatMessage').value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    if (message.errorMessage) {
        console.error('Error received from server:', message.errorMessage);
        return;
    }


    var messageElement = document.createElement('li');

    if (message.type === 'newUser') {
        messageElement.classList.add('event-data');
        message.content = message.sender + ' has joined the chat';
    } else if (message.type === 'Leave') {
        messageElement.classList.add('event-data');
        message.content = message.sender + ' has left the chat';
    } else {
        messageElement.classList.add('message-data');

        var element = document.createElement('i');
        var text = document.createTextNode(message.sender[0]);
        element.appendChild(text);

        messageElement.appendChild(element);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    document.querySelector('#messageList').appendChild(messageElement);
    document.querySelector('#messageList').scrollTop = document
        .querySelector('#messageList').scrollHeight;

}