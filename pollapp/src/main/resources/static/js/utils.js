function initDropDown() {
    const dropdown = document.querySelectorAll('.dropdown-trigger');
    M.Dropdown.init(dropdown, {
        inDuration: 300,
        outDuration: 150,
        coverTrigger: false
    });
}

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');

    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' '){
            c = c.substring(1);
        }

        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }

    return "";
}

function showHomePage() {
    var jwt = getCookie('jwt');

    if(jwt != "") {
        window.location.replace("http://localhost:8080/index.html");
    }
}

function showLoginPage() {
    window.location.replace("http://localhost:8080/login.html");
}

function isLoggedIn() {
    var jwt = getCookie("jwt");

    if(jwt != "") {
        return true;
    }
    return false;
}

function logout() {
    if(getCookie("jwt")) {
        document.cookie = "jwt=;path=;domain=;expires=Thu, 01 Jan 1970 00:00:01 GMT";
        window.location.href = "http://localhost:8080/index.html";
    }
}

const CSS_COLOR_NAMES = [
    "DarkBlue",
    "DarkCyan",
    "DarkGoldenRod",
    "DarkGrey",
    "DarkGreen",
    "DarkOliveGreen",
    "DarkOrange",
    "DarkOrchid",
    "DarkTurquoise",
    "DeepSkyBlue",
    "FireBrick",
    "ForestGreen",
    "LightBlue",
    "LightCoral",
    "LightCyan",
    "LightGoldenRodYellow",
    "LightGray",
    "LightGrey",
    "LightGreen",
    "LightPink",
    "LightSeaGreen",
    "LightSalmon",
    "LightSkyBlue",
    "LightSlateGray",
    "LightSlateGrey",
    "LightSteelBlue",
];

function addPollActiveNotVoted(poll, loggedIn) {
    var voteBtnState = loggedIn ? "" : "disabled";

    // format createAt datetime
    var createdAtDate = new Date(poll.createdAt);
    var options = { day: "numeric", month: "short", year: "numeric" };
    var createdAtDateFromatted = createdAtDate.toLocaleDateString("default", options);
    var createdAtTimeFormatted = createdAtDate.getHours() + ":" + createdAtDate.getMinutes();

    // calculate time left for poll to end
    var endsAtDate = new Date(poll.endsAt);
    var now = new Date();

    var seconds = Math.floor((endsAtDate - (now)) / 1000);
    var minutes = Math.floor(seconds / 60);
    var hours = Math.floor(minutes / 60);
    var days = Math.floor(hours / 24);

    hours = hours-(days * 24);
    minutes = minutes - (days * 24 * 60) - (hours * 60);
    seconds = seconds - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);

    var pollTimeLeft = "";
    if(days > 0) {
        pollTimeLeft += days + "d ";
    }
    if(hours > 0) {
        pollTimeLeft += hours + "h ";
    }
    if(minutes > 0 && days == 0) {
        pollTimeLeft += minutes + "m ";
    }

    // create poll card
    var pollHtml = ``;

    var pollHtmlHeader =
    `<div id="poll_card_${poll.id}" class="row">
        <div class="col s8 m8 offset-s2 offset-m2">
            <div class="card">
                <div class="card-content">
                    <div class="row">
                        <div class="col s1 m1">
                            <div class="card-profile-avatar" id="card_profile_avatar_${poll.id}"></div>
                        </div>
                        <div class="col s11 m4">
                            <div class="card-header">
                                <span class="card-profile-name">${poll.createdBy.name}</span>
                                <span class="card-profile-username">
                                    <a href="profile.html?user=${poll.createdBy.username}">@${poll.createdBy.username}</a>
                                </span>
                                <span class="card-datetime-created">${createdAtDateFromatted} - ${createdAtTimeFormatted}</span>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col s12 m12">
                            <span class="card-question">${poll.question}</span>
                        </div>
                    </div>

                    <form id="card_${poll.id}"></form>`;

    var pollHtmlFooter =
                    `<div class="row">
                        <div class="card-vote-btn col s4 m1">
                            <a class="waves-effect waves-light btn-small indigo accent-4 ${voteBtnState}" id="vote_btn_${poll.id}" onclick="vote(this.id)">
                                Vote
                            </a>
                        </div>
                        <div class="card-time-left col">
                            <span>${poll.totalVotes} votes - ${pollTimeLeft}left</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>`;

    var pollHtmlBody = ``;

    for(var i = 0; i < poll.choices.length; i++) {
        var pollHtmlChoice =
        `<div class="row">
            <div class="col s12 m12">
                <div>
                  <label>
                    <input id="c${poll.choices[i].id}" name="poll${poll.id}" type="radio" form="card_${poll.id}" />
                    <span>${poll.choices[i].text}</span>
                  </label>
                </div>
            </div>
        </div>`;

        pollHtmlBody += pollHtmlChoice;
    }

    pollHtml = pollHtmlHeader + pollHtmlBody + pollHtmlFooter;

    $("#polls_container").append(pollHtml);

    // init card's avatar
    var initial = poll.createdBy.name[0].toUpperCase();
    var avatar = document.getElementById(`card_profile_avatar_${poll.id}`);
    avatar.innerHTML = "<span>"+initial+"</span>";
    avatar.style.backgroundColor = CSS_COLOR_NAMES[initial.charCodeAt() - 0x41];
}

function addPollActiveVoted(poll) {
    var voteBtnState = "disabled";

    // format createAt datetime
    var createdAtDate = new Date(poll.createdAt);
    var options = { day: "numeric", month: "short", year: "numeric" };
    var createdAtDateFromatted = createdAtDate.toLocaleDateString("default", options);
    var createdAtTimeFormatted = createdAtDate.getHours() + ":" + createdAtDate.getMinutes();

    // calculate time left for poll to end
    var endsAtDate = new Date(poll.endsAt);
    var now = new Date();

    var seconds = Math.floor((endsAtDate - (now)) / 1000);
    var minutes = Math.floor(seconds / 60);
    var hours = Math.floor(minutes / 60);
    var days = Math.floor(hours / 24);

    hours = hours-(days * 24);
    minutes = minutes - (days * 24 * 60) - (hours * 60);
    seconds = seconds - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);

    var pollTimeLeft = "";
    if(days > 0) {
        pollTimeLeft += days + "d ";
    }
    if(hours > 0) {
        pollTimeLeft += hours + "h ";
    }
    if(minutes > 0 && days == 0) {
        pollTimeLeft += minutes + "m ";
    }

    // create poll card
    var pollHtml = ``;

    var pollHtmlHeader =
    `<div id="poll_card_${poll.id}" class="row">
        <div class="col s8 m8 offset-s2 offset-m2">
            <div class="card">
                <div class="card-content">
                    <div class="row">
                        <div class="col s1 m1">
                            <div class="card-profile-avatar" id="card_profile_avatar_${poll.id}"></div>
                        </div>
                        <div class="col s11 m4">
                            <div class="card-header">
                                <span class="card-profile-name">${poll.createdBy.name}</span>
                                <span class="card-profile-username">
                                    <a href="profile.html?user=${poll.createdBy.username}">@${poll.createdBy.username}</a>
                                </span>
                                <span class="card-datetime-created">${createdAtDateFromatted} - ${createdAtTimeFormatted}</span>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col s12 m12">
                            <span class="card-question">${poll.question}</span>
                        </div>
                    </div>

                    <form id="card_${poll.id}"></form>`;

    var pollHtmlFooter =
                    `<div class="row">
                        <div class="card-vote-btn col s4 m1">
                            <a class="waves-effect waves-light btn-small indigo accent-4 ${voteBtnState}" id="vote_btn_${poll.id}" onclick="">
                                Vote
                            </a>
                        </div>
                        <div class="card-time-left col">
                            <span>${poll.totalVotes} votes - ${pollTimeLeft}left</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>`;

    var pollHtmlBody = ``;

    var percentages = [];
    for(var i = 0; i < poll.choices.length; i++) {
        var choicePercentage = Math.floor((poll.choices[i].voteCount * 100) / poll.totalVotes);
        percentages.push(choicePercentage);

        var pollHtmlChoice = `<div class="row">
            <div class="col s12 m12">
                <div id="c${poll.choices[i].id}" class="card-option-div">
                    <span class="card-option-span">
                        ${choicePercentage}% ${poll.choices[i].text}
                        ${poll.selectedChoice == poll.choices[i].id ? "<i class=\"material-icons voted-icon\">done</i>" : ""}
                    </span>
                </div>
            </div>
        </div>`;

        pollHtmlBody += pollHtmlChoice;
    }

    pollHtml = pollHtmlHeader + pollHtmlBody + pollHtmlFooter;

    $("#polls_container").append(pollHtml);

    // add choice % bar color
    for(var i = 0; i < poll.choices.length; i++) {
        document.getElementById(`c${poll.choices[i].id}`).style.background = "linear-gradient(to right, LightGrey " + percentages[i] + "%, white 0%)";
    }

    // init card's avatar
    var initial = poll.createdBy.name[0].toUpperCase();
    var avatar = document.getElementById(`card_profile_avatar_${poll.id}`);
    avatar.innerHTML = "<span>"+initial+"</span>";
    avatar.style.backgroundColor = CSS_COLOR_NAMES[initial.charCodeAt() - 0x41];
}

function updatePollActiveVoted(poll) {
    var voteBtnState = "disabled";

    // format createAt datetime
    var createdAtDate = new Date(poll.createdAt);
    var options = { day: "numeric", month: "short", year: "numeric" };
    var createdAtDateFromatted = createdAtDate.toLocaleDateString("default", options);
    var createdAtTimeFormatted = createdAtDate.getHours() + ":" + createdAtDate.getMinutes();

    // calculate time left for poll to end
    var endsAtDate = new Date(poll.endsAt);
    var now = new Date();

    var seconds = Math.floor((endsAtDate - (now)) / 1000);
    var minutes = Math.floor(seconds / 60);
    var hours = Math.floor(minutes / 60);
    var days = Math.floor(hours / 24);

    hours = hours-(days * 24);
    minutes = minutes - (days * 24 * 60) - (hours * 60);
    seconds = seconds - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);

    var pollTimeLeft = "";
    if(days > 0) {
        pollTimeLeft += days + "d ";
    }
    if(hours > 0) {
        pollTimeLeft += hours + "h ";
    }
    if(minutes > 0 && days == 0) {
        pollTimeLeft += minutes + "m ";
    }

    // create poll card
    var pollHtml = ``;

    var pollHtmlHeader =
    `<div class="col s8 m8 offset-s2 offset-m2">
        <div class="card">
            <div class="card-content">
                <div class="row">
                    <div class="col s1 m1">
                        <div class="card-profile-avatar" id="card_profile_avatar_${poll.id}"></div>
                    </div>
                    <div class="col s11 m4">
                        <div class="card-header">
                            <span class="card-profile-name">${poll.createdBy.name}</span>
                            <span class="card-profile-username">
                                <a href="profile.html?user=${poll.createdBy.username}">@${poll.createdBy.username}</a>
                            </span>
                            <span class="card-datetime-created">${createdAtDateFromatted} - ${createdAtTimeFormatted}</span>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col s12 m12">
                        <span class="card-question">${poll.question}</span>
                    </div>
                </div>

                <form id="card_${poll.id}"></form>`;

    var pollHtmlFooter =
                    `<div class="row">
                        <div class="card-vote-btn col s4 m1">
                            <a class="waves-effect waves-light btn-small indigo accent-4 ${voteBtnState}" id="vote_btn_${poll.id}" onclick="">
                                Vote
                            </a>
                        </div>
                        <div class="card-time-left col">
                            <span>${poll.totalVotes} votes - ${pollTimeLeft}left</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>`;

    var pollHtmlBody = ``;

    var percentages = [];
    for(var i = 0; i < poll.choices.length; i++) {
        var choicePercentage = Math.floor((poll.choices[i].voteCount * 100) / poll.totalVotes);
        percentages.push(choicePercentage);

        var pollHtmlChoice = `<div class="row">
            <div class="col s12 m12">
                <div id="c${poll.choices[i].id}" class="card-option-div">
                    <span class="card-option-span">
                        ${choicePercentage}% ${poll.choices[i].text}
                        ${poll.selectedChoice == poll.choices[i].id ? "<i class=\"material-icons voted-icon\">done</i>" : ""}
                    </span>
                </div>
            </div>
        </div>`;

        pollHtmlBody += pollHtmlChoice;
    }

    pollHtml = pollHtmlHeader + pollHtmlBody + pollHtmlFooter;

    $(`#poll_card_${poll.id}`).html(pollHtml);

    // add choice % bar color
    for(var i = 0; i < poll.choices.length; i++) {
        document.getElementById(`c${poll.choices[i].id}`).style.background = "linear-gradient(to right, LightGrey " + percentages[i] + "%, white 0%)";
    }

    // init card's avatar
    var initial = poll.createdBy.name[0].toUpperCase();
    var avatar = document.getElementById(`card_profile_avatar_${poll.id}`);
    avatar.innerHTML = "<span>"+initial+"</span>";
    avatar.style.backgroundColor = CSS_COLOR_NAMES[initial.charCodeAt() - 0x41];
}

function addPollEndedNotVoted(poll) {
    var voteBtnState = "disabled";

    // format createAt datetime
    var createdAtDate = new Date(poll.createdAt);
    var options = { day: "numeric", month: "short", year: "numeric" };
    var createdAtDateFromatted = createdAtDate.toLocaleDateString("default", options);
    var createdAtTimeFormatted = createdAtDate.getHours() + ":" + createdAtDate.getMinutes();

    // create poll card
    var pollHtml = ``;

    var pollHtmlHeader =
    `<div id="poll_card_${poll.id}" class="row">
        <div class="col s8 m8 offset-s2 offset-m2">
            <div class="card">
                <div class="card-content">
                    <div class="row">
                        <div class="col s1 m1">
                            <div class="card-profile-avatar" id="card_profile_avatar_${poll.id}"></div>
                        </div>
                        <div class="col s11 m4">
                            <div class="card-header">
                                <span class="card-profile-name">${poll.createdBy.name}</span>
                                <span class="card-profile-username">
                                    <a href="profile.html?user=${poll.createdBy.username}">@${poll.createdBy.username}</a>
                                </span>
                                <span class="card-datetime-created">${createdAtDateFromatted} - ${createdAtTimeFormatted}</span>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col s12 m12">
                            <span class="card-question">${poll.question}</span>
                        </div>
                    </div>

                    <form id="card_${poll.id}"></form>`;

    var pollHtmlFooter =
                    `<div class="row">
                        <div class="card-vote-btn col s4 m1">
                            <a class="waves-effect waves-light btn-small indigo accent-4 ${voteBtnState}" id="vote_btn_${poll.id}" onclick="">
                                Vote
                            </a>
                        </div>
                        <div class="card-time-left col">
                            <span>${poll.totalVotes} votes - Final Results</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>`;

    var pollHtmlBody = ``;

    var percentages = [];
    for(var i = 0; i < poll.choices.length; i++) {
        var choicePercentage = 0;
        if(poll.choices[i].voteCount != 0) {
            var choicePercentage = Math.floor((poll.choices[i].voteCount * 100) / poll.totalVotes);
        }
        percentages.push(choicePercentage);

        var pollHtmlChoice = `<div class="row">
            <div class="col s12 m12">
                <div id="c${poll.choices[i].id}" class="card-option-div">
                    <span class="card-option-span">
                        ${choicePercentage}% ${poll.choices[i].text}
                        ${poll.selectedChoice == poll.choices[i].id ? "<i class=\"material-icons voted-icon\">done</i>" : ""}
                    </span>
                </div>
            </div>
        </div>`;

        pollHtmlBody += pollHtmlChoice;
    }

    pollHtml = pollHtmlHeader + pollHtmlBody + pollHtmlFooter;

    $("#polls_container").append(pollHtml);

    // add choice % bar color
    for(var i = 0; i < poll.choices.length; i++) {
        document.getElementById(`c${poll.choices[i].id}`).style.background = "linear-gradient(to right, rgba(48, 79, 254, 0.3) " + percentages[i] + "%, white 0%)";
    }

    // init card's avatar
    var initial = poll.createdBy.name[0].toUpperCase();
    var avatar = document.getElementById(`card_profile_avatar_${poll.id}`);
    avatar.innerHTML = "<span>"+initial+"</span>";
    avatar.style.backgroundColor = CSS_COLOR_NAMES[initial.charCodeAt() - 0x41];
}

function addPollEndedVoted(poll) {
    var voteBtnState = "disabled";

    // format createAt datetime
    var createdAtDate = new Date(poll.createdAt);
    var options = { day: "numeric", month: "short", year: "numeric" };
    var createdAtDateFromatted = createdAtDate.toLocaleDateString("default", options);
    var createdAtTimeFormatted = createdAtDate.getHours() + ":" + createdAtDate.getMinutes();

    // create poll card
    var pollHtml = ``;

    var pollHtmlHeader =
    `<div id="poll_card_${poll.id}" class="row">
        <div class="col s8 m8 offset-s2 offset-m2">
            <div class="card">
                <div class="card-content">
                    <div class="row">
                        <div class="col s1 m1">
                            <div class="card-profile-avatar" id="card_profile_avatar_${poll.id}"></div>
                        </div>
                        <div class="col s11 m4">
                            <div class="card-header">
                                <span class="card-profile-name">${poll.createdBy.name}</span>
                                <span class="card-profile-username">
                                    <a href="profile.html?user=${poll.createdBy.username}">@${poll.createdBy.username}</a>
                                </span>
                                <span class="card-datetime-created">${createdAtDateFromatted} - ${createdAtTimeFormatted}</span>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col s12 m12">
                            <span class="card-question">${poll.question}</span>
                        </div>
                    </div>

                    <form id="card_${poll.id}"></form>`;

    var pollHtmlFooter =
                    `<div class="row">
                        <div class="card-vote-btn col s4 m1">
                            <a class="waves-effect waves-light btn-small indigo accent-4 ${voteBtnState}" id="vote_btn_${poll.id}" onclick="">
                                Vote
                            </a>
                        </div>
                        <div class="card-time-left col">
                            <span>${poll.totalVotes} votes - Final Results</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>`;

    var pollHtmlBody = ``;

    var percentages = [];
    for(var i = 0; i < poll.choices.length; i++) {
        var choicePercentage = 0;
        if(poll.choices[i].voteCount != 0) {
            var choicePercentage = Math.floor((poll.choices[i].voteCount * 100) / poll.totalVotes);
        }
        percentages.push(choicePercentage);

        var pollHtmlChoice = `<div class="row">
            <div class="col s12 m12">
                <div id="c${poll.choices[i].id}" class="card-option-div">
                    <span class="card-option-span">
                        ${choicePercentage}% ${poll.choices[i].text}
                        ${poll.selectedChoice == poll.choices[i].id ? "<i class=\"material-icons voted-icon\">done</i>" : ""}
                    </span>
                </div>
            </div>
        </div>`;

        pollHtmlBody += pollHtmlChoice;
    }

    pollHtml = pollHtmlHeader + pollHtmlBody + pollHtmlFooter;

    $("#polls_container").append(pollHtml);

    // add choice % bar color
    for(var i = 0; i < poll.choices.length; i++) {
        document.getElementById(`c${poll.choices[i].id}`).style.background = "linear-gradient(to right, rgba(48, 79, 254, 0.3) " + percentages[i] + "%, white 0%)";
    }

    // init card's avatar
    var initial = poll.createdBy.name[0].toUpperCase();
    var avatar = document.getElementById(`card_profile_avatar_${poll.id}`);
    avatar.innerHTML = "<span>"+initial+"</span>";
    avatar.style.backgroundColor = CSS_COLOR_NAMES[initial.charCodeAt() - 0x41];
}

function vote(pollId) {
    // extract pollId from clicked vote button's id
    pollId = pollId.replace("vote_btn_", "");

    // find selected radio button in poll's radio group
    var choiceId = document.querySelector('input[name="poll' + pollId + '"]:checked').id;
    if(choiceId == null) {
        return;
    }

    // extract choiceId from selected radio button's id
    choiceId = choiceId.replace("c", "");

    // create request JSON object
    var jsonObj = {};
    jsonObj.choiceId = choiceId;

    var form_data = JSON.stringify(jsonObj);

    var jwt = getCookie("jwt");

    $.ajax({
        url: "http://localhost:8080/api/polls/" + pollId + "/votes",
        type : "POST",
        contentType : "application/json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + jwt);
        },
        data : form_data,
        success : function(result) {
            M.toast({html: "Voted!"});
            updatePollActiveVoted(result);
            return true;
        },
        error : function(xhr, resp, text) {
            var jsonResponse = $.parseJSON(xhr.responseText);

            if(jsonResponse.status == "UNAUTHORIZED") {
                showLoginPage();
            } else {
                M.toast({html: "Something went wrong!"})
            }
            return false;
        }
    });
}

function fillProfileData(userProfile) {
    var createdAtDate = new Date(userProfile.createdAt);
    var options = { month: "long", year: "numeric" };
    var createdAtDateFromatted = createdAtDate.toLocaleDateString("default", options);

    $("#profile_name").html(userProfile.name);
    $("#profile_username").html(userProfile.username);
    $("#profile_join_date").html("Joined " + createdAtDateFromatted);
}
