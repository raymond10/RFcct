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

import org.cocktail.fwkcktlwebapp.server.CktlWebAction;
import org.cocktail.fwkcktlwebapp.server.components.CktlWebPage;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSLog;

import er.extensions.foundation.ERXProperties;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr>
 *18 mars 2013
 */
public class Main extends CktlWebPage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7516556061221069857L;
	private String login;
	private String password;
	public boolean blocageQuestionSecrete = false;

	public Main(WOContext context) {
		super(context);
		NSLog.out.appendln("Main");
	}
	
	public String goCas() {
		return (CktlWebAction.getDefaultLoginActionURL(context()));
	}
	
	public boolean isConnected() {
		return (cktlSession().connectedUserInfo() != null);
	}
	
	/**
	 * @return the login
	 */
	public String login() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String password() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean useCAS() {
		return CktlWebAction.useCasService();
	}
	
	public boolean hasFicheUtilisation(){
		if (ERXProperties.stringForKey("org.utt.rfcct.serveur.fiche.utilisation") != null
				&& !ERXProperties.stringForKey("org.utt.rfcct.serveur.fiche.utilisation").equals("")
				&& !ERXProperties.stringForKey("org.utt.rfcct.serveur.fiche.utilisation").equals(" ")) {
			return true;
			}
		return false;
	}
	
	public String urlFicheProduit(){
		return ERXProperties.stringForKey("org.utt.rfcct.serveur.fiche.utilisation");
	}
}
