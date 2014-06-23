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
package org.utt.rfcct.serveur.components.windows;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSTimestamp;

import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EODomaine;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOTypeTiers;
import org.utt.rfcct.serveur.components.BaseComponent;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr>
 *28 mars 2013
 */
@SuppressWarnings("all")
public class CreerDomaine extends BaseComponent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8879245009209773218L;
	
	private NSArray<EOTypeTiers> typeTiers;
	private String lblCourt;
	private String lblLong;
	private NSTimestamp dateFin;
	private EODomaine domaine;

	public CreerDomaine(WOContext context) {
        super(context);
        typeTiers = EOTypeTiers.fetchAllEOTypeTierses(mySession().defaultEditingContext(), new NSArray(EOTypeTiers.SORT_LIBELLE_ASC));
    }

	/**
	 * @return the lblCourt
	 */
	public String getLblCourt() {
		return lblCourt;
	}

	/**
	 * @param lblCourt the lblCourt to set
	 */
	public void setLblCourt(String lblCourt) {
		this.lblCourt = lblCourt;
	}

	/**
	 * @return the lblLong
	 */
	public String getLblLong() {
		return lblLong;
	}

	/**
	 * @param lblLong the lblLong to set
	 */
	public void setLblLong(String lblLong) {
		this.lblLong = lblLong;
	}

	/**
	 * @return the dateFin
	 */
	public NSTimestamp getDateFin() {
		return dateFin;
	}

	/**
	 * @param dateFin the dateFin to set
	 */
	public void setDateFin(NSTimestamp dateFin) {
		this.dateFin = dateFin;
	}

	/**
	 * @return the typeTiers
	 */
	public NSArray<EOTypeTiers> getTypeTiers() {
		return typeTiers;
	}

	/**
	 * @param typeTiers the typeTiers to set
	 */
	public void setTypeTiers(NSArray<EOTypeTiers> typeTiers) {
		this.typeTiers = typeTiers;
	}

	/**
	 * @return the domaine
	 */
	public EODomaine getDomaine() {
		return domaine;
	}

	/**
	 * @param domaine the domaine to set
	 */
	public void setDomaine(EODomaine domaine) {
		this.domaine = domaine;
	}
	
	//Boutons annuler/Enregistrer
	public WOActionResults enregistrer(){
		return annulerAjout();
	}
	
	public WOActionResults annulerAjout() {
		CktlAjaxWindow.close(context());
		return null;
	}
}
