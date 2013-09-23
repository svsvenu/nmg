package com.nmg.multithread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ibm.pim.context.Context;
import com.ibm.pim.utils.Logger;

public class SKUAsyncService  {
	/**
	 * Default constructor.
	 */
	Context pimContext 	;

	Logger logger 		;
	
	private static final String FILE_LOGGER 			=	"com.ibm.ccd.wpc_user_scripting.NMG_DEFAULT";
	
	public SKUAsyncService(Context pimContext) {
		
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

}