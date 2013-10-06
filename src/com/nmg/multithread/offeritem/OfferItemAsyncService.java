package com.nmg.multithread.offeritem;

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
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.utils.Logger;
import com.nmg.multithread.FetchItem; 
import com.nmg.multithread.offeritem.NPAttributes;
import com.nmg.multithread.offeritem.Response;

public class OfferItemAsyncService  {
	/**
	 * Default constructor.
	 */
	Context pimContext 	;

	Logger logger 		;
	
	private static final String FILE_LOGGER 			=	"com.ibm.ccd.wpc_user_scripting.NMG_DEFAULT";
	
	private ArrayList<String> otherOfferItemIDs    		= 	new ArrayList<String>();
	
	private ArrayList<String> otherOfferCategories 		= 	new ArrayList<String>();
	
	public OfferItemAsyncService(Context pimContext) {
		
		this.pimContext = pimContext;
		
		logger = pimContext.getLogger(FILE_LOGGER);
		
		logger.logInfo("contructor called with pim context " + pimContext.toString());
	}
	
	ExecutorService executor       = Executors.newFixedThreadPool(10);

	public HashMap<String, Response> getSKU(ArrayList<String> offerItemIDs, ArrayList<String> otherOfferHierarchies)  {	
		
		List<NPAttributes> responseItemList = new ArrayList<NPAttributes>();
		
		HashMap<String, Response> responseHash = new HashMap<String, Response>();
		
		try 
 
		{
		
			logger.logInfo("Called getSKU" );
			
			CompletionService<HashMap<String, String>> pool     = new ExecutorCompletionService<HashMap<String, String>>(executor);
			
			for (String otherOfferHierarchy : otherOfferHierarchies) {

				 Callable<HashMap<String, String>> callable = new FetchItem(pimContext, otherOfferHierarchy , "iLiNK Offer Hierarchy");
				
				 pool.submit(callable);

			}
			

			for (String otherOfferHierarchy : otherOfferHierarchies) {
  				
     			HashMap<String, String> hm = pool.take().get();
     			
   				Response rp = new Response();
   				
    			rp.setOfferCode(hm.get("offerCode"));
     			
    			rp.setOfferDate(hm.get("dateFrom"));
     			
    			responseHash.put(hm.get("index"), rp);
     			  
				}
			
			//  Collect Hierarchies in a thread pool
				
			for (String offerItemID : offerItemIDs) {

				 Callable<HashMap<String, String>> callable = new FetchItem(pimContext, offerItemID, "iLiNK Offer Item Catalog");
				
				 pool.submit(callable);

			}
				
     		for (String skuItem : offerItemIDs) {
     		     				
     			HashMap<String, String> hm = pool.take().get();
     			
     			NPAttributes item = new NPAttributes(hm.get("index"), "", hm.get("offerItemId"), hm.get("advertisedDescription"), hm.get("retail"),  hm.get("differentiator"), hm.get("discontinueCode") );
     			
     			responseItemList.add(item); 
     			
     			if ( responseHash.get(hm.get("index")) == null   ) {
     				
     				Response rp = new Response();
     			     				
     				rp.getOtherOfferItems().add(item);
     				
     				responseHash.put(hm.get("index"), rp);
     			}
     			
     			else{
     				
     				Response rp = responseHash.get(hm.get("index"));
     				
     				rp.getOtherOfferItems().add(item);
     				
     				responseHash.put(hm.get("index"), rp);
     			}
     						

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

		return responseHash;

	}
	
	public  void getOtherOffers(String primaryKey, Item item){
		
		com.ibm.pim.context.Context PIMContext;
		
		HashMap<String, Response> npa = new HashMap<String, Response>();
		
		logger.logInfo( "Processors are " + Runtime.getRuntime().availableProcessors() );
	
		try {
				
				CatalogManager ctgManger = pimContext.getCatalogManager();
					
				Catalog ctg = ctgManger.getCatalog("iLiNK Offer Item Catalog");
				
				ProcessingOptions po = ctg.getProcessingOptions();

				po.setEntryBuildScriptProcessing(false);
				
				setOtherItemIDArray("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers", item);
							
			//	SKUAsyncService skuService = new SKUAsyncService(PIMContextFactory.getCurrentContext());
			
			    npa = getSKU(otherOfferItemIDs,otherOfferCategories );
			    
				for (String index : npa.keySet()){
					
					Response rp =   npa.get(index);
					
					item.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Offer Code", rp.getOfferCode());	
					
					item.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/From Date",  rp.getOfferDate());		
					
					ArrayList<NPAttributes> otherOfferAttributes = rp.getOtherOfferItems();
					
					int counter = 0;
					
					for (NPAttributes otherOfferAttribute : otherOfferAttributes )  {
						
						item.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
								             "/Offer Item Id" ,  otherOfferAttribute.getOfferItemID());	
						
						item.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
					                         "/Advertised Description" ,  otherOfferAttribute.getAdvertisedDescription());
						
						item.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
					                         "/Retail" ,  otherOfferAttribute.getRetail());
						
						item.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
					                         "/Differentiator 1 Adv Desc" ,  otherOfferAttribute.getDifferentiator1());
						
						item.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
					                         "/Discontinue Code" ,  otherOfferAttribute.getDiscontinueCode());
						
						counter++;

					}
					
				}
			    
		} 
	
	catch (Exception e) {
		
		logger.logInfo("Exception string is  " + e.toString());
		
		for (StackTraceElement st : e.getStackTrace()){
			
			logger.logInfo(st.getLineNumber() + "-----" +  st.getClassName() + "-----" + st.getMethodName());
		}
		 
		}
		
	//	return npa;
		
	}

	private void setOtherItemIDArray(String attributePath, Item item) {

	try {
		
		AttributeInstance attrInstance = item.getAttributeInstance(attributePath);
			 
		List<AttributeInstance> innerInstances = (List<AttributeInstance>) attrInstance.getChildren();
			
		for (AttributeInstance innerInstance : innerInstances) {
				
				List<AttributeInstance> innerInstances1 = (List<AttributeInstance>) innerInstance.getChildren();
				
				for (AttributeInstance innerInstance12 : innerInstances1) {
					  
					  if (innerInstance12.getPath().endsWith("Offer iLiNK Id")){
				    
				               otherOfferCategories.add(innerInstance.getOccurrenceIndex() + "^|" + innerInstance12.getDisplayValue()) ;
				               
					  }
					  
					  if (innerInstance12.getPath().endsWith("Other Offer Items")){
						  
						  List<AttributeInstance> otherOfferItems = (List<AttributeInstance>) innerInstance12.getChildren();
						  
						  for (AttributeInstance otherOfferItem : otherOfferItems) {
							  
							  List<AttributeInstance> otherOfferItemsAttributes  = (List<AttributeInstance>) otherOfferItem.getChildren();
							  
							  for (AttributeInstance otherOfferItemAttribute : otherOfferItemsAttributes) { 
							  
							  if (otherOfferItemAttribute.getPath().endsWith("Offer Item iLiNK Id")){
							
								  otherOfferItemIDs.add(innerInstance.getOccurrenceIndex() + "^|" + otherOfferItemAttribute.getDisplayValue());
								  									
							  	}
							  
							  }
							  
						  }
			              
					  }
			
				}
				
			}

	}

	catch (Exception e) {
		
		logger.logInfo("Exception in setOtherItemIDArray is " + e.getMessage());

 	}

}

}