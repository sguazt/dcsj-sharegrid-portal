var Singleton = (function() {
    var instance = null;

    function PrivateConstructor() {
        var rand = Math.round(Math.random() * 100);
        this.getRand = function() {
            return rand;
        }
    }

    return new function() {
        this.getInstance = function() {
            if (instance == null) {
                instance = new PrivateConstructor();
                instance.constructor = null;
            }
            return instance;
        }
    }
})();

var singletonInstance = Singleton.getInstance(); 

var Foo = (function() {
    var instance = null;

    function Bar() {
    }
	Bar.prototype = Bar.fn = {
        rand : Math.round(Math.random() * 100),
        getRand : function() {
            return this.rand;
        }
	};

    return new function() {
        this.getInstance = function() {
            if (instance == null) {
                instance = new Bar();
                instance.constructor = null;
            }
            return instance;
        }
    }
})();

var foo = Foo.getInstance(); 
alert( "FOO: " + foo.getRand() );

var Moo = (function() {
//    var Moo = null;
//
    function Moo() {
		this.init();
    }
	Moo.prototype = Moo.fn = {
        rand : Math.round(Math.random() * 100),
		init : function() { },
        getRand : function() {
            return this.rand;
        }
	};

    return this instanceof Moo
		? this.init()
		: new Moo();
})();

alert( "MOO 1: " + Moo );
alert( "MOO 2: " + Moo.getRand() );

var global = this;

function singletonify(constructorName) {
    var constructorFunc = global[constructorName];
    var instance = null;

    global[constructorName] = new function() {
        this.getInstance = function() {
            if (instance == null) {
                instance = new constructorFunc();
                instance.constructor = null;
            }
            return instance;
        }
    }
}

function RegularConstructor() {
    var rand = Math.round(Math.random() * 100);
    this.getRand = function() {
        return rand;
    }
}

singletonify("RegularConstructor");

var myInstance = RegularConstructor.getInstance();
document.write(myInstance.getRand()); 
/*
function CatNames() {}

CatNames.prototype = {
names:[],
add:function add() {
return this.names.push.apply(this.names, arguments);
},
removeLast:function removeLast() {
return this.names.pop();
},
log:function() {
var str = this.names.join(”, “);
console.log(str);
return str;
}
};

var x = new CatNames();
var y = new CatNames();

x.add(”Mistigri”);
y.add(”Bibi”);
y.add(”Gary”);
x.removeLast();

alert(x.log() === y.log() && x.log() === “Mistigri, Bibi”);
*/
