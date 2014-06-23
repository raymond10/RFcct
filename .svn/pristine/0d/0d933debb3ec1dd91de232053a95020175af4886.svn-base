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
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSSet;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.eof.ERXEC;

import org.cocktail.fwkcktlajaxwebext.serveur.component.CktlAjaxWindow;
import org.cocktail.fwkcktldroitsutils.common.util.MyStringCtrl;
import org.cocktail.fwkcktlpersonne.common.metier.EOCivilite;
import org.cocktail.fwkcktlpersonne.common.metier.EOIndividu;
import org.cocktail.fwkcktlpersonne.common.metier.IPersonne;
import org.cocktail.fwkcktlpersonneguiajax.serveur.components.PersonneSrch;
import org.utt.fwkuttcompetences.serveur.modele.grhum.EOIndividuUlr;
import org.utt.fwkuttcompetences.serveur.modele.grhum.EOStructureUlr;
import org.utt.fwkuttcompetences.serveur.utils.tiers.Tiers;
import org.utt.rfcct.serveur.components.BaseComponent;

/**
 * @author Raymond NANEON <raymond.naneon at utt.fr>
 *21 mars 2013
 */
@SuppressWarnings("all")
public class PersonneMultipleSrch extends BaseComponent {
    /**
	 * 
	 */
	private static final long serialVersionUID = 799688260171739403L;
    private static final String BINDING_SELECTION_DG = "selectionDisplayGroup";
    private static final String BINDING_WANT_RESET = "wantReset";
    private static final String BINDING_EDC = "editingContext";
    private static final String BINDING_TIERS_INDIV = "tiersIndiv";
    private static final String BINDING_TIERS_STR = "tiersStr";
    private static final String BINDING_QUALIFIER_FOR_INDIVIDU = "qualifierForIndividu";
    private static final String BINDING_TITRE = "titre";

    private ERXDisplayGroup displayGroup = new ERXDisplayGroup();
    private ERXDisplayGroup tiersDisplayGroup = new ERXDisplayGroup();
    private IPersonne selectedPersonne;
    private boolean isModeRecherche = false;
    private boolean isModeCreation = false;
    private boolean wantResetPersonneSrch = false;

	public PersonneMultipleSrch(WOContext context) {
        super(context);
    }

    @Override
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
	
	@Override
    public void appendToResponse(WOResponse response, WOContext context) {
        if (wantReset()) {
            displayGroup.setObjectArray(NSArray.EmptyArray);
            tiersDisplayGroup.setObjectArray(NSArray.EmptyArray);
            tiersDisplayGroup.setSelectedObjects(NSArray.EmptyArray);
            displayGroupSelection().setObjectArray(NSArray.EmptyArray);
            setModeRecherche(false);
            wantResetPersonneSrch = true;
        }
        super.appendToResponse(response, context);
        if (wantReset() && canSetValueForBinding(BINDING_WANT_RESET))
            setValueForBinding(Boolean.FALSE, BINDING_WANT_RESET);
    }
	
	//Recherche individu ou structure
	public WOActionResults selectPersonnesRecherche() {
        NSMutableArray<IPersonne> previous = displayGroupSelection().allObjects().mutableClone();
        previous.addObjectsFromArray(displayGroup().selectedObjects());
        NSSet<IPersonne> newSelection = new NSSet<IPersonne>(previous);
        displayGroupSelection().setObjectArray(newSelection.allObjects());
        for (IPersonne personne : previous) {
			Tiers tmp = Tiers.Tiers(ERXEC.newEditingContext());
			if (personne.isIndividu()) {
				tmp.setTiersInvididu(EOIndividuUlr.individuPourPersId(
						tmp.editingContext(), personne.persId()));
				tmp.setTiersLibelle(tmp.tiersInvididu().nomAffichage()+" "+tmp.tiersInvididu().prenomAffichage());
			}
			if (personne.isStructure()) {
				tmp.setTiersStructure(EOStructureUlr.structurePourPersId(
						tmp.editingContext(), personne.persId()));
				tmp.setTiersLibelle(tmp.tiersStructure().llStructure());
			}
			tmp.setTiersNumero(personne.persId());
			tmp.setTiersDetails(personne.persType());
			if (mySession().listTiers() == null)
				mySession().setListTiers(new NSMutableArray<Tiers>());
			if(!mySession().listTiers().contains(tmp))
				mySession().listTiers().addObject(tmp);
		}
        return showAccueil();
    }
	
	//Supprimer personne selectionn�e
	public WOActionResults delSelectedPersonne() {
        displayGroupSelection().deleteSelection();
        return null;
    }
	
	//Verifier si la personne existe
	public WOActionResults switchToModeCreation() {
        setModeCreation(false);
        if (selectedPersonne.isIndividu()) {
            EOIndividu individu = (EOIndividu)selectedPersonne;
            NSArray<EOCivilite> civilites = EOCivilite.fetchAll(editingContext());
            individu.setToCiviliteRelationship((EOCivilite) civilites.objectAtIndex(0));
        }
        return null;
    }
    
    private EOEditingContext editingContext() {
        return (EOEditingContext)valueForBinding(BINDING_EDC);
    }
    
    public boolean wantReset() {
        return booleanValueForBinding(BINDING_WANT_RESET, false);
    }
    
    public boolean tiersIndiv() {
		return booleanValueForBinding(BINDING_TIERS_INDIV, false);
	}
    
    public boolean tiersStr() {
		return booleanValueForBinding(BINDING_TIERS_STR, false);
	}
    
    public EOQualifier qualifierForIndividu() {
    	return (EOQualifier) valueForBinding(BINDING_QUALIFIER_FOR_INDIVIDU);
    }
    
    //Resultat recherche
    public boolean hasResults() {
        return !displayGroup().displayedObjects().isEmpty();
    }
    
    //Personne selectionnee
    public boolean hasPersonneSelected() {
        return !displayGroupSelection().selectedObjects().isEmpty();
    }

    public boolean isInSelectionMode() {
        return isModeRecherche;
    }

    public ERXDisplayGroup displayGroup() {
        return displayGroup;
    }

    public IPersonne selectedPersonne() {
        return selectedPersonne;
    }

    public void setSelectedPersonne(IPersonne selectedPersonne) {
        this.selectedPersonne = selectedPersonne;
    }

    public ERXDisplayGroup displayGroupSelection() {
        return (ERXDisplayGroup) valueForBinding(BINDING_SELECTION_DG);
    }
    
    public String titre(){
    	return (String) valueForBinding(BINDING_TITRE);
    }
    
    public boolean isModeRecherche() {
        return isModeRecherche;
    }
    
    public void setModeRecherche(boolean isModeRecherche) {
        this.isModeRecherche = isModeRecherche;
    }
    
    public boolean isModeCreation() {
        return isModeCreation;
    }
    
    public void setModeCreation(boolean isModeCreation) {
        this.isModeCreation = isModeCreation;
    }

    
    public ERXDisplayGroup tiersDisplayGroup() {
        return tiersDisplayGroup;
    }
    
    private String personneSrchType;

	/**
	 * @return the personneSrchType
	 */
	public String getPersonneSrchType() {
		if (MyStringCtrl.isEmpty(this.personneSrchType)) {
			setPersonneSrchType(PersonneSrch.PERS_TYPE_INDIVIDU);
		}
		return personneSrchType;
	}

	/**
	 * @param personneSrchType the personneSrchType to set
	 */
	public void setPersonneSrchType(String personneSrchType) {
		this.personneSrchType = personneSrchType;
	}
	
	/**
	 * @return the wantResetPersonneSrch
	 */
	public boolean isWantResetPersonneSrch() {
		if (wantReset()) {
            return true;
        } else if (wantResetPersonneSrch){
            wantResetPersonneSrch = false;
            return true;
        }
        return false;
	}

	/**
	 * @param wantResetPersonneSrch the wantResetPersonneSrch to set
	 */
	public void setWantResetPersonneSrch(boolean wantResetPersonneSrch) {
		this.wantResetPersonneSrch = wantResetPersonneSrch;
	}

	public WOActionResults showAccueil() {
		CktlAjaxWindow.close(context());
        return null;
    }
}
