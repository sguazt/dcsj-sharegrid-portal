/*
 * Colin Mollenhour - iframesubmit.js
 * link: http://pastie.caboo.se/50410#
 * Adapted from pork.iframe.js (http://beta.zapguide.nl/iframe/)
 *
 * Usage:
 *   new Form.IframeSubmit(form);
 */

Form.IframeSubmit = Class.create();
Form.IframeSubmit.prototype = {
  initialize: function(form, options){
    this.options = Object.extend({
      onComplete: null,
      container: null,
      insertion: Element.update
    },options || {});
    this.form = form;
    this.uniqueId = new Date().getTime();
    this.iframeName = 'iframe_'+this.uniqueId;
    Form.IframeSubmit.iframeLoaders[this.uniqueId] = this;
    this.transport = this.getTransport();
    form.target= this.iframeName;
    form.setAttribute("target", this.iframeName);
    form.submit();
  },
  onStateChange: function(){
    this.transport = $(this.iframeName);
    var doc = '';
    try { doc = this.transport.contentDocument.document.body.innerHTML; this.transport.contentDocument.document.close(); } catch (e){ // For NS6
    try { doc = this.transport.contentWindow.document.body.innerHTML; this.transport.contentWindow.document.close(); } catch (e){ // For IE5.5 and IE6
    try { doc = this.transport.document.body.innerHTML; this.transport.document.body.close(); } catch (e) { // for IE5
    try { doc = window.frames[this.iframeName].document.body.innerText; } catch (e) { } } } } // for really nasty browsers
    this.transport.responseText = doc;
    if (this.options.onComplete){ setTimeout(function(){ this.options.onComplete(this.transport,false); }.bind(this), 10); }
    if (this.options.container){ setTimeout(function(){ this.options.insertion(this.options.container,this.transport.responseText,false); }.bind(this), 10); }
  },
  getTransport: function(){
    var divElm = document.createElement('DIV');
    divElm.style.position = "absolute";
    divElm.style.top = "0";
    divElm.style.marginLeft = "-10000px";
    if(navigator.userAgent.indexOf('MSIE') > 0 && navigator.userAgent.indexOf('Opera') == -1){// switch to the crappy solution for IE
     divElm.innerHTML = '<iframe name="'+this.iframeName+'" id="'+this.iframeName+'" src="about:blank" onload="setTimeout(function(){Form.IframeSubmit.iframeLoaders['+this.uniqueId+'].onStateChange()},20);"></iframe>';
    }else{
      var frame = document.createElement("iframe");
      frame.setAttribute("name", this.iframeName);
      frame.setAttribute("id", this.iframeName);
      frame.addEventListener("load",  function(){ this.onStateChange(); }.bind(this), false);
      divElm.appendChild(frame);
    }
    document.getElementsByTagName("body").item(0).appendChild(divElm);
  }
};
Form.IframeSubmit.iframeLoaders = {};
