/***************************************************************************************************/

//Namespace 2112
//Create Object Node if not already done.
window.NS2112 = window.NS2112 || {};




//SNAPIN Constants / Flags

/*The global array for the YUI get utility*/

NS2112.syncArray = null ;

//encapsulation of the YUI Event - needs to be replaced by an own class to someday
//finally get rid of the YUI stuff.
NS2112.CustomEvent = function( type , oScope , silent , signature ) 
{  //this is just an encapsulation and in the future the YAHOO CE should be eradicated.
    this.type = type;  
    this.scope = oScope || window;
    this.signature = signature || NS2112.CustomEvent.LIST;

    
    this.subscribers = [];

    if (!this.silent) {
       
    }

    var onsubscribeType = "_YUICEOnSubscribe";

    // Only add subscribe events for events that are not generated by 
    // CustomEvent
    if (type !== onsubscribeType) {
        this.subscribeEvent = 
                new NS2112.CustomEvent(onsubscribeType, this, true);

    } 

    this.lastError = null;
};


NS2112.CustomEvent.LIST = 0;


NS2112.CustomEvent.FLAT = 1;

NS2112.CustomEvent.prototype = {

  
    subscribe: function(fn, obj, override) {

        if (!fn) {
throw new Error("Invalid callback for subscriber to '" + this.type + "'");
        }

        if (this.subscribeEvent) {
            this.subscribeEvent.fire(fn, obj, override);
        }

        this.subscribers.push( new NS2112.Subscriber(fn, obj, override) );
    },


    unsubscribe: function(fn, obj) {

        if (!fn) {
            return this.unsubscribeAll();
        }

        var found = false;
        for (var i=0, len=this.subscribers.length; i<len; ++i) {
            var s = this.subscribers[i];
            if (s && s.contains(fn, obj)) {
                this._delete(i);
                found = true;
            }
        }

        return found;
    },
    
    fire: function() {

        this.lastError = null;

        var errors = [],
            len=this.subscribers.length;

        if (!len && this.silent) {
            //YAHOO.log('DEBUG no subscribers');
            return true;
        }

        var args=[].slice.call(arguments, 0), ret=true, i, rebuild=false;

        if (!this.silent) {
            
        }

        // make a copy of the subscribers so that there are
        // no index problems if one subscriber removes another.
        var subs = this.subscribers.slice(), throwErrors = NS2112.CustomEvent.throwErrors;

        for (i=0; i<len; ++i) {
            var s = subs[i];
            if (!s) {
                //YAHOO.log('DEBUG rebuilding array');
                rebuild=true;
            } else {
                if (!this.silent) {
                }

                var scope = s.getScope(this.scope);

                if (this.signature == NS2112.CustomEvent.FLAT) {
                    var param = null;
                    if (args.length > 0) {
                        param = args[0];
                    }

                    try {
                        ret = s.fn.call(scope, param, s.obj);
                    } catch(e) {
                        this.lastError = e;
                        // errors.push(e);
                        if (throwErrors) {
                            throw e;
                        }
                    }
                } else {
                    try {
                        ret = s.fn.call(scope, this.type, args, s.obj);
                    } catch(ex) {
                        this.lastError = ex;
                        if (throwErrors) {
                            throw ex;
                        }
                    }
                }

                if (false === ret) {
                    if (!this.silent) {
                    }

                    break;
                    // return false;
                }
            }
        }

        return (ret !== false);
    },

    
    unsubscribeAll: function() {
        for (var i=this.subscribers.length-1; i>-1; i--) {
            this._delete(i);
        }

        this.subscribers=[];

        return i;
    },

    
    _delete: function(index) {
        var s = this.subscribers[index];
        if (s) {
            delete s.fn;
            delete s.obj;
        }

        // this.subscribers[index]=null;
        this.subscribers.splice(index, 1);
    },

    /**
     * @method toString
     */
    toString: function() {
         return "CustomEvent: " + "'" + this.type  + "', " + 
             "scope: " + this.scope;

    }
};

NS2112.Subscriber = function(fn, obj, override) {

    /**
     * The callback that will be execute when the event fires
     * @property fn
     * @type function
     */
    this.fn = fn;

    this.obj = YAHOO.lang.isUndefined(obj) ? null : obj;

   
    this.override = override;

};


NS2112.Subscriber.prototype.getScope = function(defaultScope) {
    if (this.override) {
        if (this.override === true) {
            return this.obj;
        } else {
            return this.override;
        }
    }
    return defaultScope;
};


NS2112.Subscriber.prototype.contains = function(fn, obj) {
    if (obj) {
        return (this.fn == fn && this.obj == obj);
    } else {
        return (this.fn == fn);
    }
};

/**
 * @method toString
 */
NS2112.Subscriber.prototype.toString = function() {
    return "Subscriber { obj: " + this.obj  + 
           ", override: " +  (this.override || "no") + " }";
}

//encapsulation of the YUI Methods
NS2112.scrollOffsetX = function(baseElement) //optional
{   
   return YAHOO.util.Dom.getDocumentScrollLeft(baseElement) ;
}

NS2112.scrollOffsetY = function(baseElement) //optional
{   
   return YAHOO.util.Dom.getDocumentScrollTop(baseElement) ;
}


//Taken from http://www.dustindiaz.com/javascript-curry/
NS2112.curry = function(fn, scope) {
    var scope = scope || window;
    var args = [];
    for (var i=2, len = arguments.length; i < len; ++i) {
        args.push(arguments[i]);
    };
    return function() {
	    fn.apply(scope, args);
    };
}

/*Detecting Apple Mobile (iPad/iPhone)
	Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) 
	AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 
	Mobile/7B334b Safari/531.21.10Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) 
	AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.10
*/
NS2112.isAppleMobile = function(){
    /*for iPad we check for AppleWebKit, Safari AND Mobile*/   
    var strAppleMobile = NS2112.portalMan.getSetting('IPAD_ID') ;
    var bRet = false ;

    if( navigator.userAgent.search(strAppleMobile) >= 0 ){
		NS2112.DRAGENGINE.CURRENT = NS2112.DRAGENGINE.IPAD ;
    }
    else NS2112.DRAGENGINE.CURRENT = NS2112.DRAGENGINE.TMAN ;
     
    return ( NS2112.DRAGENGINE.IPAD == NS2112.DRAGENGINE.CURRENT )  ; 
}


NS2112.clone = function(myObj, oObjectNames) {
    if (typeof (myObj) != 'object') return myObj;
    if (myObj == null) return myObj;
    var myNewObj = new Object();
    if( typeof oObjectNames == 'undefined' ) oObjectNames = null ;
    oObjectNames = oObjectNames || {};
    for (var OBJECTNAME in myObj) {

        if ( 1 == oObjectNames[OBJECTNAME] ) break;  //Rekursionsbremse
        oObjectNames[OBJECTNAME] = 1;
        myNewObj[OBJECTNAME] = NS2112.clone(myObj[OBJECTNAME], oObjectNames);
    }
    return myNewObj;
}


NS2112.include = function (file) {
  var script  = document.createElement('script');
  script.src  = file;
  script.type = 'text/javascript';

  document.getElementsByTagName('head').item(0).appendChild(script);
}


//Now we can use the NS2112.
//This namespace will be used later for declaring Classes within it.

NS2112.namespace = function(ns) {

    if (!ns || !ns.length) {
        return null;
    }

    var levels = ns.split(".");
    var nsobj = NS2112;

    // NS2112 is implied, so it is ignored if it is included
    // Creating sublevels if not already done so.
    for (var i=(levels[0] == "NS2112") ? 1 : 0; i<levels.length; ++i) {
        nsobj[levels[i]] = nsobj[levels[i]] || {};  //create empty object if necessary
        nsobj = nsobj[levels[i]];
    }

    return nsobj;
};

//Extending by prototypal inheritance using this function.
NS2112.extend = function(subclass, superclass) {
    var f = function() {};
    f.prototype = superclass.prototype;
    subclass.prototype = new f();
    subclass.prototype.constructor = subclass;
    subclass.superclass = superclass.prototype;
    if (superclass.prototype.constructor == Object.prototype.constructor) {
        superclass.prototype.constructor = superclass;
    }
};





 
	// public method for encoding
	NS2112.base64encode = function (input) {
                var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = Base64._utf8_encode(input);
 
		while (i < input.length) {
 
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);
 
			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;
 
			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}
 
			output = output +
			_keyStr.charAt(enc1) + _keyStr.charAt(enc2) +
			_keyStr.charAt(enc3) + _keyStr.charAt(enc4);
 
		}
 
		return output;
	}
 
	// public method for decoding
	NS2112.base64decode = function (input) {
                var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
 
		while (i < input.length) {
 
			enc1 = _keyStr.indexOf(input.charAt(i++));
			enc2 = _keyStr.indexOf(input.charAt(i++));
			enc3 = _keyStr.indexOf(input.charAt(i++));
			enc4 = _keyStr.indexOf(input.charAt(i++));
 
			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;
 
			output = output + String.fromCharCode(chr1);
 
			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}
 
		}
 
		//output = Base64._utf8_decode(output);
 
		return output;
 
	}


NS2112.namespace("NS2112");
NS2112.namespace("windowStyle");
NS2112.namespace("mapMode");
NS2112.namespace("WIDGET");
NS2112.namespace("DRAGENGINE");
NS2112.namespace("LoadControl"); // for constants 

NS2112.windowStyle.WS_NONE           =  0x0  ; //got no style
NS2112.windowStyle.WS_SHOWTITLE      =  0x1  ;
NS2112.windowStyle.WS_CANCLOSE       =  0x2  ;
NS2112.windowStyle.WS_CANGROW        =  0x4  ;
NS2112.windowStyle.WS_MAXIMIZE       =  0x8  ;


NS2112.windowStyle.WS_DROPDOWN       =  0x10 ;   
NS2112.windowStyle.WS_BORDERS        =  0x20 ;
NS2112.windowStyle.WS_FREEFLOAT      =  0x40 ;
NS2112.windowStyle.WS_MANAGEHEIGHT   =  0x80 ;
NS2112.windowStyle.WS_FITTOHOST      =  0x100 ;
NS2112.windowStyle.WS_CANRELOAD      =  0x200 ;
NS2112.windowStyle.WS_CANNAVIGATE    =  0x400 ; //Back/Forth Navigation with buttons
NS2112.windowStyle.WS_CANUNDOCK      =  0x800 ;
NS2112.windowStyle.WS_MANAGECONTENT  =  0x1000 ; //Manage the inner size of content
NS2112.windowStyle.WS_RESIZE         =  0x2000 ; //has a resize handle
NS2112.windowStyle.WS_CHILD          =  0x4000 ; //a childwindow without any controls (footer, header...)

NS2112.windowStyle.WS_VANILLA      =  NS2112.windowStyle.WS_SHOWTITLE 
                                    | NS2112.windowStyle.WS_CANCLOSE
                                    | NS2112.windowStyle.WS_BORDERS 
                                    | NS2112.windowStyle.WS_RESIZE;
//2013 Constants for Drag Drop Contracts for Descriptors
NS2112.mapMode.ddMM_Fields = 'mode.record.mapFields' ;
NS2112.mapMode.ddMM_ID     = 'mode.domnode.match.id' ;
//Some Standard values
NS2112.WINDOWSPACING_X = 7 ;
NS2112.WINDOWSPACING_Y = 7 ;

NS2112.WIDGET.SNAP = 'snap' ;
NS2112.WIDGET.UWA  = 'uwa'  ;
NS2112.WIDGET.CONTENT = 'content' ;
//Containers - 
NS2112.WIDGET.HOSTSINGLE = 0 ;
NS2112.WIDGET.HOSTMULTIPLE = 1 ;

//DRAG ENGINES
NS2112.DRAGENGINE.CURRENT = 1 ;
NS2112.DRAGENGINE.TMAN = 1 ;  //toolman
NS2112.DRAGENGINE.IPAD = 2 ;




NS2112.onRepositoryDataRequest = new NS2112.CustomEvent("onRepositoryDataRequest");  //1.6 Client wants to apply a query with an URL to retrieve new records