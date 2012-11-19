/*
	Copyright (c) 2004-2008, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dojox.grid._Grid"]){
dojo._hasResource["dojox.grid._Grid"]=true;
dojo.provide("dojox.grid._Grid");
dojo.require("dojox.html.metrics");
dojo.require("dojox.grid.util");
dojo.require("dojox.grid._Scroller");
dojo.require("dojox.grid._Layout");
dojo.require("dojox.grid._View");
dojo.require("dojox.grid._ViewManager");
dojo.require("dojox.grid._RowManager");
dojo.require("dojox.grid._FocusManager");
dojo.require("dojox.grid._EditManager");
dojo.require("dojox.grid.Selection");
dojo.require("dojox.grid._RowSelector");
dojo.require("dojox.grid._Events");
dojo.require("dijit.Menu");
dojo.requireLocalization("dijit","loading",null,"zh,ca,pt,da,tr,ru,ROOT,de,sv,ja,he,fi,nb,el,ar,pt-pt,cs,fr,es,ko,nl,zh-tw,pl,th,it,hu,sk,sl");
dojo.requireLocalization("dojox.grid","grid",null,"ROOT");
(function(){
var _1={cancel:function(_2){
if(_2){
clearTimeout(_2);
}
},jobs:[],job:function(_3,_4,_5){
_1.cancelJob(_3);
var _6=function(){
delete _1.jobs[_3];
_5();
};
_1.jobs[_3]=setTimeout(_6,_4);
},cancelJob:function(_7){
_1.cancel(_1.jobs[_7]);
}};
dojo.declare("dojox.grid._Grid",[dijit._Widget,dijit._Templated,dojox.grid._Events],{templateString:"<div class=\"dojoxGrid\" hidefocus=\"hidefocus\" role=\"wairole:grid\" dojoAttachEvent=\"onmouseout:_mouseOut\">\n\t<div class=\"dojoxGridMasterHeader\" dojoAttachPoint=\"viewsHeaderNode\" tabindex=\"-1\"></div>\n\t<div class=\"dojoxGridMasterView\" dojoAttachPoint=\"viewsNode\"></div>\n\t<div class=\"dojoxGridMasterMessages\" style=\"display: none;\" dojoAttachPoint=\"messagesNode\"></div>\n\t<span dojoAttachPoint=\"lastFocusNode\" tabindex=\"0\"></span>\n</div>\n",classTag:"dojoxGrid",get:function(_8){
},rowCount:5,keepRows:75,rowsPerPage:25,autoWidth:false,autoHeight:"",autoRender:true,defaultHeight:"15em",height:"",structure:"",elasticView:-1,singleClickEdit:false,selectionMode:"extended",rowSelector:"",columnReordering:false,headerMenu:null,placeholderLabel:"GridColumns",_click:null,loadingMessage:"<span class='dojoxGridLoading'>${loadingState}</span>",errorMessage:"<span class='dojoxGridError'>${errorState}</span>",noDataMessage:"<span class='dojoxGridNoData'>${noData}</span>",sortInfo:0,themeable:true,_placeholders:null,buildRendering:function(){
this.inherited(arguments);
if(this.get==dojox.grid._Grid.prototype.get){
this.get=null;
}
if(!this.domNode.getAttribute("tabIndex")){
this.domNode.tabIndex="0";
}
this.createScroller();
this.createLayout();
this.createViews();
this.createManagers();
this.createSelection();
this.connect(this.selection,"onSelected","onSelected");
this.connect(this.selection,"onDeselected","onDeselected");
this.connect(this.selection,"onChanged","onSelectionChanged");
dojox.html.metrics.initOnFontResize();
this.connect(dojox.html.metrics,"onFontResize","textSizeChanged");
dojox.grid.util.funnelEvents(this.domNode,this,"doKeyEvent",dojox.grid.util.keyEvents);
this.connect(this,"onShow","renderOnIdle");
},postMixInProperties:function(){
this.inherited(arguments);
var _9=dojo.mixin(dojo.i18n.getLocalization("dijit","loading",this.lang),dojo.i18n.getLocalization("dojox.grid","grid",this.lang));
this.loadingMessage=dojo.string.substitute(this.loadingMessage,_9);
this.errorMessage=dojo.string.substitute(this.errorMessage,_9);
this.noDataMessage=dojo.string.substitute(this.noDataMessage,_9);
this._setAutoHeightAttr(this.autoHeight,true);
},postCreate:function(){
this.styleChanged=this._styleChanged;
this._placeholders=[];
this.setHeaderMenu(this.headerMenu);
this.setStructure(this.structure);
this._click=[];
},destroy:function(){
this.domNode.onReveal=null;
this.domNode.onSizeChange=null;
this.edit.destroy();
delete this.edit;
this.views.destroyViews();
if(this.scroller){
this.scroller.destroy();
delete this.scroller;
}
if(this.focus){
this.focus.destroy();
delete this.focus;
}
if(this.headerMenu&&this._placeholders.length){
dojo.forEach(this._placeholders,function(p){
p.unReplace(true);
});
this.headerMenu.unBindDomNode(this.viewsHeaderNode);
}
this.inherited(arguments);
},_setAutoHeightAttr:function(ah,_c){
if(typeof ah=="string"){
if(!ah||ah=="false"){
ah=false;
}else{
if(ah=="true"){
ah=true;
}else{
ah=window.parseInt(ah,10);
if(isNaN(ah)){
ah=false;
}
if(ah<0){
ah=true;
}else{
if(ah===0){
ah=false;
}
}
}
}
}
this.autoHeight=ah;
if(typeof ah=="boolean"){
this._autoHeight=ah;
}else{
if(typeof ah=="number"){
this._autoHeight=(ah>=this.rowCount);
}else{
this._autoHeight=false;
}
}
if(this._started&&!_c){
this.render();
}
},styleChanged:function(){
this.setStyledClass(this.domNode,"");
},_styleChanged:function(){
this.styleChanged();
this.update();
},textSizeChanged:function(){
setTimeout(dojo.hitch(this,"_textSizeChanged"),1);
},_textSizeChanged:function(){
if(this.domNode){
this.views.forEach(function(v){
v.content.update();
});
this.render();
}
},sizeChange:function(){
_1.job(this.id+"SizeChange",50,dojo.hitch(this,"update"));
},renderOnIdle:function(){
setTimeout(dojo.hitch(this,"render"),1);
},createManagers:function(){
this.rows=new dojox.grid._RowManager(this);
this.focus=new dojox.grid._FocusManager(this);
this.edit=new dojox.grid._EditManager(this);
},createSelection:function(){
this.selection=new dojox.grid.Selection(this);
},createScroller:function(){
this.scroller=new dojox.grid._Scroller();
this.scroller.grid=this;
this.scroller._pageIdPrefix=this.id+"-";
this.scroller.renderRow=dojo.hitch(this,"renderRow");
this.scroller.removeRow=dojo.hitch(this,"rowRemoved");
},createLayout:function(){
this.layout=new dojox.grid._Layout(this);
this.connect(this.layout,"moveColumn","onMoveColumn");
},onMoveColumn:function(){
this.render();
this._resize();
},createViews:function(){
this.views=new dojox.grid._ViewManager(this);
this.views.createView=dojo.hitch(this,"createView");
},createView:function(_e,_f){
var c=dojo.getObject(_e);
var _11=new c({grid:this,index:_f});
this.viewsNode.appendChild(_11.domNode);
this.viewsHeaderNode.appendChild(_11.headerNode);
this.views.addView(_11);
return _11;
},buildViews:function(){
for(var i=0,vs;(vs=this.layout.structure[i]);i++){
this.createView(vs.type||dojox._scopeName+".grid._View",i).setStructure(vs);
}
this.scroller.setContentNodes(this.views.getContentNodes());
},setStructure:function(_14){
var s=_14;
if(s&&dojo.isString(s)){
s=dojo.getObject(s);
}
if(!s){
if(this.layout.structure){
s=this.layout.structure;
}else{
return;
}
}
this.views.destroyViews();
if(s!==this.layout.structure){
this.layout.setStructure(s);
}
this._structureChanged();
},getColumnTogglingItems:function(){
return dojo.map(this.layout.cells,function(_16){
if(!_16.menuItems){
_16.menuItems=[];
}
var _17=this;
var _18=new dijit.CheckedMenuItem({label:_16.name,checked:!_16.hidden,_gridCell:_16,onChange:function(_19){
if(_17.layout.setColumnVisibility(this._gridCell.index,_19)){
var _1a=this._gridCell.menuItems;
if(_1a.length>1){
dojo.forEach(_1a,function(_1b){
if(_1b!==this){
_1b.setAttribute("checked",_19);
}
},this);
}
var _19=dojo.filter(_17.layout.cells,function(c){
if(c.menuItems.length>1){
dojo.forEach(c.menuItems,"item.attr('disabled', false);");
}else{
c.menuItems[0].attr("disabled",false);
}
return !c.hidden;
});
if(_19.length==1){
dojo.forEach(_19[0].menuItems,"item.attr('disabled', true);");
}
}
},destroy:function(){
var _1d=dojo.indexOf(this._gridCell.menuItems,this);
this._gridCell.menuItems.splice(_1d,1);
delete this._gridCell;
dijit.CheckedMenuItem.prototype.destroy.apply(this,arguments);
}});
_16.menuItems.push(_18);
return _18;
},this);
},setHeaderMenu:function(_1e){
if(this._placeholders.length){
dojo.forEach(this._placeholders,function(p){
p.unReplace(true);
});
this._placeholders=[];
}
if(this.headerMenu){
this.headerMenu.unBindDomNode(this.viewsHeaderNode);
}
this.headerMenu=_1e;
if(!_1e){
return;
}
this.headerMenu.bindDomNode(this.viewsHeaderNode);
if(this.headerMenu.getPlaceholders){
this._placeholders=this.headerMenu.getPlaceholders(this.placeholderLabel);
}
},setupHeaderMenu:function(){
if(this._placeholders&&this._placeholders.length){
dojo.forEach(this._placeholders,function(p){
if(p._replaced){
p.unReplace(true);
}
p.replace(this.getColumnTogglingItems());
},this);
}
},_fetch:function(_21){
this.setScrollTop(0);
},getItem:function(_22){
return null;
},showMessage:function(_23){
if(_23){
this.messagesNode.innerHTML=_23;
this.messagesNode.style.display="";
}else{
this.messagesNode.innerHTML="";
this.messagesNode.style.display="none";
}
},_structureChanged:function(){
this.buildViews();
if(this.autoRender&&this._started){
this.render();
}
},hasLayout:function(){
return this.layout.cells.length;
},resize:function(_24){
this._sizeBox=_24;
this._resize();
this.sizeChange();
},_getPadBorder:function(){
this._padBorder=this._padBorder||dojo._getPadBorderExtents(this.domNode);
return this._padBorder;
},_getHeaderHeight:function(){
var vns=this.viewsHeaderNode.style,t=vns.display=="none"?0:this.views.measureHeader();
vns.height=t+"px";
this.views.normalizeHeaderNodeHeight();
return t;
},_resize:function(){
var pn=this.domNode.parentNode;
if(!pn||pn.nodeType!=1||!this.hasLayout()||pn.style.visibility=="hidden"||pn.style.display=="none"){
return;
}
var _28=this._getPadBorder();
if(this._autoHeight){
this.domNode.style.height="auto";
this.viewsNode.style.height="";
}else{
if(typeof this.autoHeight=="number"){
var h=this._getHeaderHeight();
h+=(this.scroller.averageRowHeight*this.autoHeight);
this.domNode.style.height=h+"px";
}else{
if(this.flex>0){
}else{
if(this.domNode.clientHeight<=_28.h){
if(pn==document.body){
this.domNode.style.height=this.defaultHeight;
}else{
if(this.height){
this.domNode.style.height=this.height;
}else{
this.fitTo="parent";
}
}
}
}
}
}
if(this._sizeBox){
dojo.contentBox(this.domNode,this._sizeBox);
}else{
if(this.fitTo=="parent"){
var h=dojo._getContentBox(pn).h;
dojo.marginBox(this.domNode,{h:Math.max(0,h)});
}
}
var h=dojo._getContentBox(this.domNode).h;
if(h==0&&!this._autoHeight){
this.viewsHeaderNode.style.display="none";
}else{
this.viewsHeaderNode.style.display="block";
this._getHeaderHeight();
}
this.adaptWidth();
this.adaptHeight();
this.postresize();
},adaptWidth:function(){
var w=this.autoWidth?0:this.domNode.clientWidth||(this.domNode.offsetWidth-this._getPadBorder().w),vw=this.views.arrange(1,w);
this.views.onEach("adaptWidth");
if(this.autoWidth){
this.domNode.style.width=vw+"px";
}
},adaptHeight:function(){
var t=this._getHeaderHeight();
var h=(this._autoHeight?-1:Math.max(this.domNode.clientHeight-t,0)||0);
this.views.onEach("setSize",[0,h]);
this.views.onEach("adaptHeight");
if(!this._autoHeight){
var _2e=0,_2f=0;
var _30=dojo.filter(this.views.views,function(v){
var has=v.hasHScrollbar();
if(has){
_2e++;
}else{
_2f++;
}
return (!has);
});
if(_2e>0&&_2f>0){
dojo.forEach(_30,function(v){
v.adaptHeight(true);
});
}
}
if(this.autoHeight===true||h!=-1||(typeof this.autoHeight=="number"&&this.autoHeight>=this.rowCount)){
this.scroller.windowHeight=h;
}else{
this.scroller.windowHeight=Math.max(this.domNode.clientHeight-t,0);
}
},startup:function(){
if(this._started){
return;
}
this.inherited(arguments);
this.render();
},render:function(){
if(!this.domNode){
return;
}
if(!this._started){
return;
}
if(!this.hasLayout()){
this.scroller.init(0,this.keepRows,this.rowsPerPage);
return;
}
this.update=this.defaultUpdate;
this._render();
},_render:function(){
this.scroller.init(this.rowCount,this.keepRows,this.rowsPerPage);
this.prerender();
this.setScrollTop(0);
this.postrender();
},prerender:function(){
this.keepRows=this._autoHeight?0:this.constructor.prototype.keepRows;
this.scroller.setKeepInfo(this.keepRows);
this.views.render();
this._resize();
},postrender:function(){
this.postresize();
this.focus.initFocusView();
dojo.setSelectable(this.domNode,false);
},postresize:function(){
if(this._autoHeight){
var _34=Math.max(this.views.measureContent())+"px";
this.viewsNode.style.height=_34;
}
},renderRow:function(_35,_36){
this.views.renderRow(_35,_36);
},rowRemoved:function(_37){
this.views.rowRemoved(_37);
},invalidated:null,updating:false,beginUpdate:function(){
this.invalidated=[];
this.updating=true;
},endUpdate:function(){
this.updating=false;
var i=this.invalidated,r;
if(i.all){
this.update();
}else{
if(i.rowCount!=undefined){
this.updateRowCount(i.rowCount);
}else{
for(r in i){
this.updateRow(Number(r));
}
}
}
this.invalidated=null;
},defaultUpdate:function(){
if(!this.domNode){
return;
}
if(this.updating){
this.invalidated.all=true;
return;
}
var _3a=this.scrollTop;
this.prerender();
this.scroller.invalidateNodes();
this.setScrollTop(_3a);
this.postrender();
},update:function(){
this.render();
},updateRow:function(_3b){
_3b=Number(_3b);
if(this.updating){
this.invalidated[_3b]=true;
}else{
this.views.updateRow(_3b);
this.scroller.rowHeightChanged(_3b);
}
},updateRows:function(_3c,_3d){
_3c=Number(_3c);
_3d=Number(_3d);
if(this.updating){
for(var i=0;i<_3d;i++){
this.invalidated[i+_3c]=true;
}
}else{
for(var i=0;i<_3d;i++){
this.views.updateRow(i+_3c);
}
this.scroller.rowHeightChanged(_3c);
}
},updateRowCount:function(_3f){
if(this.updating){
this.invalidated.rowCount=_3f;
}else{
this.rowCount=_3f;
this._setAutoHeightAttr(this.autoHeight,true);
if(this.layout.cells.length){
this.scroller.updateRowCount(_3f);
}
this._resize();
if(this.layout.cells.length){
this.setScrollTop(this.scrollTop);
}
}
},updateRowStyles:function(_40){
this.views.updateRowStyles(_40);
},rowHeightChanged:function(_41){
this.views.renormalizeRow(_41);
this.scroller.rowHeightChanged(_41);
},fastScroll:true,delayScroll:false,scrollRedrawThreshold:(dojo.isIE?100:50),scrollTo:function(_42){
if(!this.fastScroll){
this.setScrollTop(_42);
return;
}
var _43=Math.abs(this.lastScrollTop-_42);
this.lastScrollTop=_42;
if(_43>this.scrollRedrawThreshold||this.delayScroll){
this.delayScroll=true;
this.scrollTop=_42;
this.views.setScrollTop(_42);
_1.job("dojoxGridScroll",200,dojo.hitch(this,"finishScrollJob"));
}else{
this.setScrollTop(_42);
}
},finishScrollJob:function(){
this.delayScroll=false;
this.setScrollTop(this.scrollTop);
},setScrollTop:function(_44){
this.scroller.scroll(this.views.setScrollTop(_44));
},scrollToRow:function(_45){
this.setScrollTop(this.scroller.findScrollTop(_45)+1);
},styleRowNode:function(_46,_47){
if(_47){
this.rows.styleRowNode(_46,_47);
}
},_mouseOut:function(e){
this.rows.setOverRow(-2);
},getCell:function(_49){
return this.layout.cells[_49];
},setCellWidth:function(_4a,_4b){
this.getCell(_4a).unitWidth=_4b;
},getCellName:function(_4c){
return "Cell "+_4c.index;
},canSort:function(_4d){
},sort:function(){
},getSortAsc:function(_4e){
_4e=_4e==undefined?this.sortInfo:_4e;
return Boolean(_4e>0);
},getSortIndex:function(_4f){
_4f=_4f==undefined?this.sortInfo:_4f;
return Math.abs(_4f)-1;
},setSortIndex:function(_50,_51){
var si=_50+1;
if(_51!=undefined){
si*=(_51?1:-1);
}else{
if(this.getSortIndex()==_50){
si=-this.sortInfo;
}
}
this.setSortInfo(si);
},setSortInfo:function(_53){
if(this.canSort(_53)){
this.sortInfo=_53;
this.sort();
this.update();
}
},doKeyEvent:function(e){
e.dispatch="do"+e.type;
this.onKeyEvent(e);
},_dispatch:function(m,e){
if(m in this){
return this[m](e);
}
},dispatchKeyEvent:function(e){
this._dispatch(e.dispatch,e);
},dispatchContentEvent:function(e){
this.edit.dispatchEvent(e)||e.sourceView.dispatchContentEvent(e)||this._dispatch(e.dispatch,e);
},dispatchHeaderEvent:function(e){
e.sourceView.dispatchHeaderEvent(e)||this._dispatch("doheader"+e.type,e);
},dokeydown:function(e){
this.onKeyDown(e);
},doclick:function(e){
if(e.cellNode){
this.onCellClick(e);
}else{
this.onRowClick(e);
}
},dodblclick:function(e){
if(e.cellNode){
this.onCellDblClick(e);
}else{
this.onRowDblClick(e);
}
},docontextmenu:function(e){
if(e.cellNode){
this.onCellContextMenu(e);
}else{
this.onRowContextMenu(e);
}
},doheaderclick:function(e){
if(e.cellNode){
this.onHeaderCellClick(e);
}else{
this.onHeaderClick(e);
}
},doheaderdblclick:function(e){
if(e.cellNode){
this.onHeaderCellDblClick(e);
}else{
this.onHeaderDblClick(e);
}
},doheadercontextmenu:function(e){
if(e.cellNode){
this.onHeaderCellContextMenu(e);
}else{
this.onHeaderContextMenu(e);
}
},doStartEdit:function(_61,_62){
this.onStartEdit(_61,_62);
},doApplyCellEdit:function(_63,_64,_65){
this.onApplyCellEdit(_63,_64,_65);
},doCancelEdit:function(_66){
this.onCancelEdit(_66);
},doApplyEdit:function(_67){
this.onApplyEdit(_67);
},addRow:function(){
this.updateRowCount(this.rowCount+1);
},removeSelectedRows:function(){
this.updateRowCount(Math.max(0,this.rowCount-this.selection.getSelected().length));
this.selection.clear();
}});
dojox.grid._Grid.markupFactory=function(_68,_69,_6a,_6b){
var d=dojo;
var _6d=function(n){
var w=d.attr(n,"width")||"auto";
if((w!="auto")&&(w.slice(-2)!="em")&&(w.slice(-1)!="%")){
w=parseInt(w)+"px";
}
return w;
};
if(!_68.structure&&_69.nodeName.toLowerCase()=="table"){
_68.structure=d.query("> colgroup",_69).map(function(cg){
var sv=d.attr(cg,"span");
var v={noscroll:(d.attr(cg,"noscroll")=="true")?true:false,__span:(!!sv?parseInt(sv):1),cells:[]};
if(d.hasAttr(cg,"width")){
v.width=_6d(cg);
}
return v;
});
if(!_68.structure.length){
_68.structure.push({__span:Infinity,cells:[]});
}
d.query("thead > tr",_69).forEach(function(tr,_74){
var _75=0;
var _76=0;
var _77;
var _78=null;
d.query("> th",tr).map(function(th){
if(!_78){
_77=0;
_78=_68.structure[0];
}else{
if(_75>=(_77+_78.__span)){
_76++;
_77+=_78.__span;
lastView=_78;
_78=_68.structure[_76];
}
}
var _7a={name:d.trim(d.attr(th,"name")||th.innerHTML),colSpan:parseInt(d.attr(th,"colspan")||1,10),type:d.trim(d.attr(th,"cellType")||"")};
_75+=_7a.colSpan;
var _7b=d.attr(th,"rowspan");
if(_7b){
_7a.rowSpan=_7b;
}
if(d.hasAttr(th,"width")){
_7a.width=_6d(th);
}
if(d.hasAttr(th,"relWidth")){
_7a.relWidth=window.parseInt(dojo.attr(th,"relWidth"),10);
}
if(d.hasAttr(th,"hidden")){
_7a.hidden=d.getAttr(th,"hidden")=="true";
}
if(_6b){
_6b(th,_7a);
}
_7a.type=_7a.type?dojo.getObject(_7a.type):dojox.grid.cells.Cell;
if(_7a.type&&_7a.type.markupFactory){
_7a.type.markupFactory(th,_7a);
}
if(!_78.cells[_74]){
_78.cells[_74]=[];
}
_78.cells[_74].push(_7a);
});
});
}
return new _6a(_68,_69);
};
})();
}
