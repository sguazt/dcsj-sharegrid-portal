Dcs.Widget = function() {
	return  this instanceof Dcs.Widget
		?  this.init()
		: new Dcs.Widget();
};
Dcs.Widget.fn = Dcs.Widget.prototype = {
	Version: '1.0.0',
	init: function() {
		return this;
	}
};
