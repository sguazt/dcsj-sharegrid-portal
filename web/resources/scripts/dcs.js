if (typeof Dcs == 'undefined')
{

// for old browsers
window['undefined'] = 'undefined';

// Map over Dcs in case of overwrite
var _Dcs = window.Dcs;

Dcs = window.Dcs = (function(){

/**
 * A constructor.
 */
function Dcs()
{
	var root;
	var reUri = /^\w+:\/\/[^\/]*/;

	try{ throw new Error(""); }catch(e){ root = e.fileName.toLowerCase().split("dcs.js")[0]; };

	if (root)
	{
		var pos = 0;
		var len = root.length;

		// Check for URI scheme and host parts
		if (root.charAt(0) != '/')
		{
			m = root.match(reUri);
			if (m)
			{
				pos = m[0].length;
			}
		}
		// Remove trailing '/'
		if (root.charAt(root.length-1) == '/')
		{
			len--;
		}

		root = root.substring(pos, len);
	}
	else
	{
		root = './';
		// Init the base URL
		if (document && document.getElementsByTagName)
		{
			var scripts = document.getElementsByTagName("script");
			//var repkg = /Dcs\.js(\W|$)/i;
			var rePkg = /(^|\/)dcs\.js([\?\.]|$)/i;
			for(var i = 0; i < scripts.length; i++)
			{
				var src = scripts[i].getAttribute("src");
				if (!src)
				{
					continue;
				}
				var m = src.match(rePkg);
				if (m)
				{
					var pos = 0;
					var len = m.index;

					// find out where we came from
					if (src.charAt(0) != '/')
					{
						m = src.match(reUri);
						if (m)
						{
							pos = m[0].length;
						}
					}
					root = src.substring(pos, len);
					//Dcs.prototype._BaseUrl = root;
					break;
				}
			}
		}
	}

	Dcs.prototype._BaseUrl = root;
	//Dcs.prototype._BaseUrl = '/sgportal/resources/scripts/';

	this._LoadedModules['Dcs'] = this;
}

Dcs.prototype = {
	Version: '1.0.0', /** Version number. */
	_BaseUrl: undefined, /** Base URL used as prefix for relative paths. */
	_LoadedUrls: [], /** Cache for loaded URLs. */
	_LoadedModules: {}, /** Cache for loaded modules. */
	_ModulePrefixes: { /** Reserved module prefixes. */
		'Dcs': {
			name: "Dcs", value: "."
		}
	},
	_GlobalScope:  this, /** Global scope reference. */
	_g_OmitModuleCheck: false
};

Dcs.prototype._IsUndefined = function(/*String*/ s)
{
	return (s == 'undefined' || s == undefined);
}

Dcs.prototype._ModuleHasPrefix = function(/*String*/ module)
{
		// summary: checks to see if module has been established

		var mp = this._ModulePrefixes;
		return !!(mp[module] && mp[module].value); // Boolean
};

Dcs.prototype._GetModulePrefix = function(/*String*/ module)
{
		// summary: gets the prefix associated with module

		var mp = this._ModulePrefixes;
		if (this._ModuleHasPrefix(module))
		{
			return mp[module].value; // String
		}
		return module; // String
};

Dcs.prototype._IsModuleLoaded = function(/*String*/ module)
{
alert("IsModuleLoaded>> module '" + module + "' loaded? " + (this._LoadedModules[module] != undefined));//XXX
	return (this._LoadedModules[module] != undefined) ? true : false;
};

Dcs.prototype._GetModule = function(/*String*/ module)
{
	return this._LoadedModules[module];
};

Dcs.prototype._SetModule = function(/*String*/ module, /*Object*/ o)
{
	return this._LoadedModules[module] = o;
};

Dcs.prototype.PropertiesToString = function(obj, obj_name)
{
	var result = "";
	for (var i in obj)
	{
		result += obj_name + "." + i + " = " + obj[i] + "\n";
	}
	return result;
};

//@{ Config

Dcs.prototype.Config = {
};

//@} Config

//@{ DOM facilities

Dcs.prototype.IsDomSupported = function()
{
	return !!document.getElementsByTagName;
};

Dcs.prototype.IsDom1Supported = function()
{
	return !!document.getElementsByTagName;
};

Dcs.prototype.IsDom2Supported = function()
{
	return !!document.getElementById;
};

Dcs.prototype.IsDom3Supported = function()
{
	return !!document.adoptNode;
};

//@] DOM facilities

//@{ OO facilities

Dcs.prototype.Namespace = function(/*String,...*/)
{
	// SUMMARY
	//   Creates namespaces to be used for scoping variables and classes.
	//
	// PARAMETERS
	//   ...: a list of namespace names.
	//
	// RETURN
	//   none
	//

	var names = arguments;

	if (names.length == 0)
	{
		throw new Error('At least one namespace name is needed.');
	}

	for (var i = 0; i < names.length; i++)
	{
		var parts = names[i].split(".");

		if (!this._IsModuleLoaded(parts[0]))
		{
			var o=null;

			// Evaluates the first name in global scope
			// Subsequent names are evaluated in nested scopes
			var rt = parts[0];
			//this.EvalScript('if (typeof ' + rt + ' == "undefined"){' + rt + ' = {};} o = ' + rt + ';');
			this.EvalScript('if (typeof ' + rt + ' == "undefined"){' + rt + ' = {};}');
			o = rt;
			//o=Dcs;

			for (var j = 1; j < parts.length; j++)
			{
				var subname = parts.slice(0, j);
				if (this._IsModuleLoaded(subname))
				{
						// Get from cache
						o = this._GetModule(subname);
				}
				else
				{
					// Fake definition
					o[parts[j]] = o[parts[j]] || {};
					o = o[parts[j]];
				}
			}
		}
	}
};

Dcs.prototype.Module = function(/*String*/ name, /*Object*/ properties, /*Object*/ ctor, /*Object*/ context)
{

	if (!this._IsModuleLoaded[name])
	{
		var parts = name.split(".");
		var o = null;

		// Evaluates the first name in global scope
		// Subsequent names are evaluated in nested scopes
		var rt = parts[0];
		//this.EvalScript('if (typeof ' + rt + ' == "undefined"){' + rt + ' = {};} o = ' + rt + ';');
		if (this._IsModuleLoaded(parts[0]))
		{
			rt = this._GetModule(parts[0]);
		}
		else
		{
			this.EvalScript('if (typeof ' + rt + ' == "undefined"){' + rt + ' = {};}', context);
		}
		o = rt;
		//o=Dcs;

		// Get or create the parent modules
		for (var j = 1; j < (parts.length-1) - 1; j++)
		{
			var subname = parts.slice(0, j);
			if (this._IsModuleLoaded(subname))
			{
				o = this._GetModule(subname);
			}
			else
			{
				// Trick: actually we don't load the module.
				// We only define the name in the current scope.
				// This faster the module definition operation
				// Note: the module must not be put in the cache.

				//o[parts[j]] = o[parts[j]] || {};
				o[parts[j]] = o[parts[j]] || function(){};
				o = o[parts[j]];
			}
		}

		var clazz = null;

		// Create the module
		if (ctor != undefined)
		{
			clazz = o[parts[parts.length-1]] = o[parts[parts.length-1]].constructor = ctor;
		}
		else
		{
			//o[parts[parts.length-1]] = function(){};
			clazz = function(){};
		}
		if (properties != undefined)
		{
			//o[parts[parts.length-1]].prototype = properties;
			//name.prototype = properties;
			clazz.prototype = properties;
			//this.EvalScript(name + '.prototype = ' + properties + ';');
		}
		else
		{
			clazz.prototype = {};
		}
		clazz.prototype.constructor = clazz;
		//o = o[parts[parts.length-1]];
		o[parts[parts.length-1]] = new clazz();
		o = o[parts[parts.length-1]];

		// Put the module in the cache
		//this._LoadedModules[a[i]] = o;
		this._SetModule(name, o);
	}

	return this._GetModule(name);
};

Dcs.prototype.Override = function(origclass, overrides){
	if(overrides){
		var p = origclass.prototype;
		for(var method in overrides){
			p[method] = overrides[method];
		}
	}
};

Dcs.prototype.Apply = function(o, c, defaults){
    if(defaults){
        // no "this" reference for friendly out of scope calls
        this.Apply(o, defaults);
    }
    if(o && c && typeof c == 'object'){
        for(var p in c){
            o[p] = c[p];
        }
    }
    return o;
};

/**
 * Create a class.
 *
 * Create a class with the given (optional) properties (i.e. an object literal)
 * and an optional base-class.
 * This class also gets the method the function "Override()", that can be used
 * to override members on an instance.
 * MySubClass = Dcs.Class(
 *   {
 *     constructor: function(config) {
 *         // preprocessing here
 *         MySubClass.superclass.constructor.apply(this, arguments);
 *         // postprocessing here
 *     },
 *
 *     aMethod: function() {
 *         // etc.
 *     }
 *   },
 *   MySuperClass
 * );
 *
 * @param {Object} props A literal with members which are copied
 * into the subclass's * prototype, and are therefore shared between all
 * instances of the new class.
 * This may contain a special member named {@code constructor}. This is used to
 * define the constructor of the new class, and is returned. If this property is
 * not specified, a constructor is generated and returned which just calls the
 * superclass's constructor passing on its parameters.
 * @param {Function} base The class being extended
 *
 * @return {Function} The subclass constructor.
 */
//Dcs.prototype.Class = function(sb, sp, overrides)
Dcs.prototype.Class = function(name, props, base)
{
//		if (props == undefined)
//		{
//			return undefined;
//		}
	
alert("Class>> Name: " + name);//XXX
alert("Class>> Base: " + base);//XXX
		if (this._IsUndefined(name))
		{
			throw new Error("Class definition needs a class name");
		}
		if (this._IsUndefined(base))
		{
			var parts = name.split(".");
			if (parts && parts.length > 1)
			{
				base = parts.slice(0, parts.length-1);
			}
			else
			{
				base = 'Object';
			}
		}
alert("Class>> New Base: " + base);//XXX
		if (
			!this._IsModuleLoaded(base)
			&& this.EvalScript('try { (typeof ' + base + ' == "undefined") ? true : false } catch (e) { true; }', this)
		) {
alert("Class>> Creating Base: " + base);//XXX
			this.Class(base, {});
		}
		if (this._IsUndefined(props))
		{
			props = {};
		}

alert("Class>> Creating Class: " + name);//XXX
		//inline override
		var io = function(o) {
			for (var m in o)
			{
				this[m] = o[m];
			}
		};
		var oc = Object.prototype.constructor;
		var sub;

		if (!this._IsUndefined(props.constructor) && props.constructor != oc)
		{
			sub = props.constructor;
		}
		else
		{
			sub = function() { base.apply(this, arguments); return this; };
		}
		var F = function() {};
		var subproto;
		var baseproto = base.prototype;
		F.prototype = baseproto;
		subproto = sub.prototype = new F();
		subproto.constructor = sub;
		sub.superclass = baseproto;
alert("Class>> baseproto: " + baseproto);
alert("Class>> baseproto != undefined: " + (baseproto != undefined));
alert("Class>> baseproto != undefined: " + !this._IsUndefined(baseproto));
		if (!this._IsUndefined(baseproto) && baseproto.constructor == oc)
		{
alert("Class>> baseproto has entered " + baseproto);
			baseproto.constructor = base;
		}
		sub.prototype.Override = function(o) { this.Override(props, o); };
		subproto.Override = io;
		this.Override(sub, props);
//			sub.prototype.Class = function(o) { Dcs.prototype.Class(sub, o); };
//			return props;

		this._SetModule(
			name,
			sub
		);

alert("Class>> Created Class: " + name + " (" + sub + "//" + (typeof sub) + ")");//XXX
		return sub;
};

//Dcs.prototype.Singleton = function(/*String*/ clazz, /*Object*/ properties)
//{
//	if (properties['init'] == undefined)
//	{
//		throw new Error("Expected the 'init' method.");
//	}
//
//	if (this._IsModuleLoaded(clazz))
//	{
//		return this._GetModule(clazz);
//	}
//
//	c = this.EvalScript("Dcs.NameSpace('" + clazz + "');");
//	clazz.prototype = clazz.fn = properties || {};
//	//c = this.EvalScript("function " + clazz + "() { this.init(); }");
//	var o = new c();
//	o.constructor = null;
//
//	return o;
//}

//@} OO facilities

//@{ Resource import facilities

// summary: 
//   Perform an evaluation in the global scope. Use this rather than
//   calling 'eval()' directly.
// description: 
//   Placed in a separate function to minimize size of trapped
//   exceptions. Calling eval() directly from some other scope may
//   complicate tracebacks on some platforms.
// return:
//   The result of the evaluation. Often `undefined`
// note:
//   - JSC eval() takes an optional second argument which can be 'unsafe'.
//   - Mozilla/SpiderMonkey eval() takes an optional second argument which
//     is the scope object for new symbols.
//
// FIXME: investigate Joseph Smarr's technique for IE:
//   http://josephsmarr.com/2007/01/31/fixing-eval-to-use-global-scope-in-ie/
// see also:
// http://trac.dojotoolkit.org/ticket/744
//
Dcs.prototype.EvalScript = function(/*String*/ scriptFragment, /*Object*/ context, /*Boolean*/ defer)
{
	//FIXME: context if ignored. Actually all scripts are evaluated at global scope

alert("EvalScript>> Entering");//XXX
	if (scriptFragment == undefined)
	{
		return null;
	}

	if (context == undefined)
	{
		context = this._GlobalScope;
	}

	if (defer)
	{
		var head = document.getElementsByTagName("head")[0] || document.documentElement;
		var script = document.createElement("script");

		script.type = "text/javascript";
		if (document.createTextNode)
		{
			script.appendChild( document.createTextNode( scriptFragment ) );
		}
		else
		{
			script.text = scriptFragment;
		}

		// Use insertBefore instead of appendChild  to circumvent an IE6 bug.
		// This arises when a base node is used (#2709).
		head.insertBefore( script, head.firstChild );
		head.removeChild( script );

		return null;
	}
	else
	{
		if (context == this._GlobalScope)
		{
alert("EvalScript>> Evaluating in global scopr");//XXX
			// Eval in global scope
			if (window.execScript)
			{
				window.execScript(scriptFragment); // eval in global scope for IE
				return null; // execScript doesn’t return anything
			}

			var _global = this._GlobalScope; // global scope reference

			return _global.eval ? _global.eval(scriptFragment) : eval(scriptFragment);
		}

alert("EvalScript>> Evaluating in loval scopr");//XXX
		return eval(scriptFragment);
	}
};

Dcs.prototype.LoadPath = function(/*String*/ path, /*String?*/ module, /*Function?*/ cb) {
	// SUMMARY:
	//              Load a Javascript module given a relative path
	//
	// DESCRIPTION:
	//              Loads and interprets the script located at relpath, which is
	//              relative to the script root directory.  If the script is found but
	//              its interpretation causes a runtime exception, that exception is
	//              not caught by us, so the caller will see it.  We return a true
	//              value if and only if the script is found.
	//
	// @param path: 
	//              An absolute/relative path to a script (typically ending in '.js').
	// @param module: 
	//              A module whose existance to check for after loading a path.  Can be
	//              used to determine success or failure of the load.
	// @param cb: 
	//              a callback function to pass the result of evaluating the script

alert("LoadPath>> path " + path);
	var uri = ((path.charAt(0) == '/' || path.match(/^\w+:/)) ? "" : (this._BaseUrl + "/")) + path;
alert("LoadPath>> uri " + uri);
	try
	{
		return !module ? this.LoadUri(uri, cb) : this.LoadUriAndCheck(uri, module, cb);
	}
	catch(e)
	{
		return false;
	}
};

Dcs.prototype.LoadUri = function(/*String*/ uri, /*Function?*/ callback, /*Boolean*/ preventCache){
	//      summary:
	//              Loads JavaScript from a URI
	//      description:
	//              Reads the contents of the URI, and evaluates the contents.  This is
	//              used to load modules as well as resource bundles. Returns true if
	//              it succeeded. Returns false if the URI reading failed.  Throws if
	//              the evaluation throws.
	//      uri: a uri which points at the script to be loaded
	//      callback: 
	//              a callback function to process the result of evaluating the script
	//              as an expression, typically used by the resource bundle loader to
	//              load JSON-style resources

alert("LoadUri>> uri " + uri);
	if (this._LoadedUrls[uri])
	{
alert("LoadUri>> Already loaded");
		return true;
	}

	if (preventCache)
	{
		uri += ( ( uri.indexOf('?') + 1 ) ? '&' : '?' ) + ( new Date() ).getTime();
	}

alert("__FIXME> Remove false from LoadPath<EMXIF__");//XXX
	if (false && this.Ajax.IsSupported())
	{
alert("LoadUri>> Load through Ajax");
		//var contents = this._GetText(uri, true);
		var contents = this.DoGetRequest(uri, true);
		if ( !contents )
		{
			return false;
		}
		this._LoadedUrls[uri] = true;
		this._LoadedUrls.push(uri);
		if (callback)
		{
			contents = '('+contents+')';
		}
		var value = this.EvalScript(contents);
		if(callback)
		{
			callback(value);
		}
	}
	else if (this.IsDom3Supported())
	{
alert("LoadUri>> Load through DOM3");
		var head = document.getElementsByTagName("head")[0] || document.documentElement;
		var script = document.createElement("script");

		script.type = "text/javascript";
		script.src = uri;

		// Use insertBefore instead of appendChild  to circumvent an IE6 bug.
		head.insertBefore( script, head.firstChild );
		head.removeChild( script );
		if(callback)
		{
			//FIXME: not applicable
		}
	}
	else
	{
alert("LoadUri>> Load through document.write");
		// For really old browsers
		var scriptTag = '<script type="text/javascript" src="' + uri + '"><!-- // --><\/script>';
//		<div style="position:absolute;left:0px;top:0px;visibility:hidden;" id="dcs:datadiv">
//			<iframe src="about:blank" height="0" width="0" name="dcs:dataframe"></iframe>
//		</div>
		window.document.writeln(scriptTag);
		if (callback)
		{
			//FIXME: not applicable
		}
	}

	return true;
};

Dcs.prototype.LoadUriAndCheck = function(/*String*/ uri, /*String*/ moduleName, /*Function?*/ cb){
	// summary: calls loadUri then findModule and returns true if both succeed
	var ok = false;
	try
	{
		ok = this.LoadUri(uri, cb);
	}
	catch(e)
	{
		// ignore
	}
	return !!(ok && this._IsModuleLoaded(moduleName)); // Boolean
};


Dcs.prototype._GetText = function(/*URI*/ uri, /*Boolean*/ fail_ok)
{
	// summary: Read the contents of the specified uri and return those contents.
	// uri:
	//              A relative or absolute uri. If absolute, it still must be in
	//              the same "domain" as we are.
	// fail_ok:
	//              Default false. If fail_ok and loading fails, return null
	//              instead of throwing.
	// returns: The response text. null is returned when there is a
	//              failure and failure is okay (an exception otherwise)

	var xhr = this.CreateXmlHttpRequest();

//	var base = document.getElementsByTagName("base");
//	var hasBase = (base && base.length > 0);
	if (uri.charAt(0) == '/' || uri.match(/^\w+:/))
	{
		uri = this._BaseUrl + "/" + uri;
	}

	xhr.open('GET', uri, false);
	try
	{
		xhr.send(null);
		if (!this.AjaxCheckRequestStatus(http))
		{
			var err = new Error("Unable to load "+uri+" status:"+ http.status);
			err.status = http.status;
			err.responseText = http.responseText;
			throw err;
		}
	}
	catch(e)
	{
		if(fail_ok){ return null; } // null
		// rethrow the exception
		throw e;
	}
	return http.responseText; // String
};

Dcs.prototype._GetModuleSymbols = function(/*String*/modulename)
{
	// SUMMARY:
	//   Converts a module name in dotted JS notation to an array
	//   presenting the lower-case path in the source tree.

	// Converts to lower-case and extract module parts.
	var syms = modulename.toLowerCase().split(".");

	return syms; // Array
};

Dcs.prototype.UseScript = function(/*String*/scriptPath)
{
	var script = null;

	script = this.LoadPath(scriptPath);

	if(!script)
	{
		throw new Error("Could not load script '" + scriptPath + "'.");
	}

	return script;
};

Dcs.prototype.UseScriptIf = function(/*String*/scriptPath, /*Boolean*/condition)
{
	if (condition == true)
	{
		return Dcs.prototype.UseScript(scriptPath);
	}
}

Dcs.prototype.UseCss = function(/*String*/ cssPath)
{
	var head = document.getElementsByTagName("head")[0] || document.documentElement;
	var link = document.createElement("link");

	link.type = "text/css";
	link.rel = "stylesheet";
	link.href = cssPath;

	// Use insertBefore instead of appendChild  to circumvent an IE6 bug.
	// This arises when a base node is used (#2709).
	head.insertBefore( link, head.firstChild );
	head.removeChild( link );
};

/**
 * loads a Javascript module from the appropriate URI
 *
 * @param moduleName Module name to load. Module paths are de-referenced by
 * internal mapping of locations to names and are disambiguated by
 * longest prefix. See `dojo.registerModulePath()` for details on
 * registering new modules.
 * @param omitModuleCheck If 'true', omitModuleCheck skips the step of ensuring
 * that the loaded file actually defines the symbol it is referenced by.
 * For example if it called as `dojo._loadModule("a.b.c")` and the
 * file located at `a/b/c.js` does not define an object `a.b.c`,
 * and exception will be throws whereas no exception is raised
 * when called as `dojo._loadModule("a.b.c", true)`
 *
 * `Dcs.UseModule("A.B")` first checks to see if symbol A.B is
 * defined. If it is, it is simply returned (nothing to do).
 *
 * If it is not defined, it will look for `A/B.js` in the script root
 * directory.
 *
 * `Dcs.UseModule` throws an exception if it cannot find a file
 * to load, or if the symbol `A.B` is not defined after loading.
 *      
 * It returns the object `A.B`.
 *      
 * `dojo._loadModule()` does nothing about importing symbols into
 * the current namespace.  It is presumed that the caller will
 * take care of that. For example, to import all symbols into a
 * local block, you might write:
 *      
 * |       with (dojo._loadModule("A.B")) {
 * |               ...
 * |       }
 *
 *   And to import just the leaf symbol to a local variable:
 *
 *   |       var B = dojo._loadModule("A.B");
 *   |       ...
 *   returns: the required namespace object
 */
Dcs.prototype.UseModule = function(/*String*/moduleName, /*Boolean?*/omitModuleCheck) {
	omitModuleCheck = this._g_OmitModuleCheck || omitModuleCheck;

	//Check if it is already loaded.
	var module = this._GetModule(moduleName);
	if (module)
	{
		return module;
	}

	// convert module separators (periods) to file separators (slashes)
	var relpath = this._GetModuleSymbols(moduleName).join("/") + '.js';

	var modArg = (!omitModuleCheck) ? moduleName : null;
	var ok = this.LoadPath(relpath, modArg);

	if (!ok && !omitModuleCheck)
	{
		throw new Error("Could not load '" + moduleName + "'; last tried '" + relpath + "'");
	}

	// check that the symbol was defined
	// Don't bother if we're doing xdomain (asynchronous) loading.
	if (!omitModuleCheck && !this._isXDomain)
	{
		// pass in false so we can give better error
		//module = this._LoadedModules[moduleName];
		module = this._GetModule(moduleName);
		if (!module)
		{
			throw new Error("symbol '" + moduleName + "' is not defined after loading '" + relpath + "'");
		}
	}

	return module;
};

//@} Resource import facilities

//@{ AJAX facilities

Dcs.prototype.Ajax = {};

Dcs.prototype.Ajax.IsSupported = function()
{
	try
	{
		var xhr = this.CreateXmlHttpRequest();
		if (typeof xhr != undefined)
		{
			return true;
		}
	}
	catch (e)
	{
		// ignore
	}
	return false;
}

Dcs.prototype.Ajax.CreateXmlHttpRequest = function()
{
	//return new XMLHttpRequest();
	var methods = [
		function() { return new XMLHttpRequest(); },
		function() { return new ActiveXObject('Msxml2.XMLHTTP'); },
		function() { return new ActiveXObject('Microsoft.XMLHTTP'); }
	];

	var returnVal;
	for(var i = 0, len = methods.length; i < len; i++)
	{
		try
		{
			returnVal = methods[i]();
		}
		catch(e)
		{
			continue;
		}
		return returnVal;
	}

	throw new Error('Could not create an XML HTTP request object.');
};

/**
 * Send a GET request.
 *
 * @param url The URL where issuing the POST request.
 * @params callbacks Hash for callback functions. Possible keys are:
 * <ul>
 * <li>onsuccess: handler for successful request.</li>
 * <li>onerror: handler for erroneous request.</li>
 * </ul>
 */
Dcs.prototype.Ajax.DoGetRequest = function(url, callbacks, xargs)
{
	//FIXME: DOJO returns a Deferred object while our implementation return the status

	if (typeof dojo != undefined)
	{
		// Prefer DOJO over own implementation

		var xhrArgs = {};

		xhrArgs.url = url;
		if (callbacks)
		{
			if (callbacks.onsuccess)
			{
				xhrArgs.load = function(response, ioArgs) { callbacks.onsuccess(response, ioArgs); return response; }
			}
			if (callbacks.onerror)
			{
				xhrArgs.load = function(response, ioArgs) { callbacks.onerror(response, ioArgs); return response; }
			}
		}
		if (xargs.synch)
		{
			xhrArgs.sync = true;
		}

		return dojo.xhrGet(xhrArgs);
	}
	else
	{
		// DOJO not yet loaded try with own implementation;

		var xhr =  this.CreateXmlHttpRequest();
		xhr.open('GET', url, !xargs.synch);
		var _t = this;
		xhr.onreadystatechange = function() { return _t.RequestHandler(xhr, callbacks ); };
		xhr.send(null);

		return xhr.status || 0;
	}
};

/**
 * Send a POST request.
 *
 * @param url The URL where issuing the POST request.
 * @param data The data to be posted. It is a hash with the following
 * properties:
 * <ul>
 * <li>query: the raw query string.</li>
 * <li>form: the id of the form or the form DOM object itself.</li>
 * <li>content: an hash containing key/value pairs to be posted.</li>
 * </ul>
 * @params callbacks Hash for callback functions. Possible keys are:
 * <ul>
 * <li>onsuccess: handler for successful request.</li>
 * <li>onerror: handler for erroneous request.</li>
 * </ul>
 */
Dcs.prototype.Ajax.DoPostRequest = function(url, data, callbacks, xargs)
{
	//FIXME: DOJO returns a Deferred object while our implementation return the status

	if (typeof dojo != undefined)
	{
		// Prefer DOJO over own implementation

		var xhrArgs = {};

		xhrArgs.url = url;
		if (callbacks)
		{
			if (callbacks.onsuccess)
			{
				xhrArgs.load = function(response, ioArgs) { callbacks.onsuccess(response, ioArgs); return response; }
			}
			if (callbacks.onerror)
			{
				xhrArgs.load = function(response, ioArgs) { callbacks.onerror(response, ioArgs); return response; }
			}
		}
		if (data)
		{
			if (data.query)
			{
				xhrArgs.postData = data.query
			}
			if (data.form)
			{
				xhrArgs.form = data.form;
			}
			if (data.content)
			{
				xhrArgs.content = data.content;
			}
		}
		if (xargs.synch)
		{
			xhrArgs.sync = true;
		}

		return xhrArgs.postData ? dojo.rawXhrPost(xhrArgs) : dojo.xhrPost(xhrArgs);
	}
	else
	{
		// DOJO not yet loaded try with own implementation;

		var xhr =  this.CreateXmlHttpRequest();
		xhr.open("POST", url, xargs.synch);
		//xhr.setRequestHeader("Content-Type", "application/x-javascript;");
		xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		var _t = this;
		xhr.onreadystatechange = function() {
				return _t.RequestHandler(
					xhr,
					callbacks,
					{
						url: url
					}
				);
		};
		var xdata = '';
		if (data)
		{
			if (data.query)
			{
				xdata += data.query
			}
			if (data.form)
			{
				xdata += data.form;
			}
			if (data.content)
			{
				xdata += data.content;
			}
		}
		xhr.send(xdata);

		return xhr.status || 0;
	}
};

Dcs.prototype.Ajax.CheckRequestStatus = function(xhr)
{
	var stat = xhr.status || 0;
	return (stat >= 200 && stat < 300) ||   // Boolean
		stat == 304 || // allow any 2XX response code
		stat == 1223 || // get it out of the cache
		(!stat && (location.protocol=="file:" || location.protocol=="chrome:") ); // Internet Explorer mangled the status code
};

Dcs.prototype.Ajax.RequestHandler = function(xhr, callbacks, xargs)
{
	var inerr = false;
	var errmsg;

	try
	{
		if (xhr == undefined)
		{
			inerr = true;
			errmsg = 'Null XML HTTP request in callback'; //NO I18N
		}
		if (xhr.readyState == 4)
		{
			if (xhr.status == 200)
			{
				if (callbacks && callbacks.onsuccess)
				{
					callbacks.onsuccess(xhr,responseText, xargs);
				}
			}
			else
			{
				if (callbacks && callbacks.onerror)
				{
					callbacks.onerror(xhr.responseText, xargs);
				}
				//errmsg = 'Bad status from XML HTTP request: ' + xargs.xhr.statusText;
				//inerr = true;
			}
		}
	}
	catch (e)
	{
		errmsg = e;
		inerr = true;
	}

	if (inerr)
	{
		throw new Error(errmsg);
	}

	return response;
};

//@} AJAX facilities

//@{ JSON facilities

Dcs.Json = {};

Dcs.Json.FromJson = function(/*String*/ json)
{
	return dojo.fromJson(json);
};

Dcs.Json.ToJson = function(/*Object*/ o)
{
	return dojo.toJson(o);
};

//@} JSON facilities

// Dcs class Bootstrap. This actually creates a singleton instance.
return this instanceof Dcs
	? this
	: new Dcs();

})();

Dcs.UseScriptIf('dojo/dojo/dojo.js', typeof dojo == undefined);

//Dcs.UseScript("XMLHttpRequest.js");

} // typeof Dcs == 'undefined'

