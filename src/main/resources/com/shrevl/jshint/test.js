/**
 *
 ***
 * Notes : eventually I would like to move this away from needing jQuery
 */
(function( $, global, undefined ){
	"use strict";
	
	var flushing = false;
	
	function error( msg ){
		if ( console && console.log ){
			console.log( msg );
		}
	}
	
	/**
	 * The bootstrap functionality
	 */
	function Bootstrap(){};
		
	(function(){
		var stackSpace = [],
			stacks = { '_' : [] },
			readyStacks = { '_' : [] },
			isReady = false,
			pauseAfter = null;
		
		function time(){
			return ( new Date() ).getTime();
		}
	
		function runStack( stackName ){
			var next,
				stack = stacks[stackName];
				
			if ( stack != undefined && !stack.running && stack.length > 0 ){
				
				stack.running = true; // JS is single threaded, so this is good enough
				
				if ( !pauseAfter ){
					pauseAfter = time() + 300;
				}
					
				next = stack.shift();
				
				if ( next.canRun() ){
					// maintain a stack space, so any annoymous stacks run in place
					stackSpace.unshift( stackName );
					next.action.apply( next.action, next.arguments );
					stackSpace.shift();
					
					if ( !flushing && time() > pauseAfter ){
						setTimeout( function(){
							pauseAfter = null;
							stack.running = false;

							runStack( stackName );
						}, 30 );
					}else{
						stack.running = false;
						runStack( stackName );
					}
				}else if( flushing ){ // no timeout when flushing, but will need to cycle
					if ( console && console.trace ){
						console.trace();
					}
					throw 'can not flush with elements that can not run';
				}else{
					stack.unshift( next );
					
					setTimeout( function(){
						pauseAfter = null;
						stack.running = false;
						runStack( stackName );
					}, 10 );
				}
			}
		}
		
		// only place I am actually using jQuery...
		$(document).ready(function(){
			isReady = true;
			for( var stack in readyStacks ){
				if ( stacks[stack] ){
					stacks[stack] = stacks[stack].concat( readyStacks[stack] );
				}else{
					stacks[stack] = readyStacks[stack];
				}
				
				runStack(stack);
			}
		});
		
		Bootstrap.prototype.flush = function(){
			flushing = true;
			runStack();
		};
		
		Bootstrap.prototype.stopFlush = function(){
			flushing = false;
		};
		/**
		 * 
		 */
		Bootstrap.prototype.add = function( func, ops ){
			var stack,
				tag = null,
				onReady,
				args = [],
				canRun = true,
				action;
			
			if ( typeof(ops) == 'string' ){
				stack = ops;
				onReady = false;
			}else if ( typeof(ops) == 'object' ){
				stack   = ops.stack;
				onReady = ops.onReady;
				tag     = ops.tag;
				
				if ( ops.canRun != undefined ){
					canRun = ops.canRun;
				}
				
				if ( ops.args ){
					args = ops.args;
				}
			}else{
				stack = stackSpace.length > 0 ? stackSpace[0] : '_';
				onReady = false;
			}
			
			action = {
				tag       : tag,
				stack     : stack,
				flush     : function(){
					flushing = true;
					runStack();
				},
				stopFlush : function(){
					flushing = false;
				},
				unlock    : function(){
					canRun = true;
					
					runStack( stack );
				},
				canRun    : function(){
					return canRun;
				},
				action    : func,
				arguments : args
			};
			
			if ( stacks[stack] == undefined ){
				stacks[stack] = [];
				readyStacks[stack] = [];
			}
			
			if ( flushing ){
				stacks[stack].push( action );
				runStack( stack );
			}else if ( !isReady && onReady ){
				readyStacks[stack].push( action );
			}else{
				stacks[stack].push( action );
				runStack( stack );
			}
			
			return action;
		};
	}());
	
	var bootstrap = new Bootstrap();
	/**
	 * Functions for managing a namespace
	 */
	var Namespace = {
		parse : function( namespace, hasClass ){
			var ns = namespace.split('.'),
				rtn = {
					className : '',
					extension : '',
					original  : namespace
				};
			
			if ( ns[ns.length - 1] == 'js' ){
				ns.pop();
				rtn.extension = '.js';
			}
			
			if ( ns[ns.length - 1] == 'min'){
				ns.pop();
				rtn.extension = '.min' + rtn.extension;
			}
			
			if ( hasClass ){
				var name = ns.pop();
				// use full path if add on, otherwise it's a class and the name of the file should be the class
				rtn.className = ( name.charAt(0) == name.charAt(0).toUpperCase() ) ? name : namespace;
			}
			
			rtn.path = ns;
			
			return rtn;
		},
		getSpace : function( namespace ){
			var ns = Namespace.parse( namespace ),
				path = ns.path,
				obj = global;
				
			for( var i = 0; i < path.length; i++ ){
				var space = path[i];
				
				if ( obj[space] === undefined ){
					return null;
				}else{
					obj = obj[space];
				}
			}
			
			return obj;
		},
		createSpace : function( namespace ){
			var ns = Namespace.parse( namespace ),
				path = ns.path,
				obj = global;
				
			for( var i = 0; i < path.length; i++ ){
				var space = path[i];
				
				if ( obj[space] === undefined ){
					obj[space] = {};
				}
				
				obj = obj[space];
			}
			
			return obj;
		}
	};
	 
	/**
	* The autoloader content area
	*/
	function Loader(){
	}
	
	(function(){
		var cache = {},
			locations = {};
		
		Loader.prototype.settings = {
			loadTries : 10,        // After a file has been loaded, number of times to see if the definition was successful before giving up
			loadWait  : 30,        // After a file has been loaded, number of seconds to wait between definition lookups
			root      : 'js'       // The default root location for the js files
		};
		
		Loader.prototype.setLocation = function( namespace, location, hasClass ){
			var path = Namespace.parse( namespace, hasClass ).path,
				space = locations;
		
			while( path.length > 0 ){
				var name = path.shift();
				
				if ( space[name] == undefined ){
					space[name] = {};
				}
				
				space = space[name];
			};
			
			if ( space != locations ){
				space._location = location;
			}
		};
		
		Loader.prototype.getLocation = function( namespace, hasClass ){
			var ns = Namespace.parse( namespace, hasClass ),
				path = ns.path,
				space = locations,
				closest = null,
				location = '';
			
			for( var i = 0; i < path.length && space[path[i]] != undefined; i++ ){
				space = space[path[i]];
				
				if ( space._location ){
					closest = i;
					location = space._location;
				}
			}
			
			if ( closest === null ){
				location = this.settings.root;
				closest = -1;
			}
			
			for( var i = closest + 1; i < path.length; i++ ){
				location += '/' + path[i];
			}
			
			return location + ( hasClass ? '/' + ns.className : '' );
		};
		
		Loader.prototype.instantiate = function( require, func, args, stack ){
			if ( stack ){
				var inst,
					request = this.require( require, function(){
							var space = bootup.namespace.getSpace( require );
							
							// create an instance
							inst = new space();
						
							// call the constructor with the arguments
							space.apply( inst, args );
						} 
					),
					bs = bootstrap.add( function(){
							func( inst );
						},{
							stack  : stack,
							canRun : request.isReady(), 
							tag    : require
						} 
					);
				
				request.addCallback(function(){
					bs.unlock(); 
				});
			}else{
				this.require( require, function(){
					var space = bootup.namespace.getSpace( require ),
						inst = new space();
				
					space.apply( inst, args );
				
					func( inst );
				} );
			}
		};
			
		function launchCallbacks( location ){
			var t = cache[location];
			
			if ( t ){
				cache[location] = true;
				
				for( var i = 0; i < t.length; i++ ){
					t[i]();
				}
			}
		}
		
		function isReady(){
			return !this.stillLoading && this.filesLoading <= 0;
		}
			
		function requestFile( location, isLoaded ){
			var dis = this;
			
			dis.filesLoading++;
			
			if ( cache[location] ){
				if ( typeof(cache[location]) == 'boolean' ){
					// it has been loaded, just call the call back
					dis.filesLoading--;
					dis.callback();
				}else{
					// push the other callback onto the stack
					cache[location].push(function(){
						dis.filesLoading--;
						dis.callback();
					});
				}
			}else{
				cache[location] = [ function(){
					dis.filesLoading--;
					dis.callback(); 
				} ];
				
				$.getScript( location )
					.done(function( script, textStatus ){
						// what if is loaded is defined on place, but not another?
						if ( isLoaded == undefined || isLoaded.apply( dis ) ){
							launchCallbacks(location);
						}else{
							var interval = setInterval( function(){
								try {
									if ( isLoaded.apply( dis ) ){
										clearInterval( interval );
											
										launchCallbacks(location);
									}
								}catch( e ){
									clearInterval( interval );
									throw e;
								}
							}, dis.loadWait );
						}
					})
					.fail(function( jqxhr, settings, exception ){
						error( 'failed to load: ' + location + "\n" + exception );
					});
			}
		}
		
		function request(location, name){
			var tries = 0,
				dis = this;

			requestFile.call( this, location, function(){
				if ( name == undefined ){
					return true;
				}else{
					var src = Namespace.getSpace( name );
					
					if ( src != null ){
						return true;
					}else{ 
						if ( tries >= dis.loadTries ){
							throw 'loaded but not found > '+name;
						}else{
							tries++;
							return false;
						}
					}
				}
			});
		}
		
		Loader.prototype.require = function( requirements, func, args, stack ){
			var 
				dis = this,
				loadInfo = {
					isReady       : function(){ return isReady.call( loadInfo ); },
					loadWait      : this.settings.loadWait,
					loadTries     : this.settings.loadTries,
					stillLoading  : true,
					filesLoading  : 0,
					bs            : null,
					call          : function(){
						func.apply( func, args );
					},
					callback      : function(){
						if ( isReady.apply(this) && typeof(func) == 'function' ){
							for( var i = 0; i < this.callbackStack.length; i++ ){
								this.callbackStack[i].call( this );
							}
							this.callbackStack = null;
							
							// either call is fired via bootstrap or directly here
							if ( this.bs ){
								this.bs.unlock();
							}else{
								this.call();
							}
						}
					},
					callbackStack : [],
					addCallback   : function( func ){
						if ( this.callbackStack != null ){
							this.callbackStack.push( func );
						}else{
							func.call( this );
						}
					}
				};
			
			if ( args == undefined ){
				args = [];
			}
			
			if ( typeof(requirements) == 'string' ){
				requirements = { classes:[requirements] };
			}
			
			if ( requirements.scripts ){
				for( var i = 0; i < requirements.scripts.length; i++ ){
					// right now I can't think of a way to test is a script is loaded
					request.call( laodInfo, requirements.scripts[place] );
				}
			}
			
			if ( requirements.classes ){
				for( var i = 0; i < requirements.classes.length; i++ ){
					var check = requirements.classes[i],
						obj = Namespace.getSpace( check );

					if ( obj == null || typeof(obj) != 'function' ){
						request.call( loadInfo, dis.getLocation(check,true)+'.js', check );
					}
				}
			}
			
			// plugins are really just another way to define functions, at least right now
			if ( requirements.plugins ){
				if ( requirements.functions == undefined ){
					requirements.functions = {};
				}
				
				for( var path in requirements.plugins ){
					// TODO : collisions aren't being considered
					requirements.functions[ dis.getLocation(path,true)+'.js' ] = requirements.plugins[path];
				}
			}
			
			if ( requirements.functions ){
				for( var script in requirements.functions ){
					var check = requirements.functions[script],
						obj = Namespace.getSpace( check );
					
					if ( obj == null || typeof(obj) != 'function' ){
						request.call( loadInfo, script, check );
					}
				}
			}
			
			loadInfo.stillLoading = false;
			
			if ( stack ){
				loadInfo.bs = bootstrap.add( function(){
					loadInfo.call();
				},{
					stack  : stack,
					canRun : loadInfo.isReady()
				} );
			}
			
			loadInfo.callback();
			
			return loadInfo;
		};
	}());
	/**
	 * The functional code that allows you to create classes
	 */
	function Creator(){};
	
	(function(){
		// TODO : need to use apply here, to allow a proper rereferencing
		Creator.prototype.createAlias = function( from, to ){
			if ( typeof(from) == 'string' ){
				from = Namespace.getSpace( from );
			}
			
			if ( typeof(to) ){
				to = Namespace.createSpace(to);
			}
			
			to = from;
		};
		
		Creator.prototype.createClass = function( settings, callback, args ){
			var dis = this;
			
			// if callback isn't a function, assume it's actually the array of args to be passed
			if ( typeof(callback) != 'function' ){
				args = callback;
				callback = undefined;
			}
			
			if ( args == undefined ){
				args = [];
			}
			
			settings = $.extend( { 
				require   : {},
				namespace : '',
				name      : 'junk',
				define    : function( $, global, undefined ){ return function(){}; }
			}, settings );
			
			bootup.loader.require( settings.require, function(){
				var ns = Namespace.createSpace( settings.namespace ),
					definition = settings.define.apply( dis, args );
				
				ns[settings.name] = definition;
				args.unshift( definition );
				
				if ( callback ){
					callback.apply( dis, args );
				}
			});
		};
	}());
	
	// register the globals
	global.bootup = {
		classes   : {
			Creator   : Creator,
			Loader    : Loader,
			Bootstrap : Bootstrap
		},
		schedule  : function( func, ops ){
			return bootstrap.add( func, ops );
		},
		namespace : Namespace,
		creator   : new Creator(),
		loader    : new Loader(),
		bootstrap : bootstrap
	};
}( jQuery, window ));