/*
	Copyright (c) 2004-2008, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dojox.data.JsonRestStore"]){
dojo._hasResource["dojox.data.JsonRestStore"]=true;
dojo.provide("dojox.data.JsonRestStore");
dojo.require("dojox.data.ServiceStore");
dojo.require("dojox.rpc.JsonRest");
dojo.declare("dojox.data.JsonRestStore",dojox.data.ServiceStore,{constructor:function(_1){
dojo.connect(dojox.rpc.Rest._index,"onUpdate",this,function(_2,_3,_4,_5){
var _6=this.service.servicePath;
if(!_2.__id){

}else{
if(_2.__id.substring(0,_6.length)==_6){
this.onSet(_2,_3,_4,_5);
}
}
});
this.idAttribute=this.idAttribute||"id";
if(typeof _1.target=="string"&&!this.service){
this.service=dojox.rpc.Rest(this.target,true);
}
dojox.rpc.JsonRest.registerService(this.service,_1.target,this.schema);
this.schema=this.service._schema=this.schema||this.service._schema||{};
this.service._store=this;
this.schema._idAttr=this.idAttribute;
this._constructor=dojox.rpc.JsonRest.getConstructor(this.service);
this._index=dojox.rpc.Rest._index;
},target:"",newItem:function(_7,_8){
_7=new this._constructor(_7);
if(_8){
var _9=this.getValue(_8.parent,_8.attribute,[]);
this.setValue(_8.parent,_8.attribute,_9.concat([_7]));
}
this.onNew(_7);
return _7;
},deleteItem:function(_a){
dojox.rpc.JsonRest.deleteObject(_a);
var _b=dojox.data._getStoreForItem(_a);
_b._doDelete(_a);
},_doDelete:function(_c){
this.onDelete(_c);
},changing:function(_d,_e){
dojox.rpc.JsonRest.changing(_d,_e);
},setValue:function(_f,_10,_11){
var old=_f[_10];
var _13=_f.__id?dojox.data._getStoreForItem(_f):this;
if(dojox.json.schema&&_13.schema&&_13.schema.properties){
var _14=dojox.json.schema.checkPropertyChange(_11,_13.schema.properties[_10]);
if(!_14.valid){
throw new Error(dojo.map(_14.errors,function(_15){
return _15.message;
}).join(","));
}
}
if(old!=_11){
_13.changing(_f);
_f[_10]=_11;
_13.onSet(_f,_10,old,_11);
}
},setValues:function(_16,_17,_18){
if(!dojo.isArray(_18)){
throw new Error("setValues expects to be passed an Array object as its value");
}
this.setValue(_16,_17,_18);
},unsetAttribute:function(_19,_1a){
this.changing(_19);
var old=_19[_1a];
delete _19[_1a];
this.onSet(_19,_1a,old,undefined);
},save:function(_1c){
if(!(_1c&&_1c.global)){
(_1c=_1c||{}).service=this.service;
}
var _1d=dojox.rpc.JsonRest.commit(_1c);
this.serverVersion=this._updates&&this._updates.length;
return _1d;
},revert:function(){
var _1e=dojox.rpc.JsonRest.getDirtyObjects().concat([]);
while(_1e.length>0){
var d=_1e.pop();
var _20=dojox.data._getStoreForItem(d.object||d.old);
if(!d.object){
_20.onNew(d.old);
}else{
if(!d.old){
_20.onDelete(d.object);
}else{
for(var i in d.object){
if(d.object[i]!=d.old[i]){
_20.onSet(d.object,i,d.object[i],d.old[i]);
}
}
}
}
}
dojox.rpc.JsonRest.revert();
},isDirty:function(_22){
return dojox.rpc.JsonRest.isDirty(_22);
},isItem:function(_23){
return _23&&_23.__id&&this.service==dojox.rpc.JsonRest.getServiceAndId(_23.__id).service;
},_doQuery:function(_24){
var _25=typeof _24.queryStr=="string"?_24.queryStr:_24.query;
return dojox.rpc.JsonRest.query(this.service,_25,_24);
},_processResults:function(_26,_27){
var _28=_26.length;
return {totalCount:_27.fullLength||(_27.request.count==_28?_28*2:_28),items:_26};
},getConstructor:function(){
return this._constructor;
},getIdentity:function(_29){
var id=_29.__clientId||_29.__id;
if(!id){
this.inherited(arguments);
}
var _2b=this.service.servicePath;
return id.substring(0,_2b.length)!=_2b?id:id.substring(_2b.length);
},fetchItemByIdentity:function(_2c){
var id=_2c.identity;
var _2e=this;
if(id.match(/^(\w*:)?\//)){
var _2f=dojox.rpc.JsonRest.getServiceAndId(id);
_2e=_2f.service._store;
_2c.identity=_2f.id;
}
_2c._prefix=_2e.service.servicePath;
_2e.inherited(arguments);
},onSet:function(){
},onNew:function(){
},onDelete:function(){
},getFeatures:function(){
var _30=this.inherited(arguments);
_30["dojo.data.api.Write"]=true;
_30["dojo.data.api.Notification"]=true;
return _30;
}});
dojox.data._getStoreForItem=function(_31){
return dojox.rpc.JsonRest.services[_31.__id.match(/.*\//)[0]]._store;
};
}
