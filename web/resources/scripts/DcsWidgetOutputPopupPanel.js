Dcs.Namespace('Dcs.Widget');
//Dcs.Use(
//	'prototype',
//	'prototip'
//);
Dcs.UseModule('Dcs.Widget');

//Dcs.Widget.HelpOutputPopupPanel = function(element) {
//	return new Dcs.Widget.OutputPopupPanel(
//			element,
//			{
//				caption: 'Context-sensitive Help',
//				contentBody: 'Ciao da Gennaro e Ciro',
//				closeButton: true,
//				border: 1,
//				hideOthers: true
//			}
//	);
//};

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
Dcs.Widget.OutputPopupPanel = function(element, options) {
	return  this instanceof Dcs.Widget.OutputPopupPanel
		?  this.init(element, options)
		: new Dcs.Widget.OutputPopupPanel(element, options);
};
Dcs.Widget.OutputPopupPanel.fn = Dcs.Widget.OutputPopupPanel.prototype = {
	Version: '1.0.1',
//	Create: function(element, context) {
//		return this.init(element, context);
//	},
	init: function(element, options) {
		alert("BorderWidth: " + options.borderWidth);
		this._panel = this._initForPrototip2(element, options);
		return this;
	},
	_initForPrototip2: function(element, options) {
		return new Tip(
			element,
			options.contentBody,
			{
				border: options.borderWidth != undefined
					? options.borderWidth
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
	}
};
