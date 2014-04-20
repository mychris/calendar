jQuery.extend({
  
  postJson: function(reqestUrl, requestBody) {
    return this.ajax({
      type        : "DELETE",
      url         : reqestUrl,
      contentType : "application/json; charset=utf-8",
      dataType    : "json",
      data        : JSON.stringify(requestBody)
    });
  },

  deleteJson: function(reqestUrl, requestBody) {
    return this.ajax({
      type        : "DELETE",
      url         : reqestUrl,
      contentType : "application/json; charset=utf-8",
      dataType    : "json",
      data        : JSON.stringify(requestBody)
    });
  },

  putJson: function(reqestUrl, requestBody) {
    return this.ajax({
      type        : "PUT",
      url         : reqestUrl,
      contentType : "application/json; charset=utf-8",
      dataType    : "json",
      data        : JSON.stringify(requestBody)
    });
  }
});