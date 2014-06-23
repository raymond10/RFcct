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
/**
 * 
 */
package org.utt.rfcct.serveur.components.controlers;

import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;
import org.utt.rfcct.serveur.Session;
import org.utt.rfcct.serveur.components.Accueil;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSArray;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr> 10 avr. 2013
 */
@SuppressWarnings("all")
public class AccueilCtrl extends Object {

	private Accueil wocomponent;
	public EOEditingContext edc;
	public Session sess;
	private NSArray<EOCompetence> competences;

	public AccueilCtrl(Accueil aWOComponent) {
		wocomponent = aWOComponent;
		edc = wocomponent.edc();
		sess = wocomponent.mySession();
		competences = EOCompetence.fetchCompetences(edc, sess.TODAY);
	}

	/**
	 * @return the competences
	 */
	public NSArray<EOCompetence> competences() {
		return competences;
	}

	/**
	 * @param competences
	 *            the competences to set
	 */
	public void setCompetences(NSArray<EOCompetence> competences) {
		this.competences = competences;
	}
}