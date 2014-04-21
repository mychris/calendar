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
      'title'  : eventData.title,
      'start'  : eventData.start.utc().valueOf(),
      'end'    : eventData.end.utc().valueOf(),
      'tagIds' : eventData.tagIds
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
function createEvent(eventData, callback) {
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
      'title'  : eventData.title,
      'start'  : eventData.start, // Long
      'end'    : eventData.end,   // Long
      'tagIds' : eventData.tagIds // TODO: Specify Tag from List
    }),
    error: function(err) {
      console.log("createEvent Error: ");
      console.log(err.responseText);
      $('#calendar').fullCalendar('unselect');
    },
    success: function(appointmentWithTags) {
      var highestPriorityTag = getHighestPriorityTag(appointmentWithTags.tags);

      var newEventData = {
        'id'    : appointmentWithTags.appointment.id,
        'title' : appointmentWithTags.appointment.title,
        'start' : appointmentWithTags.appointment.start, 
        'end'   : appointmentWithTags.appointment.end,   
        'color' : highestPriorityTag.color,
        'tagIds': $.map(appointmentWithTags.tags, function(v) { return v.id; })
      }

      $('#calendar').fullCalendar('unselect');
      $('#calendar').fullCalendar('renderEvent', newEventData, true); // stick? = true
      callback();
    }
  });
}

function createEventPopover(selectedElement, startLong, endLong){
  getTags()
  .done(function(res) {
    $(selectedElement).popover({
        container   : 'body',
        placement   : 'auto top',
        html        : 'true',
        // title       : 'Create a new event',
        delay       :  {
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
                  + "<p class='form-control-static' id='newEventStart' date='" + startLong + "'>" + moment.utc(moment(startLong)).format("dd, MMM DD, HH:mm") + "</p>"
                + "</div>"
                + "<label class='col-sm-3 control-label'>To</label>"
                + "<div class='col-sm-9'>"
                  + "<p class='form-control-static' id='newEventEnd' date='" + endLong + "'>" + moment.utc(moment(endLong)).format("dd, MMM DD, HH:mm") + "</p>"
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


    $( "#cancelNewEventSubmit" ).on( "click", function( event ) {
      $('#calendar').fullCalendar('unselect');
      $(selectedElement).popover('destroy');

    });

    $('#newEventPopoverContent').keyup(function(e) {
      if (e.keyCode == 27) { $('#cancelNewEventSubmit').click(); }   // esc
    });
    
    $( "#newEventPopoverContent" ).on( "submit", function( event ) {
      event.preventDefault();

      var newEventTitle = $('#newEventTitle');

      if ( !newEventTitle.val().trim() ) {
        newEventTitle.parent().addClass("has-error");
        return;
      }
      else {
        newEventTitle.parent().removeClass("has-error");
      }

      var checkedTags = $('#newEventTags input:checkbox:checked').map(function() {
        return parseInt($(this).attr('tagid'));
      }).get();

      var eventData = {
        title : newEventTitle.val(),
        start : startLong, 
        end   : endLong,   
        tagIds: checkedTags
      }
      createEvent(eventData, function(){
        $(selectedElement).popover('destroy');
        $(lastSelected).popover('destroy');
        findConflicts();
      });
    });

  })
  .fail(function(err) {
    console.log("Error trying to receive list of tags:")
    console.log(err.responseText)
  });
}


function getHighestPriorityTag(tags){
  var highestPriorityTag = { priority:-1, color:"#3A87AD" };
  $.each(tags, function(index, value) {  // todo: better: use max function here
    if (highestPriorityTag.priority < value.priority) 
      highestPriorityTag = value;
  });
  return highestPriorityTag;
}

function filterEventsByTagIds(tagIds){
  function listContainsAny(list, filters) {
    return $.inArray(true, $.map(filters, function(val) {
      return $.inArray(val, list) > -1;
    })) > -1;
  }

  $('[tagIds]').each(function(index){
    $(this).removeClass("hidden");
  });      

  var d = $('[tagIds]').filter(function(index, el) {  
    console.log($(el).attr('tagIds').split(",") + ": " + !listContainsAny($(el).attr('tagIds').split(","), tagIds));
    return !listContainsAny($(el).attr('tagIds').split(","), tagIds);
  });

  d.each(function(index){
    $(this).addClass("hidden");
  });      
}


/** 
 * Returns a promise of tags 
 */
function getTags(){
  return $.ajax({
      url: jsRoutes.controllers.Tags.list().url,
      type: 'GET',
      dataType: 'json',
      headers: {
        Accept: "application/json; charset=utf-8",
        "Content-Type": "application/json; charset=utf-8"
      }
  });
}

function findFreeTimeSlots() {
  $.ajax({
    type: "POST",
    url: jsRoutes.controllers.Appointments.freeTimeSlots().url,
    dataType: "json",
    headers: {
      Accept: "application/json; charset=utf-8",
      "Content-Type": "application/json; charset=utf-8"
    },
    data: JSON.stringify({
      'duration': $('#duration').val().toString(),
      'start': $('#betweenFrom').val().toString(),
      'end': $('#betweenTo').val().toString(),
    }),
    error: function(xhr) {
      // var resp = JSON.parse(xhr.responseText)
      // console.log(resp.errorMsg);
      console.log("findFreeTimeSlots Error: ")
      console.log(xhr.responseText);
    },
    success: function(data) {
      console.log("got free time slots: ");
      console.log(data);

      var eventData;

      for (var i = 0; i < data.length; i++) {
        eventData = {
          title: "freetimeslot",
          start: data[i].start,
          end: data[i].end
        }
        $('#calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
      }
    }
  });
}

function findConflicts() {
  d3.select("#conflicts ul").selectAll("li").data([]).exit().remove();
  d3.json(jsRoutes.controllers.Appointments.conflicts().url, function(error, data) {
    if (error) {
        d3.select("#conflicts li").remove;
        d3.select("#conflicts").append("p").text("No conflicts found!");
        return;
    }
    var conflicts = d3.select("#conflicts ul").selectAll("li").data(data.conflicts);
    conflicts.enter()
      .append("li")
      .classed({"list-group-item":true})
      .on("click", function(conflict) {
        var start1 = $.fullCalendar.moment(conflict[0].start);
        var start2 = $.fullCalendar.moment(conflict[1].start);
        if (start1.dayOfYear() == start2.dayOfYear()) {
          $("#calendar").fullCalendar('changeView', 'agendaDay');
        }
        $("#calendar").fullCalendar('gotoDate', $.fullCalendar.moment(start1));
      })
      .text(function(conflict) { return conflict[0].title + " - " + conflict[1].title; });
    conflicts.exit().remove();
  })
}

function listTags() {

  function generateMenu(tag) {
    return $(
      "<div class=\"menu dropdown\">" +
      "  <a class=\"open-menu dropdown-toggle caret\" style=\"color:" + tag.color +";\" role=\"button\" data-toggle=\"dropdown\" href=\"#\"/>" +
      "  <ul class=\"dropdown-menu\" role=\"menu\">" +
      "    <li role=\"presentation\">" +
      "      <a role=\"menuitem\" tabindex=\"-1\" onclick=\"startEditTag($(this).closest('.tag'))\">Edit tag</a>" +
      "    </li>" +
      "    <li role=\"presentation\">" +
      "      <a role=\"menuitem\" tabindex=\"-1\" onclick=\"deleteTag(" + tag.id + ")\">Delete tag</a>" +
      "    </li>" +
      "  </ul>" +
      "</div>"
    );
  }

  function showMenu(container) {
    $(container).append(generateMenu(container.__data__));
    $(".menu", container).on("hidden.bs.dropdown", function() {
      hideMenu(container);
    });
  }

  function hideMenu(container) {
    d3.select(container).select(".menu").remove();
  }

  d3.json(jsRoutes.controllers.Tags.list().url, function(error, data) {

    if (!error && data.tags.length > 0) {

      var tags = d3.select("#tags ul").selectAll("li").data(data.tags);

      // Enter
      tags.enter()
        .append("li")
        .attr("class", "tag")
        .on("mouseenter", function() {
          if(!$("#tags").data("editing") && !$(".menu ul", this).is(":visible"))
            showMenu(this);
        })
        .on("mouseleave", function() {
          if(!$(".menu ul", this).is(":visible"))
            hideMenu(this);
        })
        .append("span")
        .attr("class", "name")
        .style("color", function(tag) { return tag.color; })
        .text(function(tag) { return tag.name; });

      // Update
      tags
        .each(function() {
          d3.select(this).select(".name")
            .style("color", function(tag) { return tag.color; })
            .text(function(tag) { return tag.name; });
        });

      // Exit
      tags.exit().remove();

    } else {

      d3.selectAll("#tags li").remove;
      d3.select("#tags").append("p").text("No tags found!");
    }
  })
}

function startEditTag(tag) {

  function generateForm(tagName) {
    return $(
      "<form id=\"edit-tag\">" +
      "  <input class=\"form-control\" type=\"text\" value=\"" + tagName + "\"/>" +
      "  <div class=\"form-group\">" +
      "    <button type=\"submit\" class=\"btn btn-xs btn-primary\">Save</button>" +
      "    <button class=\"btn btn-xs btn-default\" onclick=\"cancelEdit($(this).closest('.tag'))\">Cancel</button>" +
      "  </div>" +
      "</form>"
    );
  }

  var tagName = tag[0].__data__.name;

  $(".name", tag).remove();
  $("#tags").data("editing", true);

  var form = generateForm(tagName);

  $("input", form.appendTo(tag)).focus();
  form.submit(function() {
    saveEdit($(this).closest('.tag'));
    return false;
  });
}

function saveEdit(tag) {

  var newTagName = $("input", tag).val();

  var data = {
    "name"     : newTagName,
    "priority" : tag[0].__data__.priority,
    "color"    : tag[0].__data__.color
  }

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
  $("<span class=\"name\" style=\"color:" + tag[0].__data__.color + "\">" + tag[0].__data__.name + "</span>").appendTo(tag);
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
