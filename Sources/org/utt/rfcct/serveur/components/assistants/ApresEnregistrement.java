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
package org.utt.rfcct.serveur.components.assistants;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSLog;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.rfcct.serveur.RfcctParamManager;
import org.utt.rfcct.serveur.components.BaseComponent;
import org.utt.rfcct.serveur.components.GestionGroupe;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr>
 *21 mars 2013
 */
@SuppressWarnings("all")
public class ApresEnregistrement extends BaseComponent {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 7845143258385290759L;
	private EOCompetence competence;

	public ApresEnregistrement(WOContext context) {
        super(context);
		NSLog.out.appendln("ApresEnregistrement");
    }

	/**
	 * @return the competence
	 */
	public EOCompetence competence() {
		return competence;
	}

	/**
	 * @param competence the competence to set
	 */
	public void setCompetence(EOCompetence competence) {
		this.competence = competence;
	}
	
	public WOActionResults consulter() {
		return null;
	}
	
	public WOActionResults accueil() {
		GestionGroupe nextPage = (GestionGroupe)pageWithName(GestionGroupe.class.getName());
		mySession().reset();
		return nextPage;
	}
	
	/**
	 * Ajout d'un param�tre dans la table Rfcct_Param�tres pour choisir le CSS adapt�.
	 */
	public String cssApplication() {
		if (RfcctParamManager.RFCCT_CSS != null
				&& !RfcctParamManager.RFCCT_CSS.equals("")
				&& !RfcctParamManager.RFCCT_CSS.equals(" ")){
			String chemin = myApp().rfcctParamManager.getParam(RfcctParamManager.RFCCT_CSS);
			return "css/" + chemin;
		}
		return "css/UttCommonGris.css";
	}
}
