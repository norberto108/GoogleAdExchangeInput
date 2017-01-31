

package org.pentaho.pdi.steps.googleadexchangeInput;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransPreviewFactory;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.dialog.EnterNumberDialog;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.PreviewRowsDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.dialog.TransPreviewProgressDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.pdi.steps.googleadexchangeInput.api.DateRangeType;
import org.pentaho.pdi.steps.googleadexchangeInput.api.GoogleAdExchangeAPI;

import com.google.api.services.adexchangeseller.model.Report.Headers;





public class GoogleAdExchangeInputStepDialog extends BaseStepDialog implements StepDialogInterface {

	
	private static Class<?> PKG = GoogleAdExchangeInputStepMeta.class; // for i18n purposes
	
	private GoogleAdExchangeInputStepMeta meta;
	private GoogleAdExchangeAPI gAPI;

	
	private Button fileChooser;
	private TextVar keyFilename;

	
	private Button browseAuthPath;
	private TextVar authPath;
	
	private Label wlAccountId;
	private ComboVar   wAccountId;
	private Button wGetAcccountId;
	
	private Link wlFields;
	private TableView wFields;

	private Label wlQuStartDate;
	private TextVar wQuStartDate;

	private Label wlQuEndDate;
	private TextVar wQuEndDate;

	private Label wlQuDimensions;
	private TextVar wQuDimensions;


	private Label wlQuMetrics;
	private TextVar wQuMetrics;

	private Label wlQuFilters;
	private TextVar wQuFilters;

	private Label wlQuSort;
	private TextVar wQuSort;

	private Link wQuFiltersReference;
	private Link wQuFilterableReference;
	
	private Link wQuStartDateReference;
	private Link wQuEndDateReference;
	
	private Link wQuMetricsReference;
	private Link wQuDimensionsReference;
  
	

	private Group gConnect;
	
	
	
	private Label wlQuDateType;
	private ComboVar wQuDateType;

	private Label wlAppName;
	private TextVar wAppName;

	private int middle;
	private int margin;

	private Button wQuLoadQuery;
	private Button wQuSaveQuery;
	
	
	private Label wlLimit;
	private Text wLimit;


	private ModifyListener lsMod;

	  
	static final String REFERENCE_METRICS_URI =
	    "https://developers.google.com/ad-exchange/seller-rest/metrics-dimensions";
	static final String REFERENCE_DIMENSIONS_URI =
	    "https://developers.google.com/ad-exchange/seller-rest/metrics-dimensions";
	//static final String REFERENCE_SORT_URI =
		//"https://developers.google.com/doubleclick-publishers/docs/reference/v201511/ReportService.DimensionAttribute";
  
	static final String REFERENCE_FILTERS_URI =
		"https://developers.google.com/ad-exchange/seller-rest/filters#filterSyntax";
	  // Filter Able link
	
	static final String REFERENCE_DATE_URL =
			"https://developers.google.com/ad-exchange/seller-rest/reporting/relative-dates";
	
	public GoogleAdExchangeInputStepDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		meta = (GoogleAdExchangeInputStepMeta) in;
	}

	public String open() {


		Shell parent = getParent();
	    Display display = parent.getDisplay();
	    transMeta.activateParameters();
	    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
	    props.setLook( shell );
	    setShellImage( shell, meta );

	    lsMod = new ModifyListener() {
	      public void modifyText( ModifyEvent e ) {
	    	  meta.setChanged();
	      }
	    };
	    backupChanged = meta.hasChanged();

	    FormLayout formLayout = new FormLayout();
	    formLayout.marginWidth = Const.FORM_MARGIN;
	    formLayout.marginHeight = Const.FORM_MARGIN;

	    shell.setLayout( formLayout );
	    shell.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Shell.Title" ) + BaseMessages.getString( PKG, "plugin.version.major" ) +"." + BaseMessages.getString( PKG, "plugin.version.minor" ) );

	    middle = props.getMiddlePct();
	    margin = Const.MARGIN;

	  
	    wlStepname = new Label( shell, SWT.RIGHT );
	    wlStepname.setText( BaseMessages.getString( PKG, "System.Label.StepName" ) );
	    props.setLook( wlStepname );
	    fdlStepname = new FormData();
	    fdlStepname.left = new FormAttachment( 0, 0 );
	    fdlStepname.right = new FormAttachment( middle, -margin );
	    fdlStepname.top = new FormAttachment( 0, margin );
	    wlStepname.setLayoutData( fdlStepname );

	    wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wStepname.setText( stepname );
	    props.setLook( wStepname );
	    wStepname.addModifyListener( lsMod );
	    fdStepname = new FormData();
	    fdStepname.left = new FormAttachment( middle, 0 );
	    fdStepname.top = new FormAttachment( 0, margin );
	    fdStepname.right = new FormAttachment( 100, 0 );
	    wStepname.setLayoutData( fdStepname );

	   

	    gConnect = new Group( shell, SWT.SHADOW_ETCHED_IN );
	    gConnect.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.ConnectGroup.Label" ) );
	    FormLayout gConnectLayout = new FormLayout();
	    gConnectLayout.marginWidth = 3;
	    gConnectLayout.marginHeight = 3;
	    gConnect.setLayout( gConnectLayout );
	    props.setLook( gConnect );

	    FormData fdConnect = new FormData();
	    fdConnect.left = new FormAttachment( 0, 0 );
	    fdConnect.right = new FormAttachment( 100, 0 );
	    fdConnect.top = new FormAttachment( wStepname, margin );
	    gConnect.setLayoutData( fdConnect );

	    // Google Analytics app name
	    wlAppName = new Label( gConnect, SWT.RIGHT );
	    wlAppName.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AppName.Label" ) );
	    props.setLook( wlAppName );
	    FormData fdlGaAppName = new FormData();
	    fdlGaAppName.top = new FormAttachment( 0, margin );
	    fdlGaAppName.left = new FormAttachment( 0, 0 );
	    fdlGaAppName.right = new FormAttachment( middle, -margin );
	    wlAppName.setLayoutData( fdlGaAppName );
	    wAppName = new TextVar( transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wAppName.addModifyListener( lsMod );
	    wAppName.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AppName.Tooltip" ) );
	    props.setLook( wAppName );
	    FormData fdGaAppName = new FormData();
	    fdGaAppName.top = new FormAttachment( wStepname, margin );
	    fdGaAppName.left = new FormAttachment( middle, 0 );
	    fdGaAppName.right = new FormAttachment( 100, 0 );
	    wAppName.setLayoutData( fdGaAppName );


	    createOauthServiceCredentialsControls();



	    Group gQuery = new Group( shell, SWT.SHADOW_ETCHED_IN );
	    gQuery.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.QueryGroup.Label" ) );
	    FormLayout gQueryLayout = new FormLayout();
	    gQueryLayout.marginWidth = 3;
	    gQueryLayout.marginHeight = 3;
	    gQuery.setLayout( gQueryLayout );
	    props.setLook( gQuery );
	    
	   
	    
	    
	    
	    


	    // query Date Type
	    wlQuDateType = new Label( gQuery, SWT.RIGHT );
	    wlQuDateType.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.DateType.Label" ) );
	    props.setLook( wlQuDateType );
	    FormData fdlQuDateType = new FormData();
	    fdlQuDateType.top = new FormAttachment( 0, margin );
	    fdlQuDateType.left = new FormAttachment( 0, 0 );
	    fdlQuDateType.right = new FormAttachment( middle, -margin );
	    wlQuDateType.setLayoutData( fdlQuDateType );
	    wQuDateType = new ComboVar( transMeta, gQuery, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wQuDateType.add(DateRangeType.CUSTOM_DATE);
	    wQuDateType.add(DateRangeType.TODAY);
	    wQuDateType.add(DateRangeType.YESTERDAY);
	    wQuDateType.add(DateRangeType.LAST_7Days);
	    wQuDateType.add(DateRangeType.LAST_MONTH);
	    wQuDateType.add(DateRangeType.LAST_YEAR);
	    
	    wQuDateType.setText(DateRangeType.CUSTOM_DATE);
	    
	    wQuDateType.addModifyListener( lsMod );
	    
	    wQuDateType.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		transMeta.activateParameters();
	    		if(transMeta.environmentSubstitute(wQuDateType.getText()).equalsIgnoreCase(DateRangeType.CUSTOM_DATE))
	    		{
	    			wQuStartDate.setEnabled(true);
	    			wQuEndDate.setEnabled(true);
	    		}
	    		else
	    		{
	    			wQuStartDate.setEnabled(false);
	    			wQuEndDate.setEnabled(false);
	    		}
	    		
	    	}
	    });
	    wQuDateType.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.DateType.Tooltip" ) );
	    props.setLook( wQuDateType );
	    FormData fdQuDateType = new FormData();
	    fdQuDateType.top = new FormAttachment( 0, margin );
	    fdQuDateType.left = new FormAttachment( middle, 0 );
	    fdQuDateType.right = new FormAttachment( 100, 0 );
	    wQuDateType.setLayoutData( fdQuDateType );

	    
	    
	    // query start date
	    wlQuStartDate = new Label( gQuery, SWT.RIGHT );
	    wlQuStartDate.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.StartDate.Label" ) );
	    props.setLook( wlQuStartDate );
	    FormData fdlQuStartDate = new FormData();
	    fdlQuStartDate.top = new FormAttachment( wQuDateType, margin );
	    fdlQuStartDate.left = new FormAttachment( 0, 0 );
	    fdlQuStartDate.right = new FormAttachment( middle, -margin );
	    wlQuStartDate.setLayoutData( fdlQuStartDate );
	    wQuStartDate = new TextVar( transMeta, gQuery, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wQuStartDate.addModifyListener( lsMod );
	    wQuStartDate.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.StartDate.Tooltip" ) );
	    props.setLook( wQuStartDate );
	    
	    wQuStartDateReference = new Link( gQuery, SWT.SINGLE );

	    wQuStartDateReference.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Reference.Label" ) );
	    props.setLook( wQuStartDateReference );
	    wQuStartDateReference.addListener( SWT.Selection, new Listener() {
	      @Override
	      public void handleEvent( Event ev ) {
	        BareBonesBrowserLaunch.openURL( REFERENCE_DATE_URL );
	      }
	    } );

	    wQuStartDateReference.pack( true );
	    
	    FormData fdQuStartDate = new FormData();
	    fdQuStartDate.top = new FormAttachment( wQuDateType, margin );
	    fdQuStartDate.left = new FormAttachment( middle, 0 );
	    fdQuStartDate.right = new FormAttachment( 100, -wQuStartDateReference.getBounds().width - margin);
	    wQuStartDate.setLayoutData( fdQuStartDate );

	    FormData fdQuStartDateReference = new FormData();
	    fdQuStartDateReference.top = new FormAttachment( wQuDateType, margin );
	    fdQuStartDateReference.left = new FormAttachment( wQuStartDate, 0 );
	    fdQuStartDateReference.right = new FormAttachment( 100, 0);
	    wQuStartDateReference.setLayoutData( fdQuStartDateReference );
	    
	    // query end date
	    wlQuEndDate = new Label( gQuery, SWT.RIGHT );
	    wlQuEndDate.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.EndDate.Label" ) );
	    props.setLook( wlQuEndDate );
	    FormData fdlQuEndDate = new FormData();
	    fdlQuEndDate.top = new FormAttachment( wQuStartDate, margin );
	    fdlQuEndDate.left = new FormAttachment( 0, 0 );
	    fdlQuEndDate.right = new FormAttachment( middle, -margin );
	    wlQuEndDate.setLayoutData( fdlQuEndDate );
	    wQuEndDate = new TextVar( transMeta, gQuery, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wQuEndDate.addModifyListener( lsMod );
	    wQuEndDate.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.EndDate.Tooltip" ) );
	    props.setLook( wQuEndDate );
	    
	    wQuEndDateReference = new Link( gQuery, SWT.SINGLE );

	    wQuEndDateReference.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Reference.Label" ) );
	    props.setLook( wQuEndDateReference );
	    wQuEndDateReference.addListener( SWT.Selection, new Listener() {
	      @Override
	      public void handleEvent( Event ev ) {
	        BareBonesBrowserLaunch.openURL( REFERENCE_DATE_URL );
	      }
	    } );

	    wQuEndDateReference.pack( true );
	    
	    FormData fdQuEndDate = new FormData();
	    fdQuEndDate.top = new FormAttachment( wQuStartDate, margin );
	    fdQuEndDate.left = new FormAttachment( middle, 0 );
	    fdQuEndDate.right = new FormAttachment( 100, -wQuEndDateReference.getBounds().width - margin );
	    wQuEndDate.setLayoutData( fdQuEndDate );

	    FormData fdQuEndDateReference = new FormData();
	    fdQuEndDateReference.top = new FormAttachment( wQuStartDate, margin );
	    fdQuEndDateReference.left = new FormAttachment( wQuEndDate, 0 );
	    fdQuEndDateReference.right = new FormAttachment( 100, 0);
	    wQuEndDateReference.setLayoutData( fdQuEndDateReference);
	    
	    // query dimensions
	    wlQuDimensions = new Label( gQuery, SWT.RIGHT );
	    wlQuDimensions.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Dimensions.Label" ) );
	    props.setLook( wlQuDimensions );
	    FormData fdlQuDimensions = new FormData();
	    fdlQuDimensions.top = new FormAttachment( wQuEndDate, margin );
	    fdlQuDimensions.left = new FormAttachment( 0, 0 );
	    fdlQuDimensions.right = new FormAttachment( middle, -margin );
	    wlQuDimensions.setLayoutData( fdlQuDimensions );
	    wQuDimensions = new TextVar( transMeta, gQuery, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wQuDimensions.addModifyListener( lsMod );
	    wQuDimensions.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Dimensions.Tooltip" ) );
	    props.setLook( wQuDimensions );

	    wQuDimensionsReference = new Link( gQuery, SWT.SINGLE );

	    wQuDimensionsReference.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Reference.Label" ) );
	    props.setLook( wQuDimensionsReference );
	    wQuDimensionsReference.addListener( SWT.Selection, new Listener() {
	      @Override
	      public void handleEvent( Event ev ) {
	        BareBonesBrowserLaunch.openURL( REFERENCE_DIMENSIONS_URI );
	      }
	    } );

	    wQuDimensionsReference.pack( true );

	    FormData fdQuDimensions = new FormData();
	    fdQuDimensions.top = new FormAttachment( wQuEndDate, margin );
	    fdQuDimensions.left = new FormAttachment( middle, 0 );
	    fdQuDimensions.right = new FormAttachment( 100, -wQuDimensionsReference.getBounds().width - margin );
	    wQuDimensions.setLayoutData( fdQuDimensions );

	    FormData fdQuDimensionsReference = new FormData();
	    fdQuDimensionsReference.top = new FormAttachment( wQuEndDate, margin );
	    fdQuDimensionsReference.left = new FormAttachment( wQuDimensions, 0 );
	    fdQuDimensionsReference.right = new FormAttachment( 100, 0 );
	    wQuDimensionsReference.setLayoutData( fdQuDimensionsReference );
	    
	    
	      // query Metrics
	    wlQuMetrics = new Label( gQuery, SWT.RIGHT );
	    wlQuMetrics.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Metrics.Label" ) );
	    props.setLook( wlQuMetrics );
	    FormData fdlQuMetrics = new FormData();
	    fdlQuMetrics.top = new FormAttachment( wQuDimensions, margin );
	    fdlQuMetrics.left = new FormAttachment( 0, 0 );
	    fdlQuMetrics.right = new FormAttachment( middle, -margin );
	    wlQuMetrics.setLayoutData( fdlQuMetrics );
	    wQuMetrics = new TextVar( transMeta, gQuery, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wQuMetrics.addModifyListener( lsMod );
	    wQuMetrics.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Metrics.Tooltip" ) );
	    props.setLook( wQuMetrics );

	    wQuMetricsReference = new Link( gQuery, SWT.SINGLE );
	    wQuMetricsReference.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Reference.Label" ) );
	    props.setLook( wQuMetricsReference );
	    wQuMetricsReference.addListener( SWT.Selection, new Listener() {
	      @Override
	      public void handleEvent( Event ev ) {
	        BareBonesBrowserLaunch.openURL( REFERENCE_METRICS_URI );
	      }
	    } );

	    wQuMetricsReference.pack( true );

	    FormData fdQuMetrics = new FormData();
	    fdQuMetrics.top = new FormAttachment( wQuDimensions, margin );
	    fdQuMetrics.left = new FormAttachment( middle, 0 );
	    fdQuMetrics.right = new FormAttachment( 100, -wQuMetricsReference.getBounds().width - margin );
	    wQuMetrics.setLayoutData( fdQuMetrics );

	    FormData fdQuMetricsReference = new FormData();
	    fdQuMetricsReference.top = new FormAttachment( wQuDimensions, margin );
	    fdQuMetricsReference.left = new FormAttachment( wQuMetrics, 0 );
	    fdQuMetricsReference.right = new FormAttachment( 100, 0 );
	    wQuMetricsReference.setLayoutData( fdQuMetricsReference );

	    // query filters
	    wlQuFilters = new Label( gQuery, SWT.RIGHT );
	    wlQuFilters.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Filters.Label" ) );
	    props.setLook( wlQuFilters );
	    FormData fdlQuFilters = new FormData();
	    fdlQuFilters.top = new FormAttachment( wQuMetrics, margin );
	    fdlQuFilters.left = new FormAttachment( 0, 0 );
	    fdlQuFilters.right = new FormAttachment( middle, -margin );
	    wlQuFilters.setLayoutData( fdlQuFilters );
	    
	    
	    wQuFilters = new TextVar( transMeta, gQuery, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wQuFilters.addModifyListener( lsMod );
	    wQuFilters.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Filters.Tooltip" ) );
	    props.setLook( wQuFilters );

	    wQuFiltersReference = new Link( gQuery, SWT.SINGLE );
	    wQuFiltersReference.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Reference.Label" ) );
	    props.setLook( wQuFiltersReference );
	    wQuFiltersReference.addListener( SWT.Selection, new Listener() {
	      @Override
	      public void handleEvent( Event ev ) {
	        BareBonesBrowserLaunch.openURL( REFERENCE_FILTERS_URI );
	      }
	    } );

	    wQuFiltersReference.pack( true );


	    wQuFilterableReference = new Link( gQuery, SWT.SINGLE );
	    wQuFilterableReference.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Sample.Label" ) );
	    props.setLook( wQuFilterableReference );
	    wQuFilterableReference.addListener( SWT.Selection, new Listener() {
	      @Override
	      public void handleEvent( Event ev ) {
	    	  MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_INFORMATION );
              mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.FilterSample.DialogTitle" ) );
              mb.setMessage(BaseMessages.getString( PKG, "GoogleAdExchangeDialog.FilterSample.DialogMessage" ) );
              mb.open();
	      }
	    } );

	    wQuFilterableReference.pack( true );

	    
	    
	    FormData fdQuFilters = new FormData();
	    fdQuFilters.top = new FormAttachment( wQuMetrics, margin );
	    fdQuFilters.left = new FormAttachment( middle, 0 );
	    fdQuFilters.right = new FormAttachment( 100, -2*(wQuFiltersReference.getBounds().width) );
	    wQuFilters.setLayoutData( fdQuFilters );

	    FormData fdQuFiltersReference = new FormData();
	    fdQuFiltersReference.top = new FormAttachment( wQuMetrics, margin );
	    fdQuFiltersReference.left = new FormAttachment( wQuFilters, 0 );
	    fdQuFiltersReference.right = new FormAttachment(100,  -(wQuFiltersReference.getBounds().width - margin) );
	    wQuFiltersReference.setLayoutData( fdQuFiltersReference );

	    FormData fdQuFilterableReference = new FormData();
	    fdQuFilterableReference.top = new FormAttachment( wQuMetrics, margin );
	    fdQuFilterableReference.left = new FormAttachment( wQuFiltersReference, 0 );
	    fdQuFilterableReference.right = new FormAttachment( 100, 0 );
	    wQuFilterableReference.setLayoutData( fdQuFilterableReference );

	    //Sort 	    
	    wlQuSort = new Label( gQuery, SWT.RIGHT );
	    wlQuSort.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Sort.Label" ) );
	    props.setLook( wlQuSort );
	    FormData fdlQuSort = new FormData();
	    fdlQuSort.top = new FormAttachment( wQuFilters, margin );
	    fdlQuSort.left = new FormAttachment( 0, 0 );
	    fdlQuSort.right = new FormAttachment( middle, -margin );
	    wlQuSort.setLayoutData( fdlQuSort );
	    
	    wQuSort = new TextVar( transMeta, gQuery, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wQuSort.addModifyListener( lsMod );
	    wQuSort.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Query.Sort.Tooltip" ) );
	    props.setLook( wQuSort );

	   

	    FormData fdQuSort = new FormData();
	    fdQuSort.top = new FormAttachment( wQuFilters, margin );
	    fdQuSort.left = new FormAttachment( middle, 0 );
	    fdQuSort.right = new FormAttachment( 100, 0 );
	    wQuSort.setLayoutData( fdQuSort );

	   


	    
	    //Save Query & Load Query
	    wQuSaveQuery= new Button( gQuery, SWT.PUSH | SWT.CENTER );
	    wQuSaveQuery.setText( BaseMessages.getString( PKG, ( "GoogleAdExchangeDialog.Query.SaveQuery" ) ) );
		props.setLook( wQuSaveQuery );
		
		FormData fdbwQuSaveQuery = new FormData();
		fdbwQuSaveQuery.top = new FormAttachment( wQuSort, margin );
		
		fdbwQuSaveQuery.right = new FormAttachment( middle, -margin );
		
		wQuSaveQuery.setLayoutData( fdbwQuSaveQuery );
		
		wQuLoadQuery= new Button( gQuery, SWT.PUSH | SWT.CENTER );
		wQuLoadQuery.setText( BaseMessages.getString( PKG, ( "GoogleAdExchangeDialog.Query.LoadQuery" ) ) );
		props.setLook( wQuLoadQuery );
		
		FormData fdbwQuLoadQuery = new FormData();
		fdbwQuLoadQuery.top = new FormAttachment( wQuSort, margin );
		fdbwQuLoadQuery.left = new FormAttachment( middle, 0);
				
		wQuLoadQuery.setLayoutData( fdbwQuLoadQuery );
		

	    FormData fdQueryGroup = new FormData();
	    fdQueryGroup.left = new FormAttachment( 0, 0 );
	    fdQueryGroup.right = new FormAttachment( 100, 0 );
	    fdQueryGroup.top = new FormAttachment( gConnect, margin );
	    gQuery.setLayoutData( fdQueryGroup );

	    gQuery.setTabList( new Control[] {wlQuDateType,wQuStartDate, wQuEndDate, wQuDimensions, wQuMetrics, wQuFilters, wQuSort ,wQuSaveQuery,wQuLoadQuery} );


	    // Limit input ...
	    wlLimit = new Label( shell, SWT.RIGHT );
	    wlLimit.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.LimitSize.Label" ) );
	    props.setLook( wlLimit );
	    FormData fdlLimit = new FormData();
	    fdlLimit.left = new FormAttachment( 0, 0 );
	    fdlLimit.right = new FormAttachment( middle, -margin );
	    fdlLimit.bottom = new FormAttachment( 100, -50 );
	    wlLimit.setLayoutData( fdlLimit );
	    wLimit = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	    wLimit.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.LimitSize.Tooltip" ) );
	    props.setLook( wLimit );
	    wLimit.addModifyListener( lsMod );
	    FormData fdLimit = new FormData();
	    fdLimit.left = new FormAttachment( middle, 0 );
	    fdLimit.right = new FormAttachment( 100, 0 );
	    fdLimit.bottom = new FormAttachment( 100, -50 );

	    wLimit.setLayoutData( fdLimit );

	    
	    /*************************************************
	     * // KEY / LOOKUP TABLE
	     *************************************************/

	    wlFields = new Link( shell, SWT.NONE );
	    wlFields.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.Return.Label" ) );
	    props.setLook( wlFields );
	  

	    FormData fdlReturn = new FormData();
	    fdlReturn.left = new FormAttachment( 0, 0 );
	    fdlReturn.top = new FormAttachment( gQuery, margin );
	    wlFields.setLayoutData( fdlReturn );

	    int fieldWidgetCols = 5;
	    int fieldWidgetRows = ( meta.getFeedField() != null ? meta.getFeedField().length : 1 );

	    ColumnInfo[] ciKeys = new ColumnInfo[ fieldWidgetCols ];
	    ciKeys[ 0 ] =
	      new ColumnInfo(
	        BaseMessages.getString( PKG, "GoogleAdExchangeDialog.ColumnInfo.FeedFieldType" ),
	        ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] {
	        	GoogleAdExchangeInputStepMeta.FIELD_TYPE_DIMENSION,GoogleAdExchangeInputStepMeta.FIELD_TYPE_METRIC, GoogleAdExchangeInputStepMeta.FIELD_TYPE_METRIC_CURRENCY,
	        	  },
	        true );
	    ciKeys[ 1 ] =
	      new ColumnInfo(
	        BaseMessages.getString( PKG, "GoogleAdExchangeDialog.ColumnInfo.FeedField" ),
	        ColumnInfo.COLUMN_TYPE_TEXT, false, false );
	    ciKeys[ 1 ].setUsingVariables( true );
	    ciKeys[ 2 ] =
	      new ColumnInfo(
	        BaseMessages.getString( PKG, "GoogleAdExchangeDialog.ColumnInfo.RenameTo" ),
	        ColumnInfo.COLUMN_TYPE_TEXT, false, false );
	    ciKeys[ 3 ] =
	      new ColumnInfo(
	        BaseMessages.getString( PKG, "GoogleAdExchangeDialog.ColumnInfo.Type" ), ColumnInfo.COLUMN_TYPE_CCOMBO,
	        ValueMeta.getTypes() );
	    ciKeys[ 4 ] =
	      new ColumnInfo(
	        BaseMessages.getString( PKG, "GoogleAdExchangeDialog.ColumnInfo.Format" ),
	        ColumnInfo.COLUMN_TYPE_FORMAT, 4 );

	    wFields =
	      new TableView(
	        transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, ciKeys,
	        fieldWidgetRows, lsMod, props );

	    FormData fdReturn = new FormData();
	    
	    fdReturn.left = new FormAttachment( 0, 0 );
	    fdReturn.top = new FormAttachment( wlFields, margin );
	    fdReturn.right = new FormAttachment( 100, 0 );
	    fdReturn.bottom = new FormAttachment( wLimit, -margin );
	    
	    wFields.setLayoutData( fdReturn );

	   
	    /*************************************************
	     * // OK AND CANCEL BUTTONS
	     *************************************************/

	    wOK = new Button( shell, SWT.PUSH );
	    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
	    wCancel = new Button( shell, SWT.PUSH );
	    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

	    wGet = new Button( shell, SWT.PUSH );
	    wGet.setText( BaseMessages.getString( PKG, "System.Button.GetFields" ) );

	    wGet.addListener( SWT.Selection, new Listener() {
	        @Override
	        public void handleEvent( Event e ) {
	        	transMeta.activateParameters();
	        	if(transMeta.environmentSubstitute(authPath.getText())   == null || transMeta.environmentSubstitute(authPath.getText()).length()<1)
	      		{
	      			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
	      			mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.NotSet.DialogTitle" ) );
	      			mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AuthenticationTokenPathNotSet.DialogMessage" ) );
	      			mb.open();
	      			return;
	      		}
	      		if(transMeta.environmentSubstitute(keyFilename.getText()) == null || transMeta.environmentSubstitute(keyFilename.getText()).length()<1)
	      		{
	      			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
	      			mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.clientSecretNotSet.DialogTitle" ) );
	      			mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.clientSecretNotSet.DialogMessage" ) );
	      			mb.open();
	      			return;
	      		}
	      		if(wAccountId.getText() == null || wAccountId.getText().length()<1 || transMeta.environmentSubstitute(wAccountId.getText()).length() <1)
	      		{
	      			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
	      			mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AccountIdNotSet.DialogTitle" ) );
	      			mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AccountIdNotSet.DialogMessage" ) );
	      			mb.open();
	      			return;
	      		}	      		
	      		
	      		createServiceApi( meta );
	      		if ( gAPI.getDimensions() == null || gAPI.getMetrics() == null ||  gAPI.getDimensions().length <1  || gAPI.getMetrics().length <1 ) {

	              MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
	              mb.setText( BaseMessages.getString( PKG, "GoogleAdExchange.Error.InvalidField.title" ) );
	              mb.setMessage(BaseMessages.getString( PKG, "GoogleAdExchange.Error.InvalidField.message" ) );
	              mb.open();

	              return;
	            }
	        	
    			IRunnableWithProgress op = new IRunnableWithProgress() {
    		      public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
    		        try {
    		        	monitor.beginTask(BaseMessages.getString(PKG, "GoogleAdExchangeDialog.GetFields.TaskProgress"), 100);
    		        	
    		        	int i = 0;
    		        	java.util.List<Headers> rptHeaders =gAPI.getReportHeader();
						wFields.table.setItemCount( rptHeaders.size());
						for (Headers header : rptHeaders) {
						
							String name = header.getName();
							TableItem item = wFields.table.getItem( i );
							  
							if(header.getType().equalsIgnoreCase( GoogleAdExchangeInputStepMeta.FIELD_TYPE_DIMENSION ))
							{
							item.setText( 1, GoogleAdExchangeInputStepMeta.FIELD_TYPE_DIMENSION );
							item.setText( 4, ValueMetaBase.getTypeDesc( ValueMeta.TYPE_STRING ) );
							}
							else if(header.getType().equalsIgnoreCase( "METRIC_CURRENCY" ))
							{
								item.setText( 1, GoogleAdExchangeInputStepMeta.FIELD_TYPE_METRIC_CURRENCY );
								item.setText( 4, ValueMetaBase.getTypeDesc( ValueMeta.TYPE_BIGNUMBER ) );
							}
							else
							{
								item.setText( 1, GoogleAdExchangeInputStepMeta.FIELD_TYPE_METRIC );
								item.setText( 4, ValueMetaBase.getTypeDesc( ValueMeta.TYPE_BIGNUMBER ) );
							}
							item.setText( 2, name );
							item.setText( 3, name );  	              
							item.setText( 5, "" );
							i++;
						}
	            }
	            catch (Exception ex)
	            {
	            	MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
					mb.setText( BaseMessages.getString( PKG,  "GoogleAdExchange.Error.title") );
					mb.setMessage(ex.getMessage() );					
					mb.open();	
	            }
	            wFields.removeEmptyRows();
	            wFields.setRowNums();
	            wFields.optWidth( true );
	            meta.setChanged();
    		          
    		       
    		      }
    		    };
    		
    			ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
    			
    			try {
    				pmd.run(false, false, op);
    			}
    			catch (Exception ex)
	            {
	            	MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
					mb.setText( BaseMessages.getString( PKG,  "GoogleAdExchange.Error.title") );
					mb.setMessage(ex.getMessage() );					
					mb.open();	
	            }
						
	        }	
	    }			
	         
	      
	    );

	    wPreview = new Button( shell, SWT.PUSH );

	    wPreview.setText( BaseMessages.getString( PKG, "System.Button.Preview" ) );
	    wPreview.addListener( SWT.Selection, new Listener() {
	        @Override
	        public void handleEvent( Event ev ) {
	          preview();
	        }
	      }
	    );
	    BaseStepDialog.positionBottomButtons( shell, new Button[] { wOK, wGet, wPreview, wCancel }, margin, wLimit );


	    // Add listeners
	    lsCancel = new Listener() {
	      public void handleEvent( Event e ) {
	        cancel();
	      }
	    };
	    lsOK = new Listener() {
	      public void handleEvent( Event e ) {
	        ok();
	      }
	    };

	    wCancel.addListener( SWT.Selection, lsCancel );
	    wOK.addListener( SWT.Selection, lsOK );

	    /*************************************************
	     * // DEFAULT ACTION LISTENERS
	     *************************************************/

	    lsDef = new SelectionAdapter() {
	      public void widgetDefaultSelected( SelectionEvent e ) {
	        ok();
	      }
	    };

	    wStepname.addSelectionListener( lsDef );
	    
	    wQuStartDate.addSelectionListener( lsDef );
	    wQuEndDate.addSelectionListener( lsDef );
	    wQuDimensions.addSelectionListener( lsDef );
	    wQuMetrics.addSelectionListener( lsDef );
	    wQuFilters.addSelectionListener( lsDef );
	    
	    

	    // Detect X or ALT-F4 or something that kills this window...
	    shell.addShellListener(
	      new ShellAdapter() {
	        public void shellClosed( ShellEvent e ) {
	          cancel();
	        }
	      }
	    );

	    fileChooser.addSelectionListener(
	      new SelectionAdapter() {
	        public void widgetSelected( SelectionEvent e ) {
	        	transMeta.activateParameters();
	          if(transMeta.environmentSubstitute(authPath.getText()) == null || transMeta.environmentSubstitute(authPath.getText()).length()<1)
	          {
	        	 MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
				 mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AuthenticationTokenPathNotSet.DialogTitle" ) );
			     mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AuthenticationTokenPathNotSet.DialogMessage" ) );
			     mb.open();
			  }
	          else
	          {
	        	  FileDialog dialog = new FileDialog( shell, SWT.OPEN );
		          if ( keyFilename.getText() != null ) {
		            String existingPath = transMeta.environmentSubstitute( keyFilename.getText() );
		            dialog.setFileName( existingPath );
		          }

		          dialog.setFilterExtensions( new String[] { "*.json", "*" } );
		          dialog.setFilterNames( new String[] {
		            BaseMessages.getString( PKG, "GoogleAdExchangeDialog.SecretFileChooser.json" ),
		            BaseMessages.getString( PKG, "GoogleAdExchangeDialog.SecretFileChooser.All" )
		          } );

		          if ( dialog.open() != null ) {
		            String keyPath = dialog.getFilterPath() + System.getProperty( "file.separator" ) + dialog.getFileName();
		            keyFilename.setText( keyPath );
		            fillAccountID();
		            wAccountId.select(0);
		          }  
	          }
	          
	        }
	      }
	    );
	    
	    wQuSaveQuery.addSelectionListener(
	  	      new SelectionAdapter() {
	  	        public void widgetSelected( SelectionEvent e ) {
	  	          FileDialog dialog = new FileDialog( shell, SWT.SAVE );
	  	          dialog.setFilterExtensions( new String[] { "*.config" } );
	  	          dialog.setFilterNames( new String[] {
	  	            BaseMessages.getString( PKG, "GoogleAdExchangeDialog.FileChooser.queryConfig" ),
	  	          } );

	  	          if ( dialog.open() != null ) {
	  	            String filePath = dialog.getFilterPath() + System.getProperty( "file.separator" ) + dialog.getFileName();
	  	            saveQuery(filePath);
	  	          }
	  	        }
	  	      }
	  	    );
	    
	    wQuLoadQuery.addSelectionListener(
    		new SelectionAdapter() {
	  	        public void widgetSelected( SelectionEvent e ) {
	  	          FileDialog dialog = new FileDialog( shell, SWT.OPEN );
	  	          dialog.setFilterExtensions( new String[] { "*.config" } );
	  	          dialog.setFilterNames( new String[] {
	  	            BaseMessages.getString( PKG, "GoogleAdExchangeDialog.FileChooser.queryConfig" ),
	  	          } );

	  	          if ( dialog.open() != null ) {
	  	            String filePath = dialog.getFilterPath() + System.getProperty( "file.separator" ) + dialog.getFileName();
	  	            loadQuery(filePath);
	  	          }
	  	        }
	  	      }
	  	    );
	    // Set the shell size, based upon previous time...
	    setSize();


	    getData();
	    
	    
	    	
	    
	    meta.setChanged( backupChanged );
	    wStepname.setFocus();

	    shell.setTabList( new Control[] { wStepname, gConnect, gQuery, wFields } );
	    shell.open();
	   
	     
	    	    
	    while ( !shell.isDisposed() ) {
	      if ( !display.readAndDispatch() ) {
	        display.sleep();
	      }
	    }

	    return stepname;

	}

	private void getInfo( GoogleAdExchangeInputStepMeta meta ) {

	    stepname = wStepname.getText(); 	 
	    meta.setAppName( wAppName.getText().trim() );
	    meta.setOAuthKeyFile( keyFilename.getText().trim() );
	    meta.setAccountID(wAccountId.getText().trim());
	    meta.setAuthTokenPath(authPath.getText());
	    
	   
	    meta.setDateType( wQuDateType.getText().trim() );
	    
	    
	    meta.setStartDate( wQuStartDate.getText().trim() );
	    meta.setEndDate( wQuEndDate.getText().trim() );

	    meta.setDimensions( wQuDimensions.getText().trim() );
	    
	    
	    meta.setMetrics( wQuMetrics.getText().trim() );
	    meta.setFilters( wQuFilters.getText().trim() );
	    meta.setSort( wQuSort.getText().trim() );

	    
	    int nrFields = wFields.nrNonEmpty();

	    meta.allocate( nrFields );

	    for ( int i = 0; i < nrFields; i++ ) {
	      TableItem item = wFields.getNonEmpty( i );
	      meta.getFeedFieldType()[ i ] = item.getText( 1 ).trim();
	      meta.getFeedField()[ i ] = item.getText( 2 ).trim();
	      meta.getOutputField()[ i ] = item.getText( 3 ).trim();

	      meta.getOutputType()[ i ] = ValueMeta.getType( item.getText( 4 ).trim() );
	      meta.getConversionMask()[ i ] = item.getText( 5 ).trim();

	      // fix unknowns
	      if ( meta.getOutputType()[ i ] < 0 ) {
	        meta.getOutputType()[ i ] = ValueMetaInterface.TYPE_STRING;
	      }
	    }
	    
	    meta.setRowLimit( Const.toInt( wLimit.getText(), 0 ) );
	    
	    
	  }
  private void preview() {
	    // Create the XML input step
	  transMeta.activateParameters();
	  	GoogleAdExchangeInputStepMeta oneMeta = new GoogleAdExchangeInputStepMeta();
	  	if(transMeta.environmentSubstitute(authPath.getText())   == null || transMeta.environmentSubstitute(authPath.getText()).length()<1)
  		{
  			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
  			mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.NotSet.DialogTitle" ) );
  			mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AuthenticationTokenPathNotSet.DialogMessage" ) );
  			mb.open();
  			return;
  		}
  		if(transMeta.environmentSubstitute(keyFilename.getText()) == null || transMeta.environmentSubstitute(keyFilename.getText()).length()<1)
  		{
  			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
  			mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.clientSecretNotSet.DialogTitle" ) );
  			mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.clientSecretNotSet.DialogMessage" ) );
  			mb.open();
  			return;
  		}
  		if(wAccountId.getText() == null || wAccountId.getText().length()<1|| transMeta.environmentSubstitute(wAccountId.getText()).length() <1)
  		{
  			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
  			mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AccountIdNotSet.DialogTitle" ) );
  			mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AccountIdNotSet.DialogMessage" ) );
  			mb.open();
  			return;
  		}	      		
  		
	    getInfo( oneMeta );
	    createServiceApi( oneMeta );
	    TransMeta previewMeta =
	      TransPreviewFactory.generatePreviewTransformation( transMeta, oneMeta, wStepname.getText() );

	    EnterNumberDialog numberDialog =
	      new EnterNumberDialog( shell, props.getDefaultPreviewSize(),
	        BaseMessages.getString( PKG, "GoogleAdExchangeDialog.PreviewSize.DialogTitle" ),
	        BaseMessages.getString( PKG, "GoogleAdExchangeDialog.PreviewSize.DialogMessage" ) );
	    int previewSize = numberDialog.open();
	    if ( previewSize > 0 ) {
	      TransPreviewProgressDialog progressDialog =
	        new TransPreviewProgressDialog(
	          shell, previewMeta, new String[] { wStepname.getText() }, new int[] { previewSize } );
	      progressDialog.open();

	      Trans trans = progressDialog.getTrans();
	      String loggingText = progressDialog.getLoggingText();

	      if ( !progressDialog.isCancelled() ) {
	        if ( trans.getResult() != null && trans.getResult().getNrErrors() > 0 ) {
	          EnterTextDialog etd =
	            new EnterTextDialog(
	              shell, BaseMessages.getString( PKG, "System.Dialog.PreviewError.Title" ), BaseMessages
	              .getString( PKG, "System.Dialog.PreviewError.Message" ), loggingText, true );
	          etd.setReadOnly();
	          etd.open();
	        }
	      }

	      PreviewRowsDialog prd =
	        new PreviewRowsDialog(
	          shell, transMeta, SWT.NONE, wStepname.getText(), progressDialog.getPreviewRowsMeta(
	            wStepname.getText() ), progressDialog.getPreviewRows( wStepname.getText() ), loggingText );
	      prd.open();
	    }
	  }
  
  	public void getData() {

		if ( meta.getAppName() != null ) {
		  wAppName.setText( meta.getAppName() );
		}

	    keyFilename.setText( Const.NVL( meta.getOAuthKeyFile(), "" ) );
	    
	    if(meta.getAccountID() != null)
	    {
	    	wAccountId.add(meta.getAccountID());
	    	wAccountId.select(0);
	    }
	    if(meta.getAuthTokenPath() != null)
	    {
	    	authPath.setText(meta.getAuthTokenPath());
	    }
	    
	  
	    
		
		
	    if ( meta.getDateType() != null ) {
	      wQuDateType.setText( meta.getDateType() );
	    }

	    
	    if ( meta.getStartDate() != null ) {
	      wQuStartDate.setText( meta.getStartDate() );
	    }

	    if ( meta.getEndDate() != null ) {
	      wQuEndDate.setText( meta.getEndDate() );
	    }

	    if ( meta.getDimensions() != null ) {
	      wQuDimensions.setText( meta.getDimensions() );
	    }
	    
	    
	    if ( meta.getMetrics() != null ) {
	      wQuMetrics.setText( meta.getMetrics() );
	    }

	    if ( meta.getFilters() != null ) {
	      wQuFilters.setText( meta.getFilters() );
	    }
	    if ( meta.getSort() != null ) {
		      wQuSort.setText( meta.getSort() );
		    }

	    

	    if ( meta.getFeedField() != null ) {

	      for ( int i = 0; i < meta.getFeedField().length; i++ ) {

	        TableItem item = wFields.table.getItem( i );

	        if ( meta.getFeedFieldType()[ i ] != null ) {
	          item.setText( 1, meta.getFeedFieldType()[ i ] );
	        }

	        if ( meta.getFeedField()[ i ] != null ) {
	          item.setText( 2, meta.getFeedField()[ i ] );
	        }

	        if ( meta.getOutputField()[ i ] != null ) {
	          item.setText( 3, meta.getOutputField()[ i ] );
	        }

	        item.setText( 4, ValueMeta.getTypeDesc( meta.getOutputType()[ i ] ) );

	        if ( meta.getConversionMask()[ i ] != null ) {
	          item.setText( 5, meta.getConversionMask()[ i ] );
	        }

	      }
	    }
	    transMeta.activateParameters();
	    if(transMeta.environmentSubstitute(meta.getDateType()).equalsIgnoreCase(DateRangeType.CUSTOM_DATE))
		{
			wQuStartDate.setEnabled(true);
			wQuEndDate.setEnabled(true);
		}
		else
		{
			wQuStartDate.setEnabled(false);
			wQuEndDate.setEnabled(false);
		}
	    wFields.setRowNums();
	    wFields.optWidth( true );

	    wLimit.setText( meta.getRowLimit() + "" );

	    
	    wStepname.selectAll();
	    wStepname.setFocus();
  	}

  	private void cancel() {
  		stepname = null;
  		meta.setChanged( backupChanged );
  		dispose();
  	}


  	private void ok() {
  		
  		getInfo( meta );
  		dispose();
  	}

  	private void createOauthServiceCredentialsControls() {
  		
  		
		//Auth File 
		
		browseAuthPath = new Button( gConnect, SWT.PUSH | SWT.CENTER );
		browseAuthPath.setText( BaseMessages.getString( PKG, ( "System.Button.Browse" ) ) );
		props.setLook( browseAuthPath );
		
		FormData fdbBrowseAuthPath = new FormData();
		fdbBrowseAuthPath.right = new FormAttachment( 100, 0 );
		fdbBrowseAuthPath.top = new FormAttachment( wAppName, margin );
		browseAuthPath.setLayoutData( fdbBrowseAuthPath );
		
		
		browseAuthPath.addSelectionListener(
			      new SelectionAdapter() {
			        public void widgetSelected( SelectionEvent e ) {
			          DirectoryDialog dlg = new DirectoryDialog(shell);
			          transMeta.activateParameters();
			          if ( authPath.getText() != null ) {
			            String existingPath = transMeta.environmentSubstitute( authPath.getText() );
			            dlg.setFilterPath(existingPath);
			          }
			          dlg.setText(BaseMessages.getString( PKG, ( "GoogleAdExchangeDialog.AuthPath.Label" ) ));
			          dlg.setMessage("Select a directory");
			          
			          String dir = dlg.open();
			          
			          	if (dir != null) {
			          		authPath.setText(dir);
			          }
			        }
			      }
			    );
		Label wlAuthTokenPath = new Label( gConnect, SWT.RIGHT );
		wlAuthTokenPath.setText( BaseMessages.getString( PKG, ( "GoogleAdExchangeDialog.AuthPath.Label" ) ) );
		props.setLook( wlAuthTokenPath );
		FormData fdlAuthTokenPath = new FormData();
		fdlAuthTokenPath.top = new FormAttachment( wAppName, margin );
		fdlAuthTokenPath.left = new FormAttachment( 0, 0 );
		fdlAuthTokenPath.right = new FormAttachment( middle, -margin );
		wlAuthTokenPath.setLayoutData( fdlAuthTokenPath );
		
		authPath = new TextVar( transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
		authPath.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AuthPath.Tooltip" ) );
		authPath.addModifyListener( lsMod );
	    props.setLook( authPath );
	
	    FormData fdauthPath = new FormData();
	    fdauthPath.top = new FormAttachment( wAppName, margin );
	    fdauthPath.left = new FormAttachment( middle, 0 );
	    fdauthPath.right = new FormAttachment( browseAuthPath, -margin );
	    authPath.setLayoutData( fdauthPath );

	    
	    //ClientSecretPath
		fileChooser = new Button( gConnect, SWT.PUSH | SWT.CENTER );
		fileChooser.setText( BaseMessages.getString( PKG, ( "System.Button.Browse" ) ) );
		props.setLook( fileChooser );
		
		FormData fdbFilename = new FormData();
		fdbFilename.right = new FormAttachment( 100, 0 );
		fdbFilename.top = new FormAttachment( authPath, margin );
		fileChooser.setLayoutData( fdbFilename );
		
		Label wlFilename = new Label( gConnect, SWT.RIGHT );
		wlFilename.setText( BaseMessages.getString( PKG, ( "GoogleAdExchangeDialog.KeyFile.Label" ) ) );
		props.setLook( wlFilename );
		FormData fdlFilename = new FormData();
		fdlFilename.top = new FormAttachment( authPath, margin );
		fdlFilename.left = new FormAttachment( 0, 0 );
		fdlFilename.right = new FormAttachment( middle, -margin );
		wlFilename.setLayoutData( fdlFilename );
		
		keyFilename = new TextVar( transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
		keyFilename.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.KeyFilename.Tooltip" ) );
	    keyFilename.addModifyListener( lsMod );
	    props.setLook( keyFilename );
	
	    FormData fdFilename = new FormData();
	    fdFilename.top = new FormAttachment( authPath, margin );
	    fdFilename.left = new FormAttachment( middle, 0 );
	    fdFilename.right = new FormAttachment( fileChooser, -margin );
	    keyFilename.setLayoutData( fdFilename );

	    
	    
	    wGetAcccountId = new Button( gConnect, SWT.PUSH | SWT.CENTER );
	    wGetAcccountId.setText( BaseMessages.getString( PKG, ( "GoogleAdExchangeDialog.AccountID.Button" ) ) );
		props.setLook( wGetAcccountId );
		
		FormData fdbGetAccountID = new FormData();
		fdbGetAccountID.right = new FormAttachment( 100, 0 );
		fdbGetAccountID.top = new FormAttachment( fileChooser, margin );
		wGetAcccountId.setLayoutData( fdbGetAccountID );
		wGetAcccountId.addListener( SWT.Selection, new Listener() {
	        @Override
	        public void handleEvent( Event e ) {
	        	fillAccountID();
	        	
	        } 
	      }
	    );

		
	    
	    wlAccountId = new Label( gConnect, SWT.RIGHT );
	    wlAccountId.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AccountID.Label" ) );
		props.setLook( wlAccountId );
		
		FormData fdlAccountId = new FormData();
		fdlAccountId.left = new FormAttachment( 0, 0 );
		fdlAccountId.top = new FormAttachment( fileChooser, margin );
		fdlAccountId.right = new FormAttachment( middle, -margin );
		
		wlAccountId.setLayoutData( fdlAccountId );
		wAccountId = new ComboVar( transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
		wAccountId.setToolTipText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AccountID.Tooltip" ) );
		props.setLook( wAccountId );
		
		wAccountId.addModifyListener( lsMod );
		FormData fdAccountId = new FormData();
		fdAccountId.left = new FormAttachment( middle, 0 );
		fdAccountId.top = new FormAttachment( fileChooser, margin );
		fdAccountId.right = new FormAttachment( wGetAcccountId, -margin );
		wAccountId.setLayoutData( fdAccountId );
		

  	}

  	private void createServiceApi( GoogleAdExchangeInputStepMeta meta ) {
  			transMeta.activateParameters();
  			
  			getInfo( meta );
		      
	      	try
			{
		    	gAPI = new GoogleAdExchangeAPI( transMeta.environmentSubstitute(meta.getOAuthKeyFile()),transMeta.environmentSubstitute(meta.getAppName()),transMeta.environmentSubstitute(meta.getAuthTokenPath()));
		    	
		    	
			}
	      	catch(Exception e)
			{
			     e.printStackTrace();
			     logError(BaseMessages.getString( PKG, "GoogleAdExchangeStep.AuthenticationFailure.Message" ) + e.getMessage() );
			}
		    try {
				if (log.isBasic()) logBasic(BaseMessages.getString( PKG, "GoogleAdExchangeStep.AuthenticationSuccess.Message" ) );
				gAPI.setAccountId(transMeta.environmentSubstitute(meta.getAccountID()));
				
				
				gAPI.setDateRange(transMeta.environmentSubstitute(meta.getDateType()));
				if(transMeta.environmentSubstitute(gAPI.getDateRange()).equalsIgnoreCase(DateRangeType.CUSTOM_DATE))
				{
					gAPI.setStartDate(transMeta.environmentSubstitute(meta.getStartDate()));
					gAPI.setEndDate(transMeta.environmentSubstitute(meta.getEndDate()));
				}
				
				
			
				gAPI.setDimensions(transMeta.environmentSubstitute(meta.getDimensions()));
				gAPI.setMetrics(transMeta.environmentSubstitute(meta.getMetrics()));
				gAPI.setFilters(transMeta.environmentSubstitute(meta.getFilters()));
				gAPI.setSort(transMeta.environmentSubstitute(meta.getSort()));
				
				gAPI.setRowLimit(meta.getRowLimit());
			} catch ( Exception e ) {
		    	
		    	MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
		    	mb.setText( BaseMessages.getString( PKG, "GoogleAdExchange.Error.title" ) );
			    mb.setMessage( e.getMessage() );
			    mb.open();
			    logError(  e.getMessage() );
		    }
  	}
  	private void loadQuery(String filePath)
  	{
  		
  		Properties prop = new Properties();
  		InputStream input = null;

  		try {

  			input = new FileInputStream(filePath);

  			prop.load(input);

  			
  			
  			if(prop.getProperty("dateRange") != null )
  				wQuDateType.setText(prop.getProperty("dateRange"));
  			
  			if(prop.getProperty("startDate") != null )
  				wQuStartDate.setText(prop.getProperty("startDate"));
  			
  			if(prop.getProperty("endDate") != null )
  				wQuEndDate.setText(prop.getProperty("endDate"));
  			
  			if(prop.getProperty("dimensions") != null )
  				wQuDimensions.setText(prop.getProperty("dimensions"));
  			
  			
  			if(prop.getProperty("metrics") != null )
  				wQuMetrics.setText(prop.getProperty("metrics"));
  			
  			if(prop.getProperty("filter") != null )
  				wQuFilters.setText(prop.getProperty("filter"));
  			
  			if(prop.getProperty("sort") != null )
  				wQuSort.setText(prop.getProperty("sort"));

  			if(prop.getProperty("limit") != null )
  				wLimit.setText(prop.getProperty("limit"));

  			transMeta.activateParameters();
  			if(transMeta.environmentSubstitute(wQuDateType.getText()).equalsIgnoreCase(DateRangeType.CUSTOM_DATE))
  			{
  				wQuStartDate.setEnabled(true);
  				wQuEndDate.setEnabled(true);
  			}
  			else
  			{
  				wQuStartDate.setEnabled(false);
  				wQuEndDate.setEnabled(false);
  			}
  			
  			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_INFORMATION );
	    	mb.setText( BaseMessages.getString( PKG, "GoogleAdExchange.loadConfig.title" ) );
		    mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchange.loadConfig.message" )  );
		    mb.open();
		    
		    
		    
  		} catch (Exception e) {
  			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
	    	mb.setText( BaseMessages.getString( PKG, "GoogleAdExchange.Error.title" ) );
		    mb.setMessage( e.getMessage() );
		    mb.open();
		    logError(  e.getMessage() );
  		} finally {
  			if (input != null) {
  				try {
  					input.close();
  				} catch (IOException e) {
  					MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
  			    	mb.setText( BaseMessages.getString( PKG, "GoogleAdExchange.Error.title" ) );
  				    mb.setMessage( e.getMessage() );
  				    mb.open();
  				    logError(  e.getMessage() );
  				}
  			}
  		}

  	}
  	private void saveQuery(String filePath)
  	{

		Properties prop = new Properties();
		OutputStream output = null;
  		try {

			output = new FileOutputStream(filePath);
	
			transMeta.activateParameters();
			prop.setProperty("dateRange", transMeta.environmentSubstitute(wQuDateType.getText()));
			prop.setProperty("startDate", 	transMeta.environmentSubstitute(wQuStartDate.getText()));
			prop.setProperty("endDate", transMeta.environmentSubstitute(wQuEndDate.getText()));
			prop.setProperty("dimensions", transMeta.environmentSubstitute(wQuDimensions.getText()));
			prop.setProperty("metrics", transMeta.environmentSubstitute(wQuMetrics.getText()));
			prop.setProperty("filter", transMeta.environmentSubstitute(wQuFilters.getText()));
			prop.setProperty("sort", transMeta.environmentSubstitute(wQuSort.getText()));
			prop.setProperty("limit", wLimit.getText());
			prop.store(output, null);
			
			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_INFORMATION );
	    	mb.setText( BaseMessages.getString( PKG, "GoogleAdExchange.saveConfig.title" ) );
		    mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchange.saveConfig.message" )  );
		    mb.open();

  		} catch (Exception io) {
  			
  			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
	    	mb.setText( BaseMessages.getString( PKG, "GoogleAdExchange.Error.title" ) );
		    mb.setMessage( io.getMessage() );
		    mb.open();
		    logError(  io.getMessage() );
  			
  		} finally {
  			if (output != null) {
  				try {
  					output.close();
  				} catch (IOException e) {
  					
  					MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
  			    	mb.setText( BaseMessages.getString( PKG, "GoogleAdExchange.Error.title" ) );
  				    mb.setMessage( e.getMessage() );
  				    mb.open();
  				    logError(  e.getMessage() );
  				}
  			}

  		}
  	}
  	private void fillAccountID()
  	{
  		wAccountId.removeAll();  
      	try
		{
      		transMeta.activateParameters();
      		if(transMeta.environmentSubstitute(authPath.getText())   == null || transMeta.environmentSubstitute(authPath.getText()).length()<1)
      		{
      			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      			mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.NotSet.DialogTitle" ) );
      			mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AuthenticationTokenPathNotSet.DialogMessage" ) );
      			mb.open();
      			return;
      		}
      		if(transMeta.environmentSubstitute(keyFilename.getText()) == null || transMeta.environmentSubstitute(keyFilename.getText()).length()<1)
      		{
      			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      			mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.clientSecretNotSet.DialogTitle" ) );
      			mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.clientSecretNotSet.DialogMessage" ) );
      			mb.open();
      			return;
      		}
      		gAPI = new GoogleAdExchangeAPI( transMeta.environmentSubstitute(keyFilename.getText()),transMeta.environmentSubstitute(wAppName.getText()),transMeta.environmentSubstitute(authPath.getText()));
	    	
	    	
		}
      	catch(Exception e)
		{
			 MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
			 mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AuthenticationFailure.DialogTitle" ) );
		     mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AuthenticationFailure.DialogMessage" ) );
		     mb.open();
		     e.printStackTrace();
		     logError(BaseMessages.getString( PKG, "GoogleAdExchangeStep.AuthenticationFailure.Message" ) + e.getMessage() );
		     return;
		}
        try
		{
      		java.util.List<String> accIds =gAPI.getAllAccountId();
      		for(String val : accIds)
      			wAccountId.add(val);
            
      		if(meta.getAccountID()!=null)
      			wAccountId.setText(meta.getAccountID());
      		else if(accIds.size() > 0 )
            	wAccountId.select(0);
	    	
		}
      	catch(Exception ex)
		{
			 MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
			 mb.setText( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AccountRetiriveFailure.DialogTitle" ) );
		     mb.setMessage( BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AccountRetiriveFailure.DialogMessage" ) );
		     mb.open();
		     ex.printStackTrace();
		     logError(ex.getMessage());
		     
		}
  	}
  	
  	
}