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

import org.utt.rfcct.serveur.Application;
import org.utt.rfcct.serveur.RfcctHelpers;
import org.utt.rfcct.serveur.RfcctParamManager;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSLog;

import er.ajax.AjaxUtils;
import er.extensions.foundation.ERXStringUtilities;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr>
 *20 mars 2013
 */
@SuppressWarnings("all")
public class Timeout extends BaseComponent {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2531756018610310812L;

	public Timeout(WOContext context) {
        super(context);
		NSLog.out.appendln("TimeOut");
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
		RfcctHelpers.insertStylesSheet(context, response);
		addScriptResource(response, "jscript/wz_tooltip.js", null, "FwkCktlWebApp.framework", 
                RESOURCE_TYPE_JSCRIPT, RESOURCE_DEST_END_BODY, RESOURCE_FROM_WEB_SERVER_RESOURCES);
	}
	
	public boolean isStateless() {
		return true;
	}

	/**
	 * Retourne la definition des styles par defaut. Les balises de style CSS 
	 * doivent etre donnees dans la configuration de l'Apllication, le parametre
	 * <code>HTML_CSS_STYLES</code>.
	 */
	public String getDefaultStyles() {
		return ERXStringUtilities.toString(myApp().config().valuesForKey("HTML_CSS_STYLES").objects(), "\n");
	}
	
	public String applicationURL() {
		return myApp().getApplicationURL(context());
	}

	public String siteURL() {
		return myApp().mainWebSiteURL();
	}

	public boolean hasSiteURL() {
		return (myApp().mainWebSiteURL() != null);
	}
	
	/**
	 * Ajout d'un paramètre dans la table Rfcct_Paramètres pour choisir le CSS adapté.
	 */
	public String cssApplication() {
		if (RfcctParamManager.RFCCT_CSS != null
				&& !RfcctParamManager.RFCCT_CSS.equals("")
				&& !RfcctParamManager.RFCCT_CSS.equals(" ")){
			Application myApp = Application.app();
			String chemin = myApp.rfcctParamManager.getParam(RfcctParamManager.RFCCT_CSS);
			return "css/" + chemin;
		}
		return "css/UttCommonGris.css";
	}
}
