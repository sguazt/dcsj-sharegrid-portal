/*
	Copyright (c) 2004-2008, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dojox.rpc.JsonRest"]){
dojo._hasResource["dojox.rpc.JsonRest"]=true;
dojo.provide("dojox.rpc.JsonRest");
dojo.require("dojox.json.ref");
dojo.require("dojox.rpc.Rest");
(function(){
var _1=[];
var _2=dojox.rpc.Rest;
var _3=/(.*?)((\.\w+)|(\[.+))+$/;
var jr=dojox.rpc.JsonRest={commit:function(_5){
var _6;
_5=_5||{};
var _7=[];
var _8={};
var _9=[];
for(var i=0;i<_1.length;i++){
var _b=_1[i];
var _c=_b.object;
var _d=false;
if(!(_5.service&&(_c||_b.old).__id.indexOf(_5.service.servicePath))){
if(_c&&!_b.old){
_7.push({method:"post",target:{__id:jr.getServiceAndId(_c.__id).service.servicePath},content:_c});
}else{
if(!_c&&_b.old){
_7.push({method:"delete",target:_b.old});
}else{
while(!(dojox.json&&dojox.json.ref&&dojox.json.ref.useRefs)&&_c.__id.match(_3)){
var _e=_c.__id.match(_3)[1];
_c=_8[_e]=_2._index[_e];
}
if(!(_c.__id in _8)){
_8[_c.__id]=_c;
_7.push({method:"put",target:_c,content:_c});
}
}
}
_9.push(_b);
_1.splice(i--,1);
}
}
var _f;
var _10=dojo.xhr;
_6=_7.length;
var _11;
dojo.xhr=function(_12,_13){
_13.headers=_13.headers||{};
_13.headers["X-Transaction"]=_7.length-1==i?"commit":"open";
if(_11){
_13.headers["Content-Location"]=_11;
}
return _10.apply(dojo,arguments);
};
for(i=0;i<_7.length;i++){
var _14=_7[i];
dojox.rpc.JsonRest._contentId=_14.content&&_14.content.__id;
var _15=_14.method=="post";
_11=_15&&dojox.rpc.JsonRest._contentId;
var _16=jr.getServiceAndId(_14.target.__id);
var _17=_16.service;
var dfd=_14.deferred=_17[_14.method](_16.id,dojox.json.ref.toJson(_14.content,false,_17.servicePath,true));
var _19=jr.schemas[_17.servicePath];
(function(_1a,dfd,_1c){
dfd.addCallback(function(_1d){
try{
var _1e=dfd.ioArgs.xhr.getResponseHeader("Location");
if(_1e){
_1a.__id=_1e;
var _1f=_1e.match(/\/([^\/]*)$/);
if(_1c&&_1f){
_1a[_1c]=_1f[1];
}
_2._index[_1e]=_1a;
}
}
catch(e){
}
if(!(--_6)){
if(_5.onComplete){
_5.onComplete.call(_5.scope);
}
}
});
})(_15&&_14.content,dfd,_19&&_19._idAttr);
dfd.addErrback(function(_20){
_6=-1;
var _21=_1;
dirtyObject=_9;
numDirty=0;
jr.revert();
_1=_21;
if(_5.onError){
_5.onError();
}
return _20;
});
}
dojo.xhr=_10;
return _7;
},getDirtyObjects:function(){
return _1;
},revert:function(){
while(_1.length>0){
var d=_1.pop();
if(d.object&&d.old){
for(var i in d.old){
if(d.old.hasOwnProperty(i)){
d.object[i]=d.old[i];
}
}
for(i in d.object){
if(!d.old.hasOwnProperty(i)){
delete d.object[i];
}
}
}
}
},changing:function(_24,_25){
if(!_24.__id){
return;
}
for(var i=0;i<_1.length;i++){
if(_24==_1[i].object){
return;
}
}
var old=_24 instanceof Array?[]:{};
for(i in _24){
if(_24.hasOwnProperty(i)){
old[i]=_24[i];
}
}
_1.push({object:!_25&&_24,old:old});
},deleteObject:function(_28){
this.changing(_28,true);
},getConstructor:function(_29,_2a){
if(typeof _29=="string"){
var _2b=_29;
_29=new dojox.rpc.Rest(_29,true);
this.registerService(_29,_2b,_2a);
}
if(_29._constructor){
return _29._constructor;
}
_29._constructor=function(_2c){
if(_2c){
dojo.mixin(this,_2c);
}
var _2d=jr.getIdAttribute(_29);
_2._index[this.__id=this.__clientId=_29.servicePath+(this[_2d]||(this[_2d]=Math.random().toString(16).substring(2,14)+Math.random().toString(16).substring(2,14)))]=this;
_1.push({object:this});
};
return dojo.mixin(_29._constructor,_29._schema,{load:_29});
},fetch:function(_2e){
var _2f=jr.getServiceAndId(_2e);
return this.byId(_2f.service,_2f.id);
},getIdAttribute:function(_30){
var _31=_30._schema;
var _32;
if(_31){
if(!(_32=_31._idAttr)){
for(var i in _31.properties){
if(_31.properties[i].identity){
_31._idAttr=_32=i;
}
}
}
}
return _32||"id";
},getServiceAndId:function(_34){
var _35=_34.match(/^(.*\/)([^\/]*)$/);
var svc=jr.services[_35[1]]||new dojox.rpc.Rest(_35[1],true);
return {service:svc,id:_35[2]};
},services:{},schemas:{},registerService:function(_37,_38,_39){
_38=_38||_37.servicePath;
_38=_37.servicePath=_38.match(/\/$/)?_38:(_38+"/");
_37._schema=jr.schemas[_38]=_39||_37._schema||{};
jr.services[_38]=_37;
},byId:function(_3a,id){
var _3c,_3d=_2._index[(_3a.servicePath||"")+id];
if(_3d&&!_3d._loadObject){
_3c=new dojo.Deferred();
_3c.callback(_3d);
return _3c;
}
return this.query(_3a,id);
},query:function(_3e,id,_40){
var _41=_3e(id,_40);
_41.addCallback(function(_42){
if(_42.nodeType&&_42.cloneNode){
return _42;
}
return dojox.json.ref.resolveJson(_42,{defaultId:typeof id!="string"||(_40&&(_40.start||_40.count))?undefined:id,index:_2._index,idPrefix:_3e.servicePath,idAttribute:jr.getIdAttribute(_3e),schemas:jr.schemas,loader:jr._loader,assignAbsoluteIds:true});
});
return _41;
},_loader:function(_43){
var _44=jr.getServiceAndId(this.__id);
var _45=this;
jr.query(_44.service,_44.id).addBoth(function(_46){
if(_46==_45){
delete _46.$ref;
delete _46._loadObject;
}else{
_45._loadObject=function(_47){
_47(_46);
};
}
_43(_46);
});
},isDirty:function(_48){
for(var i=0,l=_1.length;i<l;i++){
if(_1[i].object==_48){
return true;
}
}
return false;
}};
})();
}
