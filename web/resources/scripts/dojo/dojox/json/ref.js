/*
	Copyright (c) 2004-2008, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dojox.json.ref"]){
dojo._hasResource["dojox.json.ref"]=true;
dojo.provide("dojox.json.ref");
dojo.require("dojo.date.stamp");
dojox.json.ref.resolveJson=function(_1,_2){
_2=_2||{};
var _3=_2.idAttribute||"id";
var _4=_2.idPrefix||"/";
var _5=_2.assignAbsoluteIds;
var _6=_2.index||{};
var _7,_8=[];
var _9=/^(.*\/)?(\w+:\/\/)|[^\/\.]+\/\.\.\/|^.*\/(\/)/;
function walk(it,_b,_c){
var _d,_e,id=it[_3]||_c;
if(id!==undefined){
id=(_4+id).replace(_9,"$2$3");
}
var _10=it;
if(id!==undefined){
if(_5){
it.__id=id;
}
if(_6[id]&&((it instanceof Array)==(_6[id] instanceof Array))){
_10=_6[id];
delete _10.$ref;
_d=true;
}else{
var _11=_2.schemas&&(!(it instanceof Array))&&(_e=id.match(/^(.+\/)[^\.\[]*$/))&&(_e=_2.schemas[_e[1]])&&_e.prototype;
if(_11){
var F=function(){
};
F.prototype=_11;
_10=new F();
}
}
_6[id]=_10;
}
for(var i in it){
if(it.hasOwnProperty(i)){
if((typeof (_e=it[i])=="object")&&_e){
_7=_e.$ref;
if(_7){
var _14=_7.replace(/\\./g,"@").replace(/"[^"\\\n\r]*"/g,"");
if(/[\w\[\]\.\$# \/\r\n\t]/.test(_14)&&!/\=|((^|\W)new\W)/.test(_14)){
delete it[i];
var _15=_7.match(/(^([^\[]*\/)?[^\.\[]*)([\.\[].*)?/);
if((_7=(_15[1]=="$"||_15[1]=="this"||_15[1]=="#")?_1:_6[(_4+_15[1]).replace(_9,"$2$3")])){
try{
_7=_15[3]?eval("ref"+_15[3].replace(/^#/,"").replace(/\.([^\.]+)/g,"[\"$1\"]")):_7;
}
catch(e){
_7=null;
}
}
if(_7){
_e=_7;
}else{
if(!_b){
var _16;
if(!_16){
_8.push(_10);
}
_16=true;
}else{
_e=walk(_e,false,_e.$ref);
_e._loadObject=_2.loader;
}
}
}
}else{
if(!_b){
_e=walk(_e,_8==it,id&&(id+("["+dojo._escapeString(i)+"]")));
}
}
}
it[i]=_e;
if(_10!=it){
var old=_10[i];
_10[i]=_e;
if(_d&&_e!==old){
if(_6.onUpdate){
_6.onUpdate(_10,i,old,_e);
}
}
}
}
}
if(_d){
for(i in _10){
if(!it.hasOwnProperty(i)&&i!="__id"&&!(_10 instanceof Array&&isNaN(i))){
if(_6.onUpdate){
_6.onUpdate(_10,i,_10[i],undefined);
}
delete _10[i];
while(_10 instanceof Array&&_10.length&&_10[_10.length-1]===undefined){
_10.length--;
}
}
}
}else{
if(_6.onLoad){
_6.onLoad(_10);
}
}
return _10;
};
if(_1&&typeof _1=="object"){
_1=walk(_1,false,_2.defaultId);
walk(_8,false);
}
return _1;
};
dojox.json.ref.fromJson=function(str,_19){
function ref(_1a){
return {$ref:_1a};
};
var _1b=eval("("+str+")");
if(_1b){
return this.resolveJson(_1b,_19);
}
return _1b;
};
dojox.json.ref.toJson=function(it,_1d,_1e,_1f){
var _20=dojox.json.ref.useRefs;
_1e=_1e||"";
var _21={};
function serialize(it,_23,_24){
if(typeof it=="object"&&it){
var _25;
if(it instanceof Date){
return "\""+dojo.date.stamp.toISOString(it,{zulu:true})+"\"";
}
var id=it.__id;
if(id){
if(_23!="#"&&(_20||_21[id])){
var ref=id;
if(id.charAt(0)!="#"){
if(id.substring(0,_1e.length)==_1e){
ref=id.substring(_1e.length);
}else{
ref=id;
}
}
return serialize({$ref:ref});
}
_23=id;
}else{
it.__id=_23;
_21[_23]=it;
}
_24=_24||"";
var _28=_1d?_24+dojo.toJsonIndentStr:"";
var _29=_1d?"\n":"";
var sep=_1d?" ":"";
if(it instanceof Array){
var res=dojo.map(it,function(obj,i){
var val=serialize(obj,_23+"["+i+"]",_28);
if(typeof val!="string"){
val="undefined";
}
return _29+_28+val;
});
return "["+res.join(","+sep)+_29+_24+"]";
}
var _2f=[];
if(_23&&!_23.match(/#/)){
_23+="#";
}
for(var i in it){
if(it.hasOwnProperty(i)){
var _31;
if(typeof i=="number"){
_31="\""+i+"\"";
}else{
if(typeof i=="string"&&i.charAt(0)!="_"){
_31=dojo._escapeString(i);
}else{
continue;
}
}
var val=serialize(it[i],_23+(i.match(/^[a-zA-Z]\w*$/)?("."+i):("["+dojo._escapeString(i)+"]")),_28);
if(typeof val!="string"){
continue;
}
_2f.push(_29+_28+_31+":"+sep+val);
}
}
return "{"+_2f.join(","+sep)+_29+_24+"}";
}else{
if(typeof it=="function"&&dojox.json.ref.serializeFunctions){
return it.toString();
}
}
return dojo.toJson(it);
};
var _33=serialize(it,"#","");
if(!_1f){
for(i in _21){
delete _21[i].__id;
}
}
return _33;
};
dojox.json.ref.useRefs=false;
dojox.json.ref.serializeFunctions=false;
}
