
package org.pentaho.pdi.steps.googleadexchangeInput;

import java.util.List;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.pdi.steps.googleadexchangeInput.api.GoogleAdExchangeAPI;


import com.google.api.services.adexchangeseller.model.Report;


public class GoogleAdExchangeInputStepData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;
	public ValueMetaInterface[] conversionMeta;
	public List<List <String>> rows;
	public int entryIndex;
	public GoogleAdExchangeAPI gAPI; 
	public long totalRows=0;
	public Report response;
    public GoogleAdExchangeInputStepData()
	{
		super();
	}
}
	
