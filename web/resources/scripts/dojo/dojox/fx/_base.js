/*
	Copyright (c) 2004-2008, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dojox.fx._base"]){
dojo._hasResource["dojox.fx._base"]=true;
dojo.provide("dojox.fx._base");
dojo.require("dojo.fx");
dojo.mixin(dojox.fx,{anim:dojo.anim,animateProperty:dojo.animateProperty,fadeTo:dojo._fade,fadeIn:dojo.fadeIn,fadeOut:dojo.fadeOut,combine:dojo.fx.combine,chain:dojo.fx.chain,slideTo:dojo.fx.slideTo,wipeIn:dojo.fx.wipeIn,wipeOut:dojo.fx.wipeOut});
dojox.fx.sizeTo=function(_1){
var _2=_1.node=dojo.byId(_1.node);
var _3=_1.method||"chain";
if(!_1.duration){
_1.duration=500;
}
if(_3=="chain"){
_1.duration=Math.floor(_1.duration/2);
}
var _4,_5,_6,_7,_8,_9=null;
var _a=(function(n){
return function(){
var cs=dojo.getComputedStyle(n);
var _d=cs.position;
_4=(_d=="absolute"?n.offsetTop:parseInt(cs.top)||0);
_6=(_d=="absolute"?n.offsetLeft:parseInt(cs.left)||0);
_8=parseInt(cs.width);
_9=parseInt(cs.height);
_7=_6-Math.floor((_1.width-_8)/2);
_5=_4-Math.floor((_1.height-_9)/2);
if(_d!="absolute"&&_d!="relative"){
var _e=dojo.coords(n,true);
_4=_e.y;
_6=_e.x;
n.style.position="absolute";
n.style.top=_4+"px";
n.style.left=_6+"px";
}
};
})(_2);
_a();
var _f=dojo.animateProperty(dojo.mixin({properties:{height:{start:_9,end:_1.height||0,unit:"px"},top:{start:_4,end:_5}}},_1));
var _10=dojo.animateProperty(dojo.mixin({properties:{width:{start:_8,end:_1.width||0,unit:"px"},left:{start:_6,end:_7}}},_1));
var _11=dojo.fx[(_1.method=="combine"?"combine":"chain")]([_f,_10]);
dojo.connect(_11,"beforeBegin",_11,_a);
return _11;
};
dojox.fx.slideBy=function(_12){
var _13=_12.node=dojo.byId(_12.node);
var top=null;
var _15=null;
var _16=(function(n){
return function(){
var cs=dojo.getComputedStyle(n);
var pos=cs.position;
top=(pos=="absolute"?n.offsetTop:parseInt(cs.top)||0);
_15=(pos=="absolute"?n.offsetLeft:parseInt(cs.left)||0);
if(pos!="absolute"&&pos!="relative"){
var ret=dojo.coords(n,true);
top=ret.y;
_15=ret.x;
n.style.position="absolute";
n.style.top=top+"px";
n.style.left=_15+"px";
}
};
})(_13);
_16();
var _1b=dojo.animateProperty(dojo.mixin({properties:{top:top+(_12.top||0),left:_15+(_12.left||0)}},_12));
dojo.connect(_1b,"beforeBegin",_1b,_16);
return _1b;
};
dojox.fx.crossFade=function(_1c){
if(dojo.isArray(_1c.nodes)){
var _1d=_1c.nodes[0]=dojo.byId(_1c.nodes[0]);
var op1=dojo.style(_1d,"opacity");
var _1f=_1c.nodes[1]=dojo.byId(_1c.nodes[1]);
var op2=dojo.style(_1f,"opacity");
var _21=dojo.fx.combine([dojo[(op1==0?"fadeIn":"fadeOut")](dojo.mixin({node:_1d},_1c)),dojo[(op1==0?"fadeOut":"fadeIn")](dojo.mixin({node:_1f},_1c))]);
return _21;
}else{
return false;
}
};
dojox.fx.highlight=function(_22){
var _23=_22.node=dojo.byId(_22.node);
_22.duration=_22.duration||400;
var _24=_22.color||"#ffff99";
var _25=dojo.style(_23,"backgroundColor");
var _26=(_25=="transparent"||_25=="rgba(0, 0, 0, 0)")?_25:false;
var _27=dojo.animateProperty(dojo.mixin({properties:{backgroundColor:{start:_24,end:_25}}},_22));
if(_26){
dojo.connect(_27,"onEnd",_27,function(){
_23.style.backgroundColor=_26;
});
}
return _27;
};
dojox.fx.wipeTo=function(_28){
_28.node=dojo.byId(_28.node);
var _29=_28.node,s=_29.style;
var dir=(_28.width?"width":"height");
var _2c=_28[dir];
var _2d={};
_2d[dir]={start:function(){
s.overflow="hidden";
if(s.visibility=="hidden"||s.display=="none"){
s[dir]="1px";
s.display="";
s.visibility="";
return 1;
}else{
var now=dojo.style(_29,dir);
return Math.max(now,1);
}
},end:_2c,unit:"px"};
var _2f=dojo.animateProperty(dojo.mixin({properties:_2d},_28));
return _2f;
};
(function(){
var _30="border",_31="Width",_32="Height",_33="Top",_34="Right",_35="Left",_36="Bottom";
dojox.fx.flip=function(_37){
var _38=dojo.doc.createElement("div");
var _39=_37.node=dojo.byId(_37.node),s=_39.style,_3b=null,hs=null,pn=null,_3e=_37.lightColor||"#dddddd",_3f=_37.darkColor||"#555555",_40=dojo.style(_37.node,"backgroundColor"),_41=_37.endColor||_40,_42={},_43=[],_44=_37.duration?_37.duration/2:250,dir=_37.dir||"left",_46=0.6,_47="transparent";
var _48=function(_49){
return ((new dojo.Color(_49)).toHex()==="#000000")?"#000001":_49;
};
if(dojo.isIE<7){
_41=_48(_41);
_3e=_48(_3e);
_3f=_48(_3f);
_40=_48(_40);
_47="black";
_38.style.filter="chroma(color='#000000')";
}
var _4a=(function(n){
return function(){
var ret=dojo.coords(n,true);
_3b={top:ret.y,left:ret.x,width:ret.w,height:ret.h};
};
})(_39);
_4a();
hs={position:"absolute",top:_3b["top"]+"px",left:_3b["left"]+"px",height:"0",width:"0",zIndex:_37.zIndex||(s.zIndex||0),border:"0 solid "+_47,fontSize:"0"};
_3b["endHeight"]=_3b["height"]*_46;
_3b["endWidth"]=_3b["width"]*_46;
var _4d=[{},{top:_3b["top"],left:_3b["left"]}];
var _4e={left:[_35,_34,_33,_36,_31,_32,"end"+_32,_35],right:[_34,_35,_33,_36,_31,_32,"end"+_32,_35],top:[_33,_36,_35,_34,_32,_31,"end"+_31,_33],bottom:[_36,_33,_35,_34,_32,_31,"end"+_31,_33]};
pn=_4e[dir];
_42[pn[5].toLowerCase()]=_3b[pn[5].toLowerCase()]+"px";
_42[pn[4].toLowerCase()]="0";
_42[_30+pn[1]+_31]=_3b[pn[4].toLowerCase()]+"px";
_42[_30+pn[1]+"Color"]=_40;
var p0=_4d[0];
p0[_30+pn[1]+_31]=0;
p0[_30+pn[1]+"Color"]=_3f;
p0[_30+pn[2]+_31]=_3b[pn[6]]/2;
p0[_30+pn[3]+_31]=_3b[pn[6]]/2;
p0[pn[2].toLowerCase()]=_3b[pn[2].toLowerCase()]-_3b[pn[6]]/8;
p0[pn[7].toLowerCase()]=_3b[pn[7].toLowerCase()]+_3b[pn[4].toLowerCase()]/2;
p0[pn[5].toLowerCase()]=_3b[pn[6]];
var p1=_4d[1];
p1[_30+pn[0]+"Color"]={start:_3e,end:_41};
p1[_30+pn[0]+_31]=_3b[pn[4].toLowerCase()];
p1[_30+pn[2]+_31]=0;
p1[_30+pn[3]+_31]=0;
p1[pn[5].toLowerCase()]={start:_3b[pn[6]],end:_3b[pn[5].toLowerCase()]};
dojo.mixin(hs,_42);
dojo.style(_38,hs);
dojo.body().appendChild(_38);
var _51=function(){
_38.parentNode.removeChild(_38);
s.visibility="visible";
};
_43[1]=dojo.animateProperty({node:_38,duration:_44,properties:p1,onEnd:_51});
_43[0]=dojo.animateProperty({node:_38,duration:_44,properties:p0});
dojo.connect(_43[0],"play",function(){
s.visibility="hidden";
});
return dojo.fx.chain(_43);
};
})();
}
