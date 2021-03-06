/*******************************************************************************
 * Copyright (c) Raymond NANEON  - 2013.
 * Universite de Technologie de Troyes - CEDRE (www.utt.fr), since 1993.
 * This software is governed by the CeCILL license under French law and abiding by the
 * rules of distribution of free software. You can use, modify and/or 
 * redistribute the software under the terms of the CeCILL license as 
 * circulated by CEA, CNRS and INRIA at the following URL 
 * "http://www.cecill.info". 
 * As a counterpart to the access to the source code and rights to copy, modify 
 * and redistribute granted by the license, users are provided only with a 
 * limited warranty and the software's author, the holder of the economic 
 * rights, and the successive licensors have only limited liability. In this 
 * respect, the user's attention is drawn to the risks associated with loading,
 * using, modifying and/or developing or reproducing the software by the user 
 * in light of its specific status of free software, that may mean that it
 * is complicated to manipulate, and that also therefore means that it is 
 * reserved for developers and experienced professionals having in-depth
 * computer knowledge. Users are therefore encouraged to load and test the 
 * software's suitability as regards their requirements in conditions enabling
 * the security of their systems and/or data to be ensured and, more generally, 
 * to use and operate it in the same conditions as regards security. The
 * fact that you are presently reading this means that you have had knowledge 
 * of the CeCILL license and that you accept its terms.
 * 
 * Do not remove this copyright message
 * 
 * Contributors:
 *     Raymond NANEON - initial API and implementation
 ******************************************************************************/
package org.utt.rfcct.serveur.components;
import org.cocktail.fwkcktlwebapp.common.util.StringCtrl;
import org.utt.rfcct.serveur.RfcctHelpers;
import org.utt.rfcct.serveur.RfcctParamManager;
import org.utt.rfcct.serveur.VersionMe;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSLog;

import er.ajax.AjaxUtils;
import er.ajax.AjaxValue;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr>
 *18 mars 2013
 */
@SuppressWarnings("all")
public class Wrapper extends BaseComponent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4427426961140851340L;
	private String onloadJS;
	private String erreurScript;
	private String titre;

	public Wrapper(WOContext context) {
        super(context);
		NSLog.out.appendln("Wrapper");
    }
	
	public void appendToResponse(WOResponse response, WOContext context) {
		super.appendToResponse(response, context);
	    AjaxUtils.addScriptResourceInHead(context, response, "prototype.js");
	    AjaxUtils.addScriptResourceInHead(context, response, "effects.js");
	    AjaxUtils.addScriptResourceInHead(context, response, "wonder.js");
	    AjaxUtils.addScriptResourceInHead(context, response, "FwkCktlThemes.framework", "scripts/window.js");
		AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/strings.js");
		AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/formatteurs.js");
		AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/rfcct.js");
		//Introduction HTML5
		AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/modernizr.js");
		//AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/jquery-1.4.3.min.js");
		//AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/jquery-ui-1.8.5.min.js");
		AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/jquery.placehold-0.2.min.js");
		AjaxUtils.addScriptResourceInHead(context, response, "app", "scripts/webforms/webforms2-p.js");
		RfcctHelpers.insertStylesSheet(context, response);
		addScriptResource(response, "jscript/wz_tooltip.js", null, "FwkCktlWebApp.framework", 
                RESOURCE_TYPE_JSCRIPT, RESOURCE_DEST_END_BODY, RESOURCE_FROM_WEB_SERVER_RESOURCES);
	}
	
	
	/**
	 * @return the onloadJS
	 */
	public String onloadJS() {
		return onloadJS;
	}


	/**
	 * @param onloadJS the onloadJS to set
	 */
	public void setOnloadJS(String onloadJS) {
		this.onloadJS = onloadJS;
	}


	/**
	 * @return the erreurScript
	 */
	public String erreurScript() {
		String messageErreur = mySession().messageErreur();
		if (!StringCtrl.isEmpty(messageErreur) && !messageErreur.startsWith("[RFCCT]:Exception")) {
			if (messageErreur.indexOf("ORA-") > -1) {
				messageErreur = messageErreur.substring(messageErreur.indexOf("ORA-")+10);
			}
			erreurScript = "alert("+AjaxValue.javaScriptEscaped(messageErreur)+");";
			mySession().setMessageErreur(null);
		} else {
			erreurScript = null;
		}
		 
		return erreurScript;
	}

	/**
	 * @param erreurScript the erreurScript to set
	 */
	public void setErreurScript(String erreurScript) {
		this.erreurScript = erreurScript;
	}


	/**
	 * @return the titre
	 */
	public String titre() {
		if (titre == null) {
			titre = "Rfcct : r�f�rentiel des comp�tences, de coop�ration et des th�matiques";
		}
		return titre;
	}


	/**
	 * @param titre the titre to set
	 */
	public void setTitre(String titre) {
		this.titre = titre;
	}
	
	public String version() {
		return VersionMe.htmlAppliVersion();
	}

	public WOActionResults quitter() {
		mySession().defaultEditingContext().revert();
    	return mySession().onQuitter();
	}

	public WOActionResults tree() {
		mySession().reset();
		return pageWithName(GestionGroupe.class.getName());
	}

	public String serverId() {
		return myApp().serverBDId();
	}
	
	public boolean btActif() {
		return mySession().utilisateur() == null;
	}
	
	public String icon() {
		return application().resourceManager().urlForResourceNamed(
				"img/Idee.jpg", null, null, context().request());
	}
	
	public String nomAndPrenom() {
		String user = null;
		if(myApp().newGestionDroitsEnabled()) {
			if(myGdUser() != null)
				user = myGdUser().getNomAndPrenom();			
		} else {
			if(myJefyUser() != null)
				user = myJefyUser().getNomAndPrenom();			
		}
		return user;
	}
}
