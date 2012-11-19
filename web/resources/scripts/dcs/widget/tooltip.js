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
var Dcs = {};
Dcs.Widget = {};
Dcs.Widget.ToolTip = function(element, options)
{
	//this._panel = this._createForPrototip2(element, options);
	this._panel = this._createForExt2(element, options);
	return this;
}
Dcs.Widget.ToolTip.prototype = {
	Version: '1.0.1',
	_createForPrototip2: function(element, options) {
		return new Tip(
			element,
			options.contentBody,
			{
				border: options.borderWidth != undefined
					? Math.min(Math.max(1, options.borderWidth), 6); // 1 <= width <= 6
					: 6,
				closeButton: options.closable,
				delay: options.showDelay != undefined
					? options.showDelay
					: undefined,
				hideOn: options.autoHide
					? 'mouseout'
					: (options.closable ? { element: 'closeButton', event: 'click' } : undefined) ,
				hideAfter: options.hideDelay != undefined
					? options.hideDelay
					: undefined,
				//hideOn: { element: 'closeButton', event: 'click' },
				hideOthers: options.hideOthers != undefined
					? options.hideOthers
					: false,
				radius: options.cornerRadius != undefined
					? options.cornerRadius
					? Math.min(Math.max(1, options.cornerRadius), 6); // 1 <= radius <= 6
					: options.borderWidth, // if undefined, set inside prototip2
				showOn: options.showOn != undefined
					? options.showOn
					: 'mousemove',
				style: 'default',
				title: options.caption,
				//target: options.targetId,
				width: options.width != undefined
					? options.width
					: 'auto'
			}
		);
	},
	_createForExt2: function(element, options) {
		Ext.onReady(
			function() {
				var win;
				var source = Ext.get(element);

				source.on(
					options.showOn != undefined ? options.showOn : 'click',
					// create the window on the first click and reuse on subsequent clicks
					if (!win)
					{
						win = new Ext.Window({
							//applyTo: 'my-target',
							title: options.caption,
							layout: 'fit',
							width: 500,
							height: 300,
							closeAction:'hide',
							plain: true,
							modal: false,
							items: [{
								html: options.contentBody,
							}],
							buttons: [{
								text: 'Close',
								handler: function(){
									win.hide();
								}
							}]
						});
					}
					win.show(source);
				);
			}
		);
	}
};

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
