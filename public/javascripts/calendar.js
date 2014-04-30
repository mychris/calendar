/* jsRoutes.controllers.Tags.list().url */

function updateEvent(eventData, revertFunc) {
  $.ajax({
      type: "PUT",
      url: '/appointment/' + eventData.id,
      dataType: 'json',
      accepts: "application/json; charset=utf-8",
      headers: {
        Accept: "application/json; charset=utf-8",
        "Content-Type": "application/json; charset=utf-8"
      },
      data: JSON.stringify({
        'title': eventData.title,
        'start': eventData.start.valueOf(),
        'end': eventData.end.valueOf(),
        'tagIds': eventData.tagIds
      }),
      success: function(data) {
        findConflicts();
      },
      error: function(xhr) {
        revertFunc();
      },
  })
}

/*
 * Create and post an event to the server. If successful, show in calendar.
 * param eventData: Object with 'description', 'start' and 'end'
 */
function createEvent(eventData) {
  return $.ajax({
    type: "POST",
    url: jsRoutes.controllers.Appointments.add().url,
    dataType: "json",
    accepts: "application/json; charset=utf-8",
    headers: {
      Accept: "application/json; charset=utf-8",
      "Content-Type": "application/json; charset=utf-8"
    },
    data: JSON.stringify({
      'title' : eventData.title,
      'start' : eventData.start.valueOf(), // Long
      'end'   : eventData.end.valueOf(), // Long
      'tagIds': eventData.tagIds
    })
  });
}

function createEventPopover(selectedElement, start, end) {
  getTags()
    .done(function(res) {
      $(selectedElement).popover({
        container: 'body',
        placement: 'auto top',
        html: 'true',
        // title       : 'Create a new event',
        delay: {
          show: 400,
          hide: 400
        },
        content     : function(){
          var tags = res.tags
          var tagListElems = [];
          for (var i = 0; i < tags.length; i++) {
          tagListElems += ""
          + "<label class='btn btn-xs' style='background-color: " + tags[i].color + ";'>"
            + "<input type='checkbox' tagId='" + tags[i].id + "'>" + tags[i].name
          + "</label>"
          };

          return "" 
          + "<div id='newEventPopoverContent'>"
          + "<form class='form-horizontal' role='form'>"
            + "<div class='form-group'>"
            + "<label class='col-sm-3 control-label'>Title</label>"
            + "<div class='col-sm-9'>"
              + "<input type='text' class='form-control' id='newEventTitle' placeholder='Title'>"
            + "</div>"
            + "</div>"

            + "<div class='form-group'>"
            + "<label class='col-sm-3 control-label'>Tags</label>"
            + "<div class='col-sm-9' id='newEventTags'> "
              + tagListElems
            + "</div>"
            + "</div>"

            + "<div class='form-group'>"
            + "<label class='col-sm-3 control-label'>From</label>"
            + "<div class='col-sm-9'>"
              + "<p class='form-control-static' id='newEventStart' date='" + start + "'>" + start.format("dd, MMM DD, HH:mm") + "</p>"
            + "</div>"
            + "<label class='col-sm-3 control-label'>To</label>"
            + "<div class='col-sm-9'>"
              + "<p class='form-control-static' id='newEventEnd' date='" + end + "'>" + end.format("dd, MMM DD, HH:mm") + "</p>"
            + "</div>"
            + "</div>"

            + "<div class='form-group'>"
            + "<div class='col-sm-12'>"
              + "<button type='submit' class='btn btn-primary btn-sm col-sm-9'>Create Event</button>"
              + "<button type='button' id='cancelNewEventSubmit' class='btn btn-default btn-sm col-sm-3'>Cancel</button>"
            + "</div>"
            + "</div>"
          + "</form>"
          + "<div>";
        }
      });


      $(selectedElement).popover('show');
      $('#newEventTitle').focus();


      $("#cancelNewEventSubmit").on("click", function(event) {
        $('#calendar').fullCalendar('unselect');
        $(selectedElement).popover('destroy');

      });

      $('#newEventPopoverContent').keyup(function(e) {
        if (e.keyCode == 27) {
          $('#cancelNewEventSubmit').click();
        } // esc
      });

      $("#newEventPopoverContent").on("submit", function(event) {
        event.preventDefault();

        var newEventTitle = $('#newEventTitle');

        if (!newEventTitle.val().trim()) {
          newEventTitle.parent().addClass("has-error");
          return;
        } else {
          newEventTitle.parent().removeClass("has-error");
        }

        var checkedTags = $('#newEventTags input:checkbox:checked').map(function() {
          return parseInt($(this).attr('tagid'));
        }).get();

        var eventData = {
          'title'  : newEventTitle.val(),
          'start'  : start,
          'end'    : end,
          'tagIds' : checkedTags
        }
        createEvent(eventData)
        .done(function(appointmentWithTags) {
          var highestPriorityTag = getHighestPriorityTag(appointmentWithTags.tags);

          var newEventData = {
            'id'     : appointmentWithTags.appointment.id,
            'title'  : appointmentWithTags.appointment.title,
            'start'  : moment(appointmentWithTags.appointment.start),
            'end'    : moment(appointmentWithTags.appointment.end),
            'color'  : highestPriorityTag.color,
            'tagIds' : $.map(appointmentWithTags.tags, function(v) {
              return v.id;
            }),
            'type'   : 'appointment'
          }

          $('#calendar').fullCalendar('unselect');
          $('#calendar').fullCalendar('renderEvent', newEventData, true); // stick? = true

          $(selectedElement).popover('destroy');
          $(lastSelected).popover('destroy');
          findConflicts();
        })
        .fail(function(err){
          console.log("createEvent Error: ");
          console.log(err.responseText);
          $('#calendar').fullCalendar('unselect');
        });
      });

    })
    .fail(function(err) {
      console.log("Error trying to receive list of tags:")
      console.log(err.responseText)
    });
}

function getHighestPriorityTag(tags) {
  var highestPriorityTag = {
    priority: -1,
    color: "#3A87AD"
  };
  $.each(tags, function(index, value) { // todo: better: use max function here
    if (highestPriorityTag.priority < value.priority)
      highestPriorityTag = value;
  });
  return highestPriorityTag;
}

/* Shows only events belonging (eventAttributeName contains IDs) to any tag given in ids 
 * param ids filter IDs which eventAttributeName must contain to be filtered
 * param eventAttributeName: name of event attribute which contains the IDs to be filtered
*/
function filterEventsByIds(ids, eventAttributeName) {
  function listContainsAny(list, filters) {
    return $.inArray(true, $.map(filters, function(val) {
      return $.inArray(val, list) > -1;
    })) > -1;
  }

  var eventsWithAttribute = $("[" + eventAttributeName + "].fc-event");

  eventsWithAttribute.each(function(index) {
    $(this).removeClass("hidden");
  });

  var eventsBelongingToNoTag = eventsWithAttribute.filter(function(index, el) {
    var isFiltered = !listContainsAny($(el).attr(eventAttributeName).split(","), ids);
    // un-comment for test purposes: 
    // console.log($(el).attr(eventAttributeName).split(",") + ": " + isFiltered);
    return isFiltered;
  });

  eventsBelongingToNoTag.each(function(index) {
    $(this).addClass("hidden");
  });
}

/** 
 * Returns a promise of tags
 */
function getTags() {
  return $.ajax({
    url: jsRoutes.controllers.Tags.list().url,
    type: 'GET',
    dataType: 'json',
    headers: {
      Accept: "application/json; charset=utf-8"
    }
  });
}

function findFreeTimeSlots() {
  var userIds   = $('#inputUsers').selectize()[0].selectize.items
  var duration  = (moment($('.durationpicker').data("DateTimePicker").getDate(), "HH:mm").hours() * 60 + moment($('.durationpicker').data("DateTimePicker").getDate(), "HH:mm").minutes()) * 60 * 1000;  // hours and minutes in millis as LongA
  var from      = moment($('.datetimepicker1').data("DateTimePicker").getDate()).valueOf();
  var to        = moment($('.datetimepicker2').data("DateTimePicker").getDate()).valueOf();
  var startTime = moment($('.timepicker1').data("DateTimePicker").getDate(), "h:mm A").valueOf();
  var endTime   = moment($('.timepicker2').data("DateTimePicker").getDate(), "h:mm A").valueOf();

  return $.getJSON(jsRoutes.controllers.Proposals.findFreeTimeSlots(userIds, duration, from, to, startTime, endTime).url);
}

function findConflicts() {
  d3.json(jsRoutes.controllers.Appointments.conflicts().url, function(error, data) {
    if (error) {
      d3.select("#conflicts li").remove();
      d3.select("#conflicts").append("p").text("No conflicts found!");
      return;
    }
    var conflicts = d3.select("#conflicts ul").selectAll("li").data(data.conflicts);

    // enter
    conflicts.enter()
      .append("li")
      .classed({
        "list-group-item": true
      })
      .on("click", function(conflict) {
        var start1 = $.fullCalendar.moment(conflict[0].start);
        var start2 = $.fullCalendar.moment(conflict[1].start);
        if (start1.dayOfYear() == start2.dayOfYear()) {
          $("#calendar").fullCalendar('changeView', 'agendaDay');
        } else if (start1.week() == start2.week()) {
          $("#calendar").fullCalendar('changeView', 'agendaWeek');
        } else {
          $("#calendar").fullCalendar('changeView', 'month');
        }
        if (start1.hour() == 0) {
          start1 = start1.add("hour", 1);
        }
        $("#calendar").fullCalendar('gotoDate', $.fullCalendar.moment(start1));
      })
      .text(function(conflict) {
        return conflict[0].title + " - " + conflict[1].title;
      });

    // exit
    conflicts.exit().remove();
  })
}

function fetchProposalTimes(proposalId){
  return $.ajax({
    url: jsRoutes.controllers.Proposals.proposalTimesFromProposal(proposalId).url,
    type: 'GET',
    dataType: 'json',
    headers: {
      Accept: "application/json; charset=utf-8"
    }
  });
}

function getIconForVoteIdOrName(vote){
    var content = "";
    if ( (vote == 1) || (vote == "Accepted") ) {
      content = "<i title='Accept' class='glyphicon accepted'></i>";
    } else if ( (vote == 2) || (vote == "Refused") ) {
      content = "<i title='Refuse' class='glyphicon refused'></i>";
    } else if ( (vote == 3) || (vote == "Uncertain") ) {
      content = "<i title='Uncertain' class='glyphicon uncertain'></i>";
    } else {
      content = "<i class='glyphicon glyphicon-minus'></i>"
    }
    return content;
  }

function listProposals(successCallback) {

  function generateProposalMenu(proposal) {
    return $(
      "<div class='menu dropdown'>" +
      "  <a class='open-menu dropdown-toggle caret'" +
      "     style='color:" + proposal.color + ";'" +
      "     role='button'" +
      "     data-toggle='dropdown'" +
      "     href='#'/>" +
      "  <ul class='dropdown-menu' role='menu'>" +
      "    <li role='presentation'>" +
      "      <a role='menuitem' tabindex='-1' onclick='setFinishProposalModalValues($(this).closest(\".proposal\").attr(\"proposalId\")); $(\"#finishProposalModal\").modal(\"show\");'>Finish proposal</a>" +
      "    </li>" +
      "    <li role='presentation'>" +
      "      <a role='menuitem' tabindex='-1' onclick='deleteProposal($(this).closest(\".proposal\").attr(\"proposalId\"))'>Delete proposal</a>" +
      "    </li>" +
      "  </ul>" +
      "</div>"
    );
  }

  function showProposalMenu(container) {
    $(container).append(generateProposalMenu(container.__data__));
    $(".menu", container).on("hidden.bs.dropdown", function() {
      hideProposalMenu(container);
    });
  }

  function hideProposalMenu(container) {
    d3.select(container).select(".menu").remove();
  }

  return d3.json(jsRoutes.controllers.Proposals.list().url, function(error, data) {

    if(!error && data.proposals.length > 0) {

      var proposals = d3.select("#proposals ul").selectAll("li").data(data.proposals);

      // Enter
      var missingListItem = proposals.enter();
      var listItem = missingListItem
        .append("li")
        .attr("class", "proposal")
        .attr("proposalId", function(proposal) {
          return proposal.proposal.id;
        })
        .on("mouseenter", function() {
          if (!$("#proposals").data("editing") && !$(".menu ul", this).is(":visible"))
            showProposalMenu(this);
        })
        .on("mouseleave", function() {
          if (!$(".menu ul", this).is(":visible"))
            hideProposalMenu(this);
        })

      var listItemSpan = listItem
        .append("span")
        .attr("class", "btn filter-off")
        .on("click", function(clickedProposal) {
          $(this).toggleClass("proposal-filter-on");
          $(this).toggleClass("filter-off");

          if( $(this).hasClass("proposal-filter-on") ) {
            fetchProposalTimes(clickedProposal.proposal.id)
            .done(function(data){
              var proposalTimes = data.proposalTimes;

              for (var i = 0; i < proposalTimes.length; i++) {
                var votes = [];
                  for (var j = 0; j < proposalTimes[i].votes.length; j++) {
                     votes.push( "<div class='" + (($('#username').text() == proposalTimes[i].votes[j].user.name)?"you":"") + "'><span>" + proposalTimes[i].votes[j].user.name + " </span><span vote>" + getIconForVoteIdOrName(proposalTimes[i].votes[j].vote) + "</span></div>");
                   }; 

                eventData = {
                  'proposalId'           : clickedProposal.proposal.id,
                  'title'                : "<span>"
                                            + "<span>" + clickedProposal.proposal.title + "</span>"
                                            + "<hr style='margin: 3px 0 7px 0; border-color: #252525;'>"
                                            + votes.join("") 
                                         + "</span>",
                  'start'                : moment(proposalTimes[i].proposalTime.start),
                  'end'                  : moment(proposalTimes[i].proposalTime.end),
                  'color'                : clickedProposal.proposal.color,
                  'textColor'            : '#252525',
                  'editable'             : false,
                  'type'                 : 'proposal',
                  'timeId'               : proposalTimes[i].proposalTime.id
                }
                $('#calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
              }
            })
            .fail(function(err){
              console.log("Unable to show proposal times:");
              console.log(err.responseText);
            });
          } 
          else {
            $('#calendar').fullCalendar( 'removeEvents', function(event) {
              return (event.type == "proposal") && (event.proposalId == clickedProposal.proposal.id)
            });
          }
          // In case we would have used filtering:
          // var proposalIds = $(".proposal-filter-on").parent().map(function() {
          //   return $(this).attr("proposalId");
          // }).get()
          // filterEventsByIds(proposalIds, "proposalId");
        })
        .style("background-color", function(proposal) { return proposal.proposal.color; });

      listItemSpan  
        .append("h3")
        .text(function(proposal) { return proposal.proposal.title; })

      listItemSpan
        .append("span")
        .attr("class", "participants")
        .text(function(proposal) { return $.map(proposal.participants, function(par) { return par.name; }).join(", "); });

      // Update
      proposals.attr("proposalId", function(proposal) {
        return proposal.proposal.id;
      });

      proposals.each(function() {

        var thisSpan = d3.select(this).select("span")
          .style("background-color", function(proposal) { return proposal.proposal.color; });

        thisSpan.select("h3")
          .text(function(proposal) { return proposal.proposal.title; })
          
        thisSpan.select("span")
          .text(function(proposal) { return $.map(proposal.participants, function(par) { return par.name; }).join(", "); });
      });

      // Exit
      proposals.exit().remove();

      if(successCallback)
        successCallback();

    } else {
      d3.selectAll("#proposals li").remove();
      d3.select("#proposals ul").append("li").text("No proposals found!");
    }
  });
}

function proposalCreationMode(){
  createProposalMode = true;
  var proposalName = $('#proposalName').val();

  findFreeTimeSlots()
  .done(function(data) {
    var timeSlots = data.slots;

    if( timeSlots.length == 0 )
      return;

    $('#createProposal').hide();
    $('#finishCreateProposal').show();
    $('#cancelCreateProposal').show();

    // var eventDatas = [];
    for (var i = 0; i < timeSlots.length; i++) {
      eventData = {
        'title'                : proposalName,
        'start'                : moment(timeSlots[i].start),
        'end'                  : moment(timeSlots[i].end),
        'textColor'            : '#252525', 
        'editable'             : false,
        'type'                 : 'freetimeslot'
      }
      // eventDatas.push(eventData);
     $('#calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
    }
    
    // $('#calendar').fullCalendar('rerenderEvents');

    $("#calendar").fullCalendar('gotoDate', data.slots[0].start);
    $("#calendar").fullCalendar('changeView', 'agendaWeek');
  })
  .fail(function(err) {
      console.log("Unable to show free time slots:");
      console.log(err.responseText)
  });

  $('#proposalModal').modal('hide');  
}

function createProposalCleanup(){
  $('#finishCreateProposal').hide();
  $('#cancelCreateProposal').hide();
  $('#calendar').fullCalendar( 'removeEvents', function(event) {
    return (event.type == "freetimeslot") || (event.type == "suggestion")
  });
  $('#createProposal').show();

  createProposalMode = false;
}

function finishCreateProposal(){
  var suggestions = $('#calendar').fullCalendar('clientEvents', function(event){
    return event.type == 'suggestion'
  }).map(function(suggestion) {
    return {
      'start' : suggestion.start.valueOf(),
      'end'   : suggestion.end.valueOf()
    };
  });

  var data = {
    'title'        : $('#proposalName').val(),
    'color'        : "#E08E79",   // let them eat cake
    'participants' : $('#inputUsers').selectize()[0].selectize.items.map(function(el){
                        return parseInt(el);
                      }),
    'times'        : suggestions
  };

  $.postJson(jsRoutes.controllers.Proposals.addWithTimes().url, data)
    .done(function(proposal) {
      createProposalCleanup();

      listProposals(function(){
        $("li[proposalId=" + proposal.id + "] > span").click(); // turns on filter (shows suggestion times) for proposal after creation
      });

      $("#calendar").fullCalendar('changeView', 'month');
    })
    .fail(function(xhr) {
      console.log("Unable to post new proposal!");
      console.log(xhr.responseText)
    });
}

function cancelCreateProposal(){
  createProposalCleanup();
}

function setProposalModalDefaultValues(){
  $('#proposalModalForm').trigger("reset");
  $('#inputUsers').selectize()[0].selectize.clear(); 
  $('#inputUsers').selectize()[0].selectize.refreshItems();
  // Duration Picker (duration of event)
  $('.durationpicker') .data("DateTimePicker").setDate(new Date(1979, 0, 1, 2, 0, 0, 0));
  // Datetime picker (time frame of proposal )
  if(test) {
    $('#proposalName').val("TestProposal");
    $('#inputUsers').selectize()[0].selectize.addItem(1);
    $('#inputUsers').selectize()[0].selectize.addItem(2);
    $('.datetimepicker1').data("DateTimePicker").setDate(new Date(2014, 0, 6, 08, 0, 0, 0));
    $('.datetimepicker2').data("DateTimePicker").setDate(new Date(2014, 0, 11, 22, 0, 0, 0));
  }
  else {
    $('.datetimepicker1').data("DateTimePicker").setDate(new Date());
    $('.datetimepicker2').data("DateTimePicker").setDate(moment().add('d', 14));
  }
  // Datetime picker (time frame within a day of proposal)
  $('.timepicker1')    .data("DateTimePicker").setDate(new Date(1979, 0, 1, 08, 0, 0, 0));
  $('.timepicker2')    .data("DateTimePicker").setDate(new Date(1979, 0, 1, 22, 0, 0, 0));
}

function setFinishProposalModalValues(proposalId) {
  $("#finishProposalModal table thead th").remove();
  $("#finishProposalModal table tbody tr").remove();

  $.ajax({
    url: jsRoutes.controllers.Proposals.proposalTimesFromProposal(proposalId).url,
    type: 'GET',
    dataType: 'json',
    headers: {
      Accept: "application/json; charset=utf-8"
    },
    success: function(proposals) {
      var participants = $.map(proposals["proposalTimes"], function(time, index) {
        return $.map(time["votes"], function(vote, index) {
          return vote["user"]["name"];
        });
      });
      // make participants unique
      participants = participants.filter(function(item, i, a) {
        return i == participants.indexOf(item);
      });
      // add participants to table
      $.each(participants, function(index, participant) {
        $("#finishProposalModal table tbody").append("<tr></tr>");
        $("#finishProposalModal table tbody tr:last").append("<td>" + participant + "</td>");
        $("#finishProposalModal table tbody tr:last").addClass("participant").addClass("participant-" + participant);
      });

      $("#finishProposalModal table thead tr").append("<th></th>");
      $.each(proposals["proposalTimes"], function(index, value) {
        // add th to thead
        var start = moment(value["proposalTime"]["start"]);
        var end = moment(value["proposalTime"]["end"]);
        var title = start.format("MMM D YYYY hh:mma") + " - " + end.format("MMM D YYYY hh:mma")
        $("#finishProposalModal table thead tr").append( "<th>"+title+"</th>" );

        var votes = value["votes"];
        if (!votes || votes.length <= 0) {
          return
        }

        // add tr to tbody
        // first add names
        $.each(votes, function(index, vote) {
          var name = vote["user"]["name"];
          var content, title, clazz;
          if (vote["vote"] == 1) {
            clazz = "accepted";
            title = "Accepted";
            content = '<i class="glyphicon glyphicon-ok"></i>';
          } else if (vote["vote"] == 2) {
            clazz = "refused";
            title = "Refused";
            content = '<i class="glyphicon glyphicon-remove"></i>';
          } else if (vote["vote"] == 3) {
            clazz = "uncertain";
            title = "Uncertain";
            content = '<i class="glyphicon glyphicon-question-sign"></i>';
          } else {
            clazz = "not-voted";
            title = "Not voted";
            content = '<i class="glyphicon glyphicon-minus";"></i>';
          }
          $("#finishProposalModal table tbody tr.participant-" + name).append('<td class="'+clazz+'" title="' + title +'">' + content + '</td>');
        });
      });
      // add finish submit buttons
      $("#finishProposalModal table tbody").append("<tr></tr>");
      $("#finishProposalModal table tbody tr:last").append("<td></td>");
      $.each(proposals["proposalTimes"], function(index, value) {

        $("#finishProposalModal table tbody tr:last").append('<td><button type="button" class="btn btn-default" onclick="javascript: finishProposalVote('+proposalId+','+value["proposalTime"]["id"]+');">Use this time</button></td>');
      });
    }
  })
}

function finishProposalVote(proposalId, proposalTimeId) {
  $.ajax({
    type: "POST",
    url: jsRoutes.controllers.Proposals.finishVote(proposalId).url,
    dataType: "json",
    accepts: "application/json; charset=utf-8",
    headers: {
      Accept: "application/json; charset=utf-8",
      "Content-Type": "application/json; charset=utf-8"
    },
    data: JSON.stringify({
      'times': [proposalTimeId]
    }),
    success: function(data) {
      $('#calendar').fullCalendar('refetchEvents');
      listProposals();
      findConflicts();
      $('#calendar').fullCalendar( 'removeEvents', function(event) {
        return (event.type == "proposal") && (event.proposalId == proposalId);
      });
    }
  });
  $("#finishProposalModal").modal('hide');
}

function deleteProposal(proposalId) {
  $.ajax({
    type: "DELETE",
    url: jsRoutes.controllers.Proposals.delete(proposalId).url,
    dataType: "json",
    accepts: "application/json; charset=utf-8",
    success: function(data) {
      listProposals();
      $('#calendar').fullCalendar( 'removeEvents', function(event) {
        return (event.type == "proposal") && (event.proposalId == proposalId);
      });
    }
  });
}

function voteForTimeSuggestion(proposalEventID, timeId, voteId){

  var proposalEvent = $('#calendar').fullCalendar('clientEvents', function(event){
    return (event.proposalId == proposalEventID) && (event.timeId == timeId);
  })[0];

  $.postJson(jsRoutes.controllers.Proposals.addVote(proposalEventID, timeId).url, { vote : voteId } )
    .done(function() {
        var newTitle = $(proposalEvent.title) 
        newTitle.find('div.you span[vote]').html(getIconForVoteIdOrName(voteId))
        proposalEvent.title = newTitle;
        $('#calendar').fullCalendar('updateEvent', proposalEvent);
    })
    .fail(function(err) {
      console.log("Unable to vote for proposal. proposalId: " + proposalEventID + ", timeId: " + timeId + ", voteId: " + voteId + ".");
      console.log(err.responseText)
    });
}

function initVotingPopover(proposalEvent, timeId) {
  $("div.fc-event[proposalid]").popover({
      container: 'body',
      placement: 'auto top',
      html: 'true',
      trigger: 'click',
      // title       : 'Create a new event',
      delay: {
        show: 100,
        hide: 100
      },
      content : function(){

        var content = ""
          + "<ul class='list-inline'>"
            + "<li>"
              + "<a title='Accept' onclick='voteForTimeSuggestion(" + proposalEvent.proposalId + ", " + timeId + ", \"Accepted\")'>"
                + "<i class='glyphicon accepted'></i>"
              + "</a>"
            + "</li>"
            + "<li>"
              + "<a title='Refuse' onclick='voteForTimeSuggestion(" + proposalEvent.proposalId + ", " + timeId + ", \"Refused\")'>"
                + "<i class='glyphicon refused'></i>"
              + "</a>"
            + "</li>"
            + "<li>"
              + "<a title='Uncertain' onclick='voteForTimeSuggestion(" + proposalEvent.proposalId + ", " + timeId  +", \"Uncertain\")'>"
                + "<i class='glyphicon uncertain'></i>"
              + "</a>"
            + "</li>"
          + "</ul>"
        return "" 
        + "<div id='votePopoverContent'>"
          + content
        + "<div>";
      }
  }); 
}

      
function listTags() {
  
  function generateTagMenu(tag) {
    return $(
      "<span class='menu dropdown'>" +
      "  <a class='open-menu dropdown-toggle caret'" +
      "     style='color:" + tag.color + ";'" +
      "     role='button'" +
      "     data-toggle='dropdown'" +
      "     href='#'/>" +
      "  <ul class='dropdown-menu' role='menu'>" +
      "    <li role='presentation'>" +
      "      <a role='menuitem' tabindex='-1' onclick='startEditTag($(this).closest(\".tag\"))'>Edit tag</a>" +
      "    </li>" +
      "    <li role='presentation'>" +
      "      <a role='menuitem' tabindex='-1' onclick='deleteTag(" + tag.id + ")'>Delete tag</a>" +
      "    </li>" +
      "  </ul>" +
      "</span>"
    );
  }

  function showTagMenu(container) {
    $(container).append(generateTagMenu(container.__data__));
    $(".menu", container).on("hidden.bs.dropdown", function() {
      hideTagMenu(container);
    });
  }

  function hideTagMenu(container) {
    d3.select(container).select(".menu").remove();
  }

  d3.json(jsRoutes.controllers.Tags.list().url, function(error, data) {

    if (!error && data.tags.length > 0) {

      var tags = d3.select("#tags ul").selectAll("li").data(data.tags);

      // Update
      tags.each(function() {
        d3.select(this).select(".name")
          .style("background-color", function(tag) {
            return tag.color;
          })
          .text(function(tag) {
            return tag.name;
          });
      });


      // Enter
      tags.enter()
        .append("li")
        .attr("class", "tag")
        .attr("tagid", function(tag) {
          return tag.id
        })
        .on("mouseenter", function() {
          if (!$("#tags").data("editing") && !$(".menu ul", this).is(":visible"))
            showTagMenu(this);
        })
        .on("mouseleave", function() {
          if (!$(".menu ul", this).is(":visible"))
            hideTagMenu(this);
        })
        .append("span")
        .attr("class", "name btn btn-xs tag-filter-on")
        .style("background-color", function(tag) {
          return tag.color;
        })
        .text(function(tag) {
          return tag.name;
        })
        .on("click", function() {
          $(this).toggleClass("tag-filter-on");
          $(this).toggleClass("filter-off");

          var tagIds = $(".tag-filter-on").parent().map(function() {
            return $(this).attr("tagid");
          }).get()

          filterEventsByIds(tagIds, "tagIds");
        })

      // Exit
      tags.exit().remove();

    } else {
      d3.selectAll("#tags li").remove();
      d3.select("#tags").append("p").text("No tags found!");
    }
  })
}

function generateTagEditForm(name, priority) {
  return $(
    "<form id='edit-tag'>" +
    "  <input name='name'" +
    "         type='text'" +
    "         class='form-control'" +
    "         value='" + name + "'" +
    "         placeholder='Name'>" +
    "  <input name='priority'" +
    "         type='number'" +
    "         class='form-control'" +
    "         value='" + priority + "'" +
    "         min='1'" +
    "         max='5'" +
    "         step='1'>" +
    "  <div class='form-group'>" +
    "    <button name='submit' type='submit' class='btn btn-xs btn-primary'>Save</button>" +
    "    <button name='cancel' type='button' class='btn btn-xs btn-default'>Cancel</button>" +
    "  </div>" +
    "</form>"
  );
}

function startEditTag(tag) {
  $("#tags").data("editing", true);

  var name = tag[0].__data__.name;
  var priority = tag[0].__data__.priority;

  $(".name", tag).remove();

  var form = generateTagEditForm(name, priority);

  tag.append(form);
  $("input[name=name]", form).focus();
  $("button[name=cancel]", form).on("click", function() {
    cancelEdit($(this).closest('.tag'));
  });
  form.submit(function() {
    saveEdit($(this).closest('.tag'));
    return false;
  });
}

function saveEdit(tag) {
  var newTagName = $("input[name=name]", tag).val();
  var newPriority = parseInt($("input[name=priority]", tag).val());

  var data = {
    "name": newTagName,
    "priority": newPriority,
    "color": tag[0].__data__.color
  };

  $.putJson(jsRoutes.controllers.Tags.update(tag[0].__data__.id).url, data)
    .done(function(data) {
      $(tag).remove();
      $("#tags").data("editing", false);
      listTags();
    })
    .fail(function() {
      console.log("Unable to update tag with id " + tag[0].__data__.id);
    });
}

function cancelEdit(tag) {
  $("#edit-tag").remove();
  $("<span class='name btn btn-xs tag-filter-on' style='background-color:" + tag[0].__data__.color + "'>" + tag[0].__data__.name + "</span>").appendTo(tag);
  $("#tags").data("editing", false);
}

function deleteTag(id) {
  $.delete(jsRoutes.controllers.Tags.delete(id).url)
    .done(function(data) {
      listTags();
    })
    .fail(function() {
      console.log("Unable to delete tag with id " + id);
    });
}

function startAddTag() {
  $("#tags").data("editing", true);

  var tag = $("<li class='tag'></li>").appendTo($("#tags ul"));
  var form = generateTagEditForm("", 1);

  tag.append(form);
  $("input[name=name]", form).focus();
  $("button[name=cancel]", form).on("click", function() {
    cancelAdd($(this).closest('.tag'));
  });
  form.submit(function() {
    saveAdd($(this).closest('.tag'));
    return false;
  });
}

function cancelAdd(tag) {
  tag.remove();
  $("#tags").data("editing", false);
}

function saveAdd(tag) {
  var data = {
    "name": $("#edit-tag input[name=name]").val(),
    "priority": parseInt($("#edit-tag input[name=priority]").val()),
    "color": "#c3a12c"
  };

  $.postJson(jsRoutes.controllers.Tags.add().url, data)
    .done(function(data) {
      tag.remove();
      $("#tags").data("editing", false);
      listTags();
    })
    .fail(function() {
      console.log("Unable to add tag!");
    });
}