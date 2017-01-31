package org.pentaho.pdi.steps.googleadexchangeInput.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.pdi.steps.googleadexchangeInput.GoogleAdExchangeInputStepMeta;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.adexchangeseller.AdExchangeSeller;
import com.google.api.services.adexchangeseller.AdExchangeSeller.Accounts.Reports.Generate;
import com.google.api.services.adexchangeseller.AdExchangeSellerScopes;
import com.google.api.services.adexchangeseller.model.Account;
import com.google.api.services.adexchangeseller.model.Accounts;
import com.google.api.services.adexchangeseller.model.Report;
import com.google.api.services.adexchangeseller.model.Report.Headers;
import com.google.api.services.adexchangeseller.model.SavedReport;
import com.google.api.services.adexchangeseller.model.SavedReports;



public class GoogleAdExchangeAPI {

	
	private String CLIENT_SECRET_FILE= "";
	private String applicationName ;
	private AdExchangeSeller adExchangeSeller;
	private java.io.File DATA_STORE_DIR ;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	private  final int MAX_LIST_PAGE_SIZE = 50;
	public static long MAX_READ_RECORD = 5000;

	private	FileDataStoreFactory dataStoreFactory;
	private HttpTransport httpTransport;

	private String accountId;
	private String authTokenPath;
	private String dimensions [];
	private String metrics [];
	private String filters  [];
	private String sort [];	
	private String startDate,endDate;	
	private String dateRange;
	
	private int rowLimit;
	  
	private static Class<?> PKG = GoogleAdExchangeInputStepMeta.class;

	/*public static void main(String [] args)throws Exception
	{
		
		
	}*/
	

	public GoogleAdExchangeAPI(String clientIDSecretFile,String appName,String authPath)throws Exception
	{
			
	      	CLIENT_SECRET_FILE = clientIDSecretFile;
			applicationName =appName;
			authTokenPath= authPath;
			dateRange=DateRangeType.CUSTOM_DATE;
			
			createSession();
	}
	private void createSession()throws Exception
	{	
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
			        new InputStreamReader(new java.io.FileInputStream(new java.io.File(CLIENT_SECRET_FILE))));
	    
	    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
	        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
	     
	    	throw new Exception(BaseMessages.getString( PKG, "GoogleAdExchangeAPI.InvalidClientSecrentFile.message" ));
	    }
		
	    httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	    DATA_STORE_DIR = new java.io.File(authTokenPath +"/"+ clientSecrets.getDetails().getClientId());
      	dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      	
      	
	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
		    httpTransport, JSON_FACTORY, clientSecrets,
		    Collections.singleton(AdExchangeSellerScopes.ADEXCHANGE_SELLER_READONLY)).setDataStoreFactory(
		    dataStoreFactory).build();
			    // authorize

	 
	    
	    
	    Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	    							
	    adExchangeSeller = new AdExchangeSeller.Builder(
	            new NetHttpTransport(), JSON_FACTORY, setHttpTimeout(credential)).setApplicationName(applicationName)
	            .build();
	    
	    
	}
	   private HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
			  return new HttpRequestInitializer() {
			    @Override
			    public void initialize(HttpRequest httpRequest) throws IOException {
			      requestInitializer.initialize(httpRequest);
			      httpRequest.setConnectTimeout(3 * 60000);  // 3 minutes connect timeout
			      httpRequest.setReadTimeout(60 * 60000);  // 3 minutes read timeout
			    }
			  };
	   }
	public List<String> getAllAccountId()throws Exception
	{
		List<String> accIds=  new ArrayList<String>();
		String pageToken = null;
		Accounts accounts = null;
		do {
		  accounts = adExchangeSeller.accounts().list()
			  .setMaxResults(MAX_LIST_PAGE_SIZE)
		      .setPageToken(pageToken)
		      .execute();
		
		  if (accounts.getItems() != null && !accounts.getItems().isEmpty()) {
		    for (Account account : accounts.getItems()) {
		    	accIds.add(account.getId());
		    }
		  } 
		  pageToken = accounts.getNextPageToken();
		} while (pageToken != null);
		
		return accIds;
		
	}
	
	public List<Headers> getReportHeader()throws Exception
	{
		Report response;
		Generate request = getReportGenerater();
		
		request.setStartDate(new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date() ));
		request.setEndDate(new SimpleDateFormat( "yyyy-MM-dd" ).format( new Date() ));
		
		request.setMaxResults(Long.valueOf(1));
		response = request.execute();
		
		
		// Check if the results fit the requirements for this method.
	    if (response.getHeaders() == null && !response.getHeaders().isEmpty()) {
	      throw new RuntimeException("No headers defined in report results.");
	    }

	    if (response.getHeaders().size() < 2 ||
	        !response.getHeaders().get(0).getType().equals("DIMENSION")) {
	      throw new RuntimeException("Insufficient dimensions and metrics defined.");
	    }
	    
		return response.getHeaders(); 

	}
	private Generate getReportGenerater()throws Exception
	{
		if(!dateRange.equalsIgnoreCase(DateRangeType.CUSTOM_DATE))
		{
			String val [] = DateRangeType.getStartEndDate(dateRange);
			startDate = val[0];
			endDate = val[1];
		}
		
		Generate request = adExchangeSeller.accounts().reports().generate(accountId, startDate, endDate);

		request.setDimension(Arrays.asList(dimensions));
		request.setMetric(Arrays.asList(metrics));

		request.setFilter(Arrays.asList(filters));
		request.setSort(Arrays.asList(sort));
		
		//request.setUseTimezoneReporting(timeZone);
		
		return request;
	}


	public String validate()
	{
		if(adExchangeSeller == null)
			return BaseMessages.getString( PKG, "GoogleAdExchangeDialog.AuthenticationFailure.DialogMessage" ) ;
		else if(dimensions == null || dimensions.length < 1)
			return BaseMessages.getString( PKG, "GoogleAdExchangeStep.GoogleAdExchangeAPI.InvalidDimension" ) ;
		else if(metrics == null || metrics.length < 1)
			return BaseMessages.getString( PKG, "GoogleAdExchangeStep.GoogleAdExchangeAPI.InvalidMetrics" ) ;
		else if(dateRange == null )
			return BaseMessages.getString( PKG, "GoogleAdExchangeStep.GoogleAdExchangeAPI.InvalidDateRange" ) ;
		else if(dateRange.equalsIgnoreCase(DateRangeType.CUSTOM_DATE))
		{
			if(startDate == null )
				return BaseMessages.getString( PKG, "GoogleAdExchangeStep.GoogleAdExchangeAPI.InvalidStartDate" ) ;
			else if (endDate == null)
				return BaseMessages.getString( PKG, "GoogleAdExchangeStep.GoogleAdExchangeAPI.InvalidEndDate" ) ;
		}
		return BaseMessages.getString( PKG, "GoogleAdExchangeStep.GoogleAdExchangeAPI.Success" ) ;
		
	}
	
	public Report  runRerpots()throws Exception
	{
		String validMsg=validate(); 
		if(!validMsg.equalsIgnoreCase(BaseMessages.getString( PKG, "GoogleAdExchangeStep.GoogleAdExchangeAPI.Success" )))
		{
			throw new KettleException(validMsg);
		}
		
		Generate request = getReportGenerater();
		Report response = request.execute();
		
		return response;
		  
	}
	
	public String[] getDimensions() {
		return dimensions;
	}
	public void setDimensions(String[] dimensions) {
		this.dimensions = dimensions;
	}
	public void setDimensions(String dimensions) {
	
		if(dimensions.length() > 0)
		{
			String dimensionArr[] = dimensions.split(",");
			this.dimensions = new String[dimensionArr.length];
			for(int i=0;i<dimensionArr.length;i++)
			{
				this.dimensions [i] = dimensionArr[i]  ;
			}
		}
		else
		{
			this.dimensions = new String[]{};
		}
		
		
	}
	public String[] getMetrics() {
		return metrics;
	}
	public void setMetrics(String[] metrics) {
		this.metrics = metrics;
	}
	public void setMetrics(String metrics) {
		
		if(metrics.length() > 0)
		{
			String metricsArr[] = metrics.split(",");
			this.metrics = new String[metricsArr.length];
			
			for(int i=0;i<metricsArr.length;i++)
			{
				this.metrics [i] = metricsArr[i]  ;
			}
		}
		else
		{
			this.metrics = new String[]{};
		}
	}
	public String[] getFilters() {
		return filters;
	}
	public void setFilters(String[] filters) {
		this.filters = filters;
	}
	public void setFilters(String filters) {
		
		if(filters!= null && filters.length() > 0)
		{
			//String filtersStr= filters.replaceAll("(?i)or", ","); // OR Condition 
			String filtersArr []= filters.split("&"); // And Condition
			
			this.filters = new String[filtersArr.length];
			for(int i=0;i<filtersArr.length;i++)
			{
				this.filters [i] = filtersArr[i]  ;
			}
		}
		else
		{
			this.filters = new String[]{};
		}
		
	}
	public String[] getSort() {
		return sort;
	}
	public void setSort(String[] sort) {
		this.sort = sort;
	}
	public void setSort(String sort) {
		
		if(sort != null && sort.length() > 0)
		{
			String sortArr[] = sort.split(",");
			this.sort = new String[sortArr.length];
			
			for(int i=0;i<sortArr.length;i++)
			{
				this.sort [i] = sortArr[i]  ;
			}
		}
		else
		{
			this.sort = new String[]{};
		}
	}
	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getDateRange() {
		return dateRange;
	}
	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public int getRowLimit() {
		return rowLimit;
	}
	public void setRowLimit(int rowLimit) {
		this.rowLimit = rowLimit;
	}
	public String getAuthTokenPath() {
		return authTokenPath;
	}
	public void setAuthTokenPath(String authTokenPath) {
		this.authTokenPath = authTokenPath;
	}
	
	
}
