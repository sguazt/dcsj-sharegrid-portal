/*
	Copyright (c) 2004-2008, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dojox.editor.plugins.ToggleDir"]){
dojo._hasResource["dojox.editor.plugins.ToggleDir"]=true;
dojo.provide("dojox.editor.plugins.ToggleDir");
dojo.experimental("dojox.editor.plugins.ToggleDir");
dojo.require("dojox.editor._Plugin");
dojo.declare("dojox.editor.plugins.ToggleDir",dojox.editor._Plugin,{useDefaultCommand:false,command:"toggleDir",_initButton:function(){
this.inherited("_initButton",arguments);
this.connect(this.button,"onClick",this._toggleDir);
},updateState:function(){
},_toggleDir:function(){
var _1=this.editor.editorObject.contentWindow.document.documentElement;
var _2=dojo.getComputedStyle(_1).direction=="ltr";
_1.dir=_2?"rtl":"ltr";
}});
dojo.subscribe(dijit._scopeName+".Editor.getPlugin",null,function(o){
if(o.plugin){
return;
}
switch(o.args.name){
case "toggleDir":
o.plugin=new dojox.editor.plugins.ToggleDir({command:o.args.name});
}
});
}
