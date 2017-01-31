
package org.pentaho.pdi.steps.googleadexchangeInput;


import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.pdi.steps.googleadexchangeInput.api.DateRangeType;
import org.pentaho.pdi.steps.googleadexchangeInput.api.GoogleAdExchangeAPI;




public class GoogleAdExchangeInputStep extends BaseStep implements StepInterface {

	
	GoogleAdExchangeInputStepMeta meta ;
	GoogleAdExchangeInputStepData data ;
	private static Class<?> PKG = GoogleAdExchangeInputStepMeta.class; // for i18n purposes
	
	public GoogleAdExchangeInputStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}
	
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		// Casting to step-specific implementation classes is safe
		GoogleAdExchangeInputStepMeta meta = (GoogleAdExchangeInputStepMeta) smi;
		GoogleAdExchangeInputStepData data = (GoogleAdExchangeInputStepData) sdi;
		if(!super.init(meta, data))
		{
			return false;
		}

		
		return true;
	}	

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		meta = (GoogleAdExchangeInputStepMeta) smi;
		data = (GoogleAdExchangeInputStepData) sdi;
		
		if (first) {
			
			first = false;
			data.outputRowMeta = new RowMeta();
			meta.getFields( data.outputRowMeta, getStepname(), null, null, this, repository, metaStore );
			
			data.conversionMeta = new ValueMetaInterface[ meta.getFeedField().length ];

			for ( int i = 0; i < meta.getFeedField().length; i++ ) {
	

		        ValueMetaInterface returnMeta = data.outputRowMeta.getValueMeta( i );
	
		        ValueMetaInterface conversionMeta;
	
		        conversionMeta = ValueMetaFactory.cloneValueMeta( returnMeta, ValueMetaInterface.TYPE_STRING );
		        conversionMeta.setConversionMask( meta.getConversionMask()[ i ] );
		        conversionMeta.setDecimalSymbol( "." ); 
		        conversionMeta.setGroupingSymbol( null ); 
	
		        data.conversionMeta[ i ] = conversionMeta;
		      }
			
	      try
			{
				data.gAPI = new GoogleAdExchangeAPI(environmentSubstitute(meta.getOAuthKeyFile()),environmentSubstitute(meta.getAppName()),environmentSubstitute(meta.getAuthTokenPath()));

				if (log.isBasic()) logBasic(BaseMessages.getString( PKG, "GoogleAdExchangeStep.AuthenticationSuccess.Message" ) );
				
				data.gAPI.setAccountId(environmentSubstitute(meta.getAccountID()));
				
				data.gAPI.setDateRange(environmentSubstitute(meta.getDateType()));
				if(environmentSubstitute(data.gAPI.getDateRange()).equalsIgnoreCase(DateRangeType.CUSTOM_DATE))
				{
					data.gAPI.setStartDate(environmentSubstitute(meta.getStartDate()));
					data.gAPI.setEndDate(environmentSubstitute(meta.getEndDate()));
				}
				data.gAPI.setDimensions(environmentSubstitute(meta.getDimensions()));
				data.gAPI.setMetrics(environmentSubstitute(meta.getMetrics()));
				data.gAPI.setFilters(environmentSubstitute(meta.getFilters()));
				data.gAPI.setSort(environmentSubstitute(meta.getSort()));
				
				data.gAPI.setRowLimit(meta.getRowLimit());
				
				/*if(isDetailed())
					logDetailed("Configuration Properties : "+ meta.getXML());*/
			}
			catch(Exception e)
			{
				logError(e.getMessage() );
				e.printStackTrace();
				setErrors(1);
				stopAll();
			}
			
		}
		
		Object[] outputRow = RowDataUtil.allocateRowData( data.outputRowMeta.size() );
		List <String> entry  = getNextDataEntry();
		
		if ( entry != null) { 

			for ( int i = 0; i < meta.getFeedField().length; i++ ) {
				
				Object dataObject = entry.get(i);
		        outputRow[ i ] = data.outputRowMeta.getValueMeta( i ).convertData( data.conversionMeta[ i ], dataObject );
		      }

			putRow( data.outputRowMeta, outputRow );
			
			if ( checkFeedback( getLinesWritten() ) ) {
		        if ( log.isBasic() ) {
		          logBasic( "Linenr " + getLinesWritten() );
		        }
		      }
		      return true;

		} 
		else {
			setOutputDone();
			return false;
		}
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

		GoogleAdExchangeInputStepMeta meta = (GoogleAdExchangeInputStepMeta) smi;
		GoogleAdExchangeInputStepData data = (GoogleAdExchangeInputStepData) sdi;
		
		super.dispose(meta, data);
	}

	private List<String> getNextDataEntry() throws KettleException {
	     
		if ( data.rows == null ) {
			
			try {
					logBasic( "Retriving Google Ad Exchange data..." );
					data.response = data.gAPI.runRerpots();
					data.totalRows = data.response.getTotalMatchedRows();
					data.rows =data.response.getRows();
					if(meta.getRowLimit() > 0)
						data.totalRows = Math.min(data.response.getTotalMatchedRows(), meta.getRowLimit());
					else
						data.totalRows = data.response.getTotalMatchedRows();
					
					logBasic( "Google Ad Exchange data retrieved successfully." );
					logDetailed( "Report successfully downloaded : " + data.totalRows + " Rows Featched.");
					data.entryIndex = 0;
			} catch ( Exception e2 ) {
		    	e2.printStackTrace();
		    	logError(e2.getMessage());
		        throw new KettleException( e2 );
			}

		}
	    if (data.entryIndex < data.rows.size()  ) {
	      incrementLinesInput();
	      if(meta.getRowLimit()<1 || getLinesInput() <= meta.getRowLimit())
	    	  return data.rows.get( data.entryIndex++ );
	      else
	    	  return null;
	      
	    } 
	    else {
	    	
	    	return null;
	    	 
	    }
	}
}
