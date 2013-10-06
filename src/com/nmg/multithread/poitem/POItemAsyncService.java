package com.nmg.multithread.poitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.CatalogManager;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.common.ProcessingOptions;
import com.ibm.pim.context.Context;
import com.ibm.pim.utils.Logger;
import com.nmg.multithread.FetchItem; 

public class POItemAsyncService  {
	/**
	 * Default constructor.
	 */
	Context pimContext 	;

	Logger logger 		;
	
	private static final String FILE_LOGGER 			=	"com.ibm.ccd.wpc_user_scripting.NMG_DEFAULT";
	
	private ArrayList<String> styles		    		= 	new ArrayList<String>();
	
	public POItemAsyncService(Context pimContext) {
		
		this.pimContext = pimContext;
		
		logger = pimContext.getLogger(FILE_LOGGER);

	}
	
	ExecutorService executor       = Executors.newFixedThreadPool(10);
 
	public void getOffers(ArrayList<String> offers, Item item )  {	
		
		try 
 
		{
			
			CompletionService<HashMap<String, String>> pool     = new ExecutorCompletionService<HashMap<String, String>>(executor);
			
			for (String offer : offers) {

				 Callable<HashMap<String, String>> callable = new FetchItem(pimContext, offer , "iLiNK PO Catalog");
				
				 pool.submit(callable);

			}
			
			int counter = 0;
			
			for (String offer : offers) {
  				
     			HashMap<String, String> hm = pool.take().get();
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Offer Id"  ,  					hm.get("offerId"));	
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Offer Item Id"  ,  			hm.get("offerItemId"));
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Offer Offer Item Id"  ,  		hm.get("offerOfferItemId"));
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Offer Item Status"  ,  		hm.get("offerItemStatus"));
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Advertised Description"  ,  	hm.get("offerItemId"));
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Retail"  ,  					hm.get("offerItemId"));
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Differentiator 1 Adv Desc"  ,  hm.get("offerItemId"));
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Discontinue Code"  ,  			hm.get("offerItemId"));
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Date To"  ,  					hm.get("dateTo"));
     			
     			item.setAttributeValue("iLiNK Item Style Spec/Identifiers/Offers#" + counter + "/Date From"  ,  				hm.get("dateFrom"));
     			
     			logger.logInfo(hm.toString() );
     			
     			counter++;
     			  
				}
				
			executor.shutdown();
			
		}

		catch (Exception e) {
			
			logger.logInfo("Exception string is  " + e.toString());
			
			for (StackTraceElement st : e.getStackTrace()){
				
				logger.logInfo(st.getLineNumber() + "-----" +  st.getClassName() + "-----" + st.getMethodName());
			}
			
		} finally {
			
			}
		
	}
	
	public  void getOtherOffers(String primaryKey, Item item){
		
		com.ibm.pim.context.Context PIMContext;
	
		try {
				
				CatalogManager ctgManger = pimContext.getCatalogManager();
					
				Catalog ctg = ctgManger.getCatalog("iLiNK PO Catalog");
				
				ProcessingOptions po = ctg.getProcessingOptions();

				po.setEntryBuildScriptProcessing(false);
				
				setOtherItemIDArray("iLiNK PO Ctg Spec/Styles", item); 
			
			 //   getOffers(styles , item  );
		    
		} 
	
	catch (Exception e) {
		
		logger.logInfo("Exception string is  " + e.toString());
		
		for (StackTraceElement st : e.getStackTrace()){
			
			logger.logInfo(st.getLineNumber() + "-----" +  st.getClassName() + "-----" + st.getMethodName());
		}
		 
		}
		
	}

	private void setOtherItemIDArray(String attributePath, Item item) {

	try {
		
		AttributeInstance 		attrInstance = item.getAttributeInstance(attributePath);
		
		// This will list an array of styles	 
		List<AttributeInstance> innerInstances = (List<AttributeInstance>) attrInstance.getChildren();
		
		// Loops over the offer item array
		for (AttributeInstance innerInstance : innerInstances) {
			
			// Gives the individual attributes within the offer item array
			List<AttributeInstance>  innerInstances1	= (List<AttributeInstance>) innerInstance.getChildren();
			
			for (AttributeInstance innerInstance12 : innerInstances1) {
								  
				if ( innerInstance12.getPath().endsWith("iLiNK Id") ) {
				
                  styles.add( innerInstance12.getDisplayValue()) ;
                  
                  logger.logInfo(innerInstance12.getDisplayValue());
                  
                  break;
               
				}
				
			}   
  
		}
		
	}

	catch (Exception e) {
		
		logger.logInfo("Exception in setOtherItemIDArray is " + e.getMessage());
 
 	}

}

}