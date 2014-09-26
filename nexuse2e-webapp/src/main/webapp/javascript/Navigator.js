var myTreeModel = {
    _loadChildren: function (parentItem,onComplete) {
        dojo.xhrGet({
            url:"ajax/menu?action=getChildren" + ( parentItem == undefined || parentItem.widgetId == undefined ? "" : "&parentId=" + encodeURI(parentItem.widgetId+"&dummy="+Math.random()) ),
            handleAs:"json",
            load: function(data){
                onComplete(data.items);
            }
        });
    },

    destroy: function(){
    },
    
    // Methods for traversing hierarchy
    getRoot: function(onItem) {
        this._loadChildren(undefined,onItem);
    },
    
    mayHaveChildren: function(/*dojo.data.Item*/ item){
        return item.type == "folder";
    },
    
    getChildren: function(/*dojo.data.Item*/ parentItem, /*function(items)*/ onComplete){
        this._loadChildren(parentItem,onComplete);
    },
    
    // Inspecting items
    getIdentity: function(/* item */ item){
        return item.objectId;
    },
    
    getLabel: function(/*dojo.data.Item*/ item){
        return item.title;
    },

    // Write interface
    newItem: function(/* Object? */ args, /*Item?*/ parent){
    },
    
    pasteItem: function(/*Item*/ childItem, /*Item*/ oldParentItem, /*Item*/ newParentItem, /*Boolean*/ bCopy){
    },
    
    // =======================================================================
    // Callbacks
    onChange: function(/*dojo.data.Item*/ item){
    },
    
    onChildrenChange: function(/*dojo.data.Item*/ parent, /*dojo.data.Item[]*/ newChildrenList){
    }
};
    
/*
 * Returns the style sheet object for generated CSS.
 */
function getGeneratedCSS() {
    for ( var i=0; i < document.styleSheets.length; i++ ) {
        if ( document.styleSheets[i].title == "generatedCSS" ) {
            return document.styleSheets[i];
        }
    }
}

/*
 * Check, whether the array of cssRules contains a rule with the given selector.
 * Return true, if the selector was found; false otherwise.
 */
function containsStyleClass(cssRules, selector) {
    for ( var i=0; i < cssRules.length; i++ ) {
        if ( cssRules[i].selectorText == selector ) {
            return true;
        }
    }
    return false;
};
