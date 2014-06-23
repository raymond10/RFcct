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
import org.cocktail.fwkcktlreportingguiajax.serveur.CktlAbstractReporterAjaxProgress;
import org.cocktail.reporting.CktlAbstractReporter;
import org.cocktail.reporting.jrxml.IJrxmlReportListener;
import org.utt.fwkuttcompetences.serveur.modele.rfcct.EOCompetence;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSLog;

public class Competence extends BaseComponent {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1277046382843437652L;
	private EOCompetence competence;
	private CktlAbstractReporter reporter;
	private ReporterAjaxProgress reporterProgress;
	private String reportFilename;
	private Boolean temcreate;
	private Boolean temupdate;

	public Competence(WOContext context) {
        super(context);
		NSLog.out.appendln("Competence");
    }

	/**
	 * @return the competence
	 */
	public EOCompetence getCompetence() {
		return competence;
	}

	/**
	 * @param competence the competence to set
	 */
	public void setCompetence(EOCompetence competence) {
		this.competence = competence;
	}

	/**
	 * @return the isAfficherDetailCompetence
	 */
	public boolean isAfficherDetailCompetence() {
		return competence != null;
	}
	
	/**
	 * @return the reporter
	 */
	public CktlAbstractReporter reporter() {
		return reporter;
	}

	/**
	 * @param reporter the reporter to set
	 */
	public void setReporter(CktlAbstractReporter reporter) {
		this.reporter = reporter;
	}

	/**
	 * @return the reporterProgress
	 */
	public ReporterAjaxProgress reporterProgress() {
		return reporterProgress;
	}

	/**
	 * @param reporterProgress the reporterProgress to set
	 */
	public void setReporterProgress(ReporterAjaxProgress reporterProgress) {
		this.reporterProgress = reporterProgress;
	}

	/**
	 * @return the reportFilename
	 */
	public String reportFilename() {
		return reportFilename;
	}

	/**
	 * @param reportFilename the reportFilename to set
	 */
	public void setReportFilename(String reportFilename) {
		this.reportFilename = reportFilename;
	}

	/**
	 * @return the temcreate
	 */
	public Boolean getTemcreate() {
		return temcreate;
	}

	/**
	 * @param temcreate the temcreate to set
	 */
	public void setTemcreate(Boolean temcreate) {
		this.temcreate = temcreate;
		mySession().setTemcreate(temcreate);
	}

	/**
	 * @return the temupdate
	 */
	public Boolean getTemupdate() {
		return temupdate;
	}

	/**
	 * @param temupdate the temupdate to set
	 */
	public void setTemupdate(Boolean temupdate) {
		this.temupdate = temupdate;
		mySession().setTemupdate(temupdate);
	}

	public static class ReporterAjaxProgress extends CktlAbstractReporterAjaxProgress implements IJrxmlReportListener {

        public ReporterAjaxProgress(int maximum) {
            super(maximum);
        }
	    
	}
}
