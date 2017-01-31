
package org.pentaho.pdi.steps.googleadexchangeInput;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.pdi.steps.googleadexchangeInput.api.DateRangeType;
import org.w3c.dom.Node;




@Step(	
		id = "GoogleAdExchangeInputStep",
		image = "org/pentaho/pdi/steps/googleadexchangeInput/resources/GoogleAdExchange.png",
		i18nPackageName="org.pentaho.pdi.steps.googleadexchangeInput",
		name="GoogleAdExchangeInputStep.Name",
		description = "GoogleAdExchangeInputStep.TooltipDesc",
		categoryDescription="i18n:org.pentaho.di.trans.step:BaseStep.Category.Transform",
		documentationUrl = "#"
		
)
public class GoogleAdExchangeInputStepMeta extends BaseStepMeta implements StepMetaInterface {

	
	private static Class<?> PKG = GoogleAdExchangeInputStepMeta.class; // for i18n purposes

	public static final String FIELD_TYPE_DIMENSION = "Dimension";
	public static final String FIELD_TYPE_METRIC = "Metric";
	public static final String FIELD_TYPE_METRIC_CURRENCY = "Metric Currency";
	
	public static final String DEFAULT_GA_APPLICATION_NAME = "PDI";
	
	  // The following is deprecated and removed by Google, and remains here only to allow old transformations to load
	  // successfully in Spoon.
	  public static final String DEPRECATED_FIELD_TYPE_CONFIDENCE_INTERVAL = "Confidence Interval for Metric";

	  private String oauthKeyFile;
	  private String appName;
	  private String accountID ;
	  private String authTokenPath;
	  
	  
	  
	  private String dateType;
	  private String startDate;
	  private String endDate;
	  private String dimensions;
	  
	  private String metrics;
	  private String filters;
	  private String sort;
	  
	  private String[] feedField;
	  private String[] feedFieldType;
	  private String[] outputField;
	  private int[] outputType;
	  private String[] conversionMask;

	  private int rowLimit;

	public GoogleAdExchangeInputStepMeta() {
		super(); 
	}

	  
	  public String[] getConversionMask() {
	    return conversionMask;
	  }

	  public String getAppName() {
	    return appName;
	  }

	  public void setAppName( String appName ) {
	    this.appName = appName;
	  }



	  public String getDimensions() {
	    return dimensions;
	  }

	  public void setDimensions( String dimensions ) {
	    this.dimensions = dimensions;
	  }

	  public String getMetrics() {
	    return metrics;
	  }

	  public void setMetrics( String metrics ) {
	    this.metrics = metrics;
	  }

	  public String getFilters() {
	    return filters;
	  }

	  public void setFilters( String filters ) {
	    this.filters = filters;
	  }

	  
	  public String getStartDate() {
	    return startDate;
	  }

	  public void setStartDate( String startDate ) {
	    this.startDate = startDate;
	  }

	  public String getEndDate() {
	    return endDate;
	  }

	  public void setEndDate( String endDate ) {
	    this.endDate = endDate;
	  }
	  public String[] getFeedFieldType() {
	    return feedFieldType;
	  }

	  public String[] getFeedField() {
	    return feedField;
	  }

	  public String[] getOutputField() {
	    return outputField;
	  }

	  public int[] getOutputType() {
	    return outputType;
	  }

	  
	  

	
	// set sensible defaults for a new step
	  public void setDefault() {
	    
	    oauthKeyFile = "";
	    accountID ="";
	    
	    dimensions = "DATE";
	    sort="";
	    metrics = "AD_REQUESTS";
	    startDate = new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date() );
	    endDate = new String( startDate );
	    
	    
	    dateType=DateRangeType.CUSTOM_DATE;
	    appName = DEFAULT_GA_APPLICATION_NAME;
	    authTokenPath=System.getProperty("user.home");
	    rowLimit = 0;
	    // default is to have no key lookup settings
	    allocate( 0 );
	    
	  }

	  // helper method to allocate the arrays
	  public void allocate( int nrkeys ) {

	    feedField = new String[ nrkeys ];
	    outputField = new String[ nrkeys ];
	    outputType = new int[ nrkeys ];
	    feedFieldType = new String[ nrkeys ];
	    conversionMask = new String[ nrkeys ];
	  }

	  public void getFields( RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep,
	                         VariableSpace space, Repository repository, IMetaStore metaStore ) {

	    // clear the output
	    r.clear();
	    // append the outputFields to the output
	    for ( int i = 0; i < outputField.length; i++ ) {
	      ValueMetaInterface v = new ValueMeta( outputField[ i ], outputType[ i ] );
	      // that would influence the output
	      // v.setConversionMask(conversionMask[i]);
	      v.setOrigin( origin );
	      r.addValueMeta( v );
	    }

	  }

	  public Object clone() {

	    // field by field copy is default
		  GoogleAdExchangeInputStepMeta retval = (GoogleAdExchangeInputStepMeta) super.clone();

	    // add proper deep copy for the collections
	    int nrKeys = feedField.length;

	    retval.allocate( nrKeys );

	    for ( int i = 0; i < nrKeys; i++ ) {
	      retval.feedField[ i ] = feedField[ i ];
	      retval.outputField[ i ] = outputField[ i ];
	      retval.outputType[ i ] = outputType[ i ];
	      retval.feedFieldType[ i ] = feedFieldType[ i ];
	      retval.conversionMask[ i ] = conversionMask[ i ];
	    }

	    return retval;
	  }

	  private boolean getBooleanAttributeFromNode( Node node, String tag ) {
	    String sValue = XMLHandler.getTagValue( node, tag );
	    return ( sValue != null && sValue.equalsIgnoreCase( "Y" ) );

	  }

	  public String getXML() throws KettleValueException {

	    StringBuilder retval = new StringBuilder( 800 );
	    
	    retval.append( "    " ).append( XMLHandler.addTagValue( "appName", appName ) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "oauthKeyFile", oauthKeyFile ) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "accountID", accountID ) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "authTokenPath", authTokenPath ) );
	    
	    retval.append( "    " ).append( XMLHandler.addTagValue( "dateType", dateType) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "startDate", startDate ) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "endDate", endDate ) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "dimensions", dimensions ) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "metrics", metrics ) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "filters", filters ) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "sort", sort ) );
	    retval.append( "    " ).append( XMLHandler.addTagValue( "rowLimit", rowLimit ) );


	    for ( int i = 0; i < feedField.length; i++ ) {
	      retval.append( "      <feedField>" ).append( Const.CR );
	      retval.append( "        " ).append( XMLHandler.addTagValue( "feedFieldType", feedFieldType[ i ] ) );
	      retval.append( "        " ).append( XMLHandler.addTagValue( "feedField", feedField[ i ] ) );
	      retval.append( "        " ).append( XMLHandler.addTagValue( "outField", outputField[ i ] ) );
	      retval
	        .append( "        " ).append( XMLHandler.addTagValue( "type", ValueMeta.getTypeDesc( outputType[ i ] ) ) );
	      retval.append( "        " ).append( XMLHandler.addTagValue( "conversionMask", conversionMask[ i ] ) );
	      retval.append( "      </feedField>" ).append( Const.CR );
	    }
	    return retval.toString();
	  }

	  public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {

	    try {
	      // Check for legacy fields (user/pass/API key), present an error if found
	      String user = XMLHandler.getTagValue( stepnode, "user" );
	      String pass = XMLHandler.getTagValue( stepnode, "pass" );
	      String apiKey = XMLHandler.getTagValue( stepnode, "apiKey" );

	      
	      oauthKeyFile = XMLHandler.getTagValue( stepnode, "oauthKeyFile" );

	      // Are we loading a legacy transformation?
	      if ( ( user != null || pass != null || apiKey != null )
	        && (  oauthKeyFile == null ) ) {
	        logError( BaseMessages.getString( PKG, "GoogleAdExchange.Error.TransformationUpdateNeeded" ) );
	      }
	      appName = XMLHandler.getTagValue( stepnode, "appName" );
	      accountID= XMLHandler.getTagValue( stepnode, "accountID" );
	      authTokenPath= XMLHandler.getTagValue( stepnode, "authTokenPath" );
	      
	      
	      
	      dateType = XMLHandler.getTagValue( stepnode, "dateType" );
	      
	      startDate = XMLHandler.getTagValue( stepnode, "startDate" );
	      endDate = XMLHandler.getTagValue( stepnode, "endDate" );
	      dimensions = XMLHandler.getTagValue( stepnode, "dimensions" );
	      metrics = XMLHandler.getTagValue( stepnode, "metrics" );
	      filters = XMLHandler.getTagValue( stepnode, "filters" );
	      sort= XMLHandler.getTagValue( stepnode, "sort" );
	      rowLimit = Const.toInt( XMLHandler.getTagValue( stepnode, "rowLimit" ), 0 );
	      

	      allocate( 0 );

	      int nrFields = XMLHandler.countNodes( stepnode, "feedField" );
	      allocate( nrFields );

	      for ( int i = 0; i < nrFields; i++ ) {
	        Node knode = XMLHandler.getSubNodeByNr( stepnode, "feedField", i );

	        feedFieldType[ i ] = XMLHandler.getTagValue( knode, "feedFieldType" );
	        feedField[ i ] = XMLHandler.getTagValue( knode, "feedField" );
	        outputField[ i ] = XMLHandler.getTagValue( knode, "outField" );
	        outputType[ i ] = ValueMeta.getType( XMLHandler.getTagValue( knode, "type" ) );
	        conversionMask[ i ] = XMLHandler.getTagValue( knode, "conversionMask" );

	        if ( outputType[ i ] < 0 ) {
	          outputType[ i ] = ValueMetaInterface.TYPE_STRING;
	        }

	      }

	    } catch ( Exception e ) {
	      throw new KettleXMLException( BaseMessages.getString( PKG, "GoogleAdExchange.Error.UnableToReadFromXML" ), e );
	    }

	  }

	  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
	    throws KettleException {
	    try {
	      String user = rep.getStepAttributeString( id_step, "user" );
	      String pass = rep.getStepAttributeString( id_step, "pass" );
	      String apiKey = rep.getStepAttributeString( id_step, "apiKey" );

	      oauthKeyFile = rep.getStepAttributeString( id_step, "oauthKeyFile" );

	      // Are we loading a legacy transformation?
	      if ( ( user != null || pass != null || apiKey != null )
	        && (  oauthKeyFile == null ) ) {
	        logError( BaseMessages.getString( PKG, "GoogleAdExchange.Error.TransformationUpdateNeeded" ) );
	      }
	      
	      appName = rep.getStepAttributeString( id_step, "appName" );
	      accountID= rep.getStepAttributeString( id_step, "accountID" );
	      authTokenPath= rep.getStepAttributeString( id_step, "authTokenPath" );
	      
	      
	      
	      dateType = rep.getStepAttributeString( id_step, "dateType" );
	      
	      startDate = rep.getStepAttributeString( id_step, "startDate" );
	      endDate = rep.getStepAttributeString( id_step, "endDate" );
	      dimensions = rep.getStepAttributeString( id_step, "dimensions" );
	      
	      metrics = rep.getStepAttributeString( id_step, "metrics" );
	      filters = rep.getStepAttributeString( id_step, "filters" );
	      sort= rep.getStepAttributeString( id_step, "sort" );
	      rowLimit = (int) rep.getStepAttributeInteger( id_step, "rowLimit" );
	      

	      int nrFields = rep.countNrStepAttributes( id_step, "feedField" );
	      allocate( nrFields );

	      for ( int i = 0; i < nrFields; i++ ) {

	        feedFieldType[ i ] = rep.getStepAttributeString( id_step, i, "feedFieldType" );
	        feedField[ i ] = rep.getStepAttributeString( id_step, i, "feedField" );
	        outputField[ i ] = rep.getStepAttributeString( id_step, i, "outField" );
	        outputType[ i ] = ValueMeta.getType( rep.getStepAttributeString( id_step, i, "type" ) );
	        conversionMask[ i ] = rep.getStepAttributeString( id_step, i, "conversionMask" );

	        if ( outputType[ i ] < 0 ) {
	          outputType[ i ] = ValueMetaInterface.TYPE_STRING;
	        }
	      }
	    } catch ( Exception e ) {
	      throw new KettleException( BaseMessages.getString( PKG, "GoogleAdExchange.Error.UnableToReadFromRep" ), e );
	    }
	  }

	  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
	    throws KettleException {
	    try {
	      rep.saveStepAttribute( id_transformation, id_step, "oauthKeyFile", oauthKeyFile );
	      rep.saveStepAttribute( id_transformation, id_step, "appName", appName );
	      rep.saveStepAttribute( id_transformation, id_step, "accountID", accountID );
	      rep.saveStepAttribute( id_transformation, id_step, "authTokenPath", authTokenPath );
	      
	      
	      
	      rep.saveStepAttribute( id_transformation, id_step, "dateType", dateType );
	      rep.saveStepAttribute( id_transformation, id_step, "startDate", startDate );
	      rep.saveStepAttribute( id_transformation, id_step, "endDate", endDate );
	      rep.saveStepAttribute( id_transformation, id_step, "dimensions", dimensions );
	      
	      rep.saveStepAttribute( id_transformation, id_step, "metrics", metrics );
	      rep.saveStepAttribute( id_transformation, id_step, "filters", filters );
	      rep.saveStepAttribute( id_transformation, id_step, "sort", sort );
	      rep.saveStepAttribute( id_transformation, id_step, "rowLimit", rowLimit );

	      for ( int i = 0; i < feedField.length; i++ ) {
	        rep.saveStepAttribute( id_transformation, id_step, i, "feedFieldType", feedFieldType[ i ] );
	        rep.saveStepAttribute( id_transformation, id_step, i, "feedField", feedField[ i ] );
	        rep.saveStepAttribute( id_transformation, id_step, i, "outField", outputField[i] );
	        rep.saveStepAttribute( id_transformation, id_step, i, "conversionMask", conversionMask[ i ] );
	        rep.saveStepAttribute( id_transformation, id_step, i, "type", ValueMeta.getTypeDesc( outputType[ i ] ) );

	      }

	    } catch ( Exception e ) {
	      throw new KettleException( BaseMessages.getString( PKG, "GoogleAdExchange.Error.UnableToSaveToRep" )
	        + id_step, e );
	    }
	  }

	  public void check( List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta,
	                     RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info, VariableSpace space,
	                     Repository repository, IMetaStore metaStore ) {
	    CheckResult cr;

	    if ( prev == null || prev.size() == 0 ) {
	      cr =
	        new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(
	          PKG, "GoogleAdExchange.CheckResult.NotReceivingFields" ), stepMeta );
	      remarks.add( cr );
	    } else {
	      cr =
	        new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(
	          PKG, "GoogleAdExchange.CheckResult.StepRecevingData", prev.size() + "" ), stepMeta );
	      remarks.add( cr );
	    }

	    // See if we have input streams leading to this step!
	    if ( input.length > 0 ) {
	      cr =
	        new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString(
	          PKG, "GoogleAdExchange.CheckResult.StepRecevingData2" ), stepMeta );
	      remarks.add( cr );
	    } else {
	      cr =
	        new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(
	          PKG, "GoogleAdExchange.CheckResult.NoInputReceivedFromOtherSteps" ), stepMeta );
	      remarks.add( cr );
	    }

	  }

	  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr,
	                                TransMeta transMeta, Trans disp ) {
	    return new GoogleAdExchangeInputStep( stepMeta, stepDataInterface, cnr, transMeta, disp );
	  }

	  public StepDataInterface getStepData() {
	    return new GoogleAdExchangeInputStepData();
	  }

	  public String getOAuthKeyFile() {
	    return oauthKeyFile;
	  }

	  public void setOAuthKeyFile( String oauthKeyFile ) {
	    this.oauthKeyFile = oauthKeyFile;
	  }

	  

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}


	public String getAccountID() {
		return accountID;
	}


	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}


	

	public String getSort() {
		return sort;
	}


	public void setSort(String sort) {
		this.sort = sort;
	}


	public int getRowLimit() {
		return rowLimit;
	}


	public void setRowLimit(int rowLimit) {
		if ( rowLimit < 0 ) {
		      rowLimit = 0;
		    }
		this.rowLimit = rowLimit;
	}


	
	public String getAuthTokenPath() {
		return authTokenPath;
	}


	public void setAuthTokenPath(String authTokenPath) {
		this.authTokenPath = authTokenPath;
	}
	
	
}
