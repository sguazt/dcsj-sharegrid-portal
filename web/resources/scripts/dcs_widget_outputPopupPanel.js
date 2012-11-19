//(function(){

var Dcs = {
	Version: '1.0.0',
	init: function() {
		return this;
	}
};

var Dcs.Widget = {
	Version: '1.0.0',
	init: function() {
		return this;
	}
};

var Dcs.Widget.OutputPopupPanel = {
	Version: '1.0.1',
//	Create: function(element, context) {
//		return this.init(element, context);
//	},
	init: function(element, context) {
		this._panel = new Tip(
			element,
			context.contentBody,
			{
				closeButton: !context.autoHide,
				title: context.contentTitle,
				target: context.targetId,
				width: context.width != undefined ? context.width : 'auto',
				style: 'protoblue'
			}
		);
		return this;
	}
}

//})();
