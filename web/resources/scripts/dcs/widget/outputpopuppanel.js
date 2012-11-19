//alert("In Dcs.Widget.OutputPopupPanel");//XXX
//Dcs.UseModule('Dcs.Widget');
//Dcs.UseScript('prototype.js');
//Dcs.UseScript('prototip/js/prototip.js');
//Dcs.UseCss('prototip/css/prototip.css');

/**
 * Options:
 *
 * autoHide: When true the tooltip is automatically hidden after the mouse exits
 *   the target element or after the hideDelay has expired.
 *   Default: true.
 * border: The size of the border in pixels. Use 'none' for no border.
 *   Default: 6.
 * caption: A string with the title of the tooltip.
 *   Default: undefined.
 * closable: true or false. When true, a close button is shown.
 *   Default: false.
 * hideDelay: Hide the tooltip after a time of inactivity in seconds. This means
 *   your tooltip will hide after the element or the tooltip is not hovered for
 *   this duration.
 *   Default: undefined.
 * hideOthers: When true, will hide all other tooltips before opening.
 *   Default: false.
 * showDelay: Delay when showing the tooltip in seconds.
 *   Default 0.14, so you can safely hover you page.
 * showOn: The event that shows the tooltip.
 *   Default: 'mousemove'.
 * targetId: An id of the element that will function as the target for your
 *   tooltip.
 *   Default: the element in the tip call.
 * width: An integer to set the width of the tooltip in pixels, a css string
 *   like 'auto', or false.
 *   Default: false, sets the width defined in the CSS.
 *
 */
//if (typeof undefined == 'undefined')
//{
//	undefined = 'undefined';
//}
if (typeof Dcs == 'undefined')
{
	Dcs = {};
}
if (typeof Dcs.Widget == 'undefined')
{
	Dcs.Widget = {};
}
if (typeof Dcs.Widget.OutputPopupPanel == 'undefined')
{
	Dcs.Widget.OutputPopupPanel = function(options)
	{
		//this._panel = this._createForPrototip2(element, options);

		// private functions
		this._gP = function(/*Object*/ p, /*String*/ n, /*Mixed*/ v) {
			return n in p ? p[n] : v;
		};
		this._gPIf = function(/*Object*/ p, /*String*/ n, /*Mixed*/ v, /*Function*/ test) {
			return (n in p && test(p[n])) ? p[n] : v;
		};
		this._eP = function(/*Object*/ p, /*String*/ n, /*Mixed*/ v) {
			return n in p;
		};

		this._createForExt2(options);
	}

	Dcs.Widget.OutputPopupPanel.prototype = {
		Version: '1.0.1',
		_createForExt2: function(options) {
			var win;
			var source = Ext.get(this._gP(options, 'showOnId', ''));

			var _this = this;
			source.on(
				_this._gP(options, 'showOn', 'click'),
				function(){
					// create the window on the first click and reuse on subsequent clicks
					if(!win){
						win = new Ext.Window({
							border:_this._gP(options, 'border', true),
							closable:_this._gP(options, 'closable', true),
							closeAction:_this._gPIf(options, 'closeAction', 'hide', function(vv) { return vv in ['hide','close']; }),
//							buttons: [{
//								text: 'Close', //NO-I18N
//								handler: function(){
//									win.hide();
//								}
//							}],
							height: _this._gPIf(options, 'height', 'auto', function(vv) { return vv >= 0; }),
							items: [{
								html: _this._gP(options, 'bodyContent', '')
							}],
							layout: 'fit',
							maximizable: _this._gP(options, 'maximizable', false),
							minimizable: _this._gP(options, 'minimizable', false),
							modal: _this._gP(options, 'modal', false),
							plain: false,
							resizable: _this._gP(options, 'resizable', true),
							title: _this._gP(options, 'headerTitle', ''),
							width: _this._gPIf(options, 'width', 'auto', function(vv) { return vv >= 0; })
						});
					}
					win.show(source);
				}
			);
		}
	}
}

//	if (this instanceof Dcs.Widget.OutputPopupPanel)
//	{
//alert("OutputPopupPanel constructor>> already defined: " + Dcs.Widget.OutputPanel.prototype);//XXX
//		this.init(element, options);
//	}
//	else
//	{
//alert("OutputPopupPanel constructor>> not yet defined");//XXX
//		new Dcs.Widget.OutputPopupPanel();
//	}


//Dcs.Widget.OutputPopupPanel = function(element, options) {
//	return  this instanceof Dcs.Widget.OutputPopupPanel
//		?  this.init(element, options)
//		: new Dcs.Widget.OutputPopupPanel(element, options);
//};

/*
Dcs.Widget.OutputPopupPanel = function(element, options) {
	if (this instanceof Dcs.Widget.OutputPopupPanel)
	{
		return this.init(element, options);
	}
	else
	{
		return new function Dcs.Widget.OutputPopupPanel(element, options)
		{
			this.init(element, options);
		}
	}
};
*/
