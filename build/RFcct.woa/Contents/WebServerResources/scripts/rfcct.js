// Namespace Rfcct
var rfcct = {};

rfcct.focusedEltMemento = {

	retainEltWithFocus : function(elt) {
	    this.eltHadFocus = elt.name;
	},
	
	focusRetainedElt : function() {
	    var elt = document.getElementsByName(this.eltHadFocus)[0];
	    if (elt != null) {
	        elt.focus();
	    }
	}

}

rfcct.getSelectionId = function(text, li) {
	if (typeof(li)!='undefined') {
		var chaine = li.innerText;
	  if (typeof(chaine)=='undefined') {
			chaine = li.textContent;
		}
	
		switch(text.id) {
	   case 'CodeNaf_field' :
	 	 var libelle_code_naf = chaine;
		 $('CodeNaf_field').value = libelle_code_naf;
	       break;
	  }
	
		var updateContainer = text.getAttribute("updatecontainerid");
		if (typeof(updateContainer)!='undefined' && updateContainer!=null && updateContainer.length > 0) {
			eval(updateContainer+'Update()');
		}
	}
}

function openWinPageErreur1(href,title) {
	var win = Windows.getWindow('Erreur_win');
	var className = 'greyLighting';
	var closable = true;
	if (typeof(win)=='undefined') {
	 	win = new Window('Erreur_win',{className: className, title: title, url: href, destroyOnClose:true, recenterAuto:true, resizable:true, closable:false, minimizable:false, maximizable:false,  minWidth:500,  showEffectOptions: {duration:0.5}});
		var editorOnClose = { 
			onClose: function(eventName, win) {
	  		eval('parent.ContainerWinPageErreurOnCloseUpdate()'); 
	  	} ,
			onDestroy: function(eventName, win) {
				Windows.removeObserver(editorOnClose);
			}
	 	};
	 	Windows.addObserver(editorOnClose);  
	 	win.setDestroyOnClose();    
	   
	  win.setZIndex(99999);
	  win.showCenter(true);
	} else {
	   win.showCenter();
	}
}
