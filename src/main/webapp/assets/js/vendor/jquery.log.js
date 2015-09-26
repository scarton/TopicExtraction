// jquery plugin to provide console debugging (works around IE bug)
// Make a call to setLogging to turn logging on or off: $.setLogging(true); or $.setLogging(false);
(function( $ ) {
    $.log = function(msg) {
        var logging = false;
        if (typeof $.data(document, '_jq_logging_') !== "undefined")
            logging = $.data(document, '_jq_logging_')
        if (logging && typeof console !== "undefined") {
            console.log(msg);
        } 
    };
/*    
    $.log = function(lvl, msg) {
        var logging = false;
        var level = "error";
        if (typeof $.data(document, '_jq_logging_') !== "undefined")
            logging = $.data(document, '_jq_logging_')
        if (typeof $.data(document, '_jq_logging_level_') !== "undefined")
            level = $.data(document, '_jq_logging_level_')
        if (level==="error" && lvl!=="error")
            logging=false;
        if (level==="debug" && (lvl==="error" || lvl==="debug"))
            logging=true;
        if (logging && typeof console !== "undefined") {
            console.log(lvl+": "+msg);
        } 
    };
*/
    $.setLogging = function(tf) {
        if (typeof tf === "boolean") 
            $.data(document, '_jq_logging_', tf);
    }
    $.setLogLevel = function(l) {
        if (typeof l === "string") 
            $.data(document, '_jq_logging_level_', l);
    }
}( jQuery ));


