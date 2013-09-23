package com.nmg.multithread;

import com.ibm.ccd.content.common.Category;
import com.ibm.pim.attribute.AttributeDefinition;
import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.CatalogManager;
import com.ibm.pim.catalog.item.*;
import com.ibm.pim.collaboration.CollaborationCategory;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ProcessingOptions;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.CategoryEntryBuildFunctionArguments;
import com.ibm.pim.extensionpoints.CategoryPreviewFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationCategoryEntryBuildFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationCategoryPreviewFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationItemEntryBuildFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationItemPreviewFunctionArguments;
import com.ibm.pim.extensionpoints.EntryBuildFunction; 
import com.ibm.pim.extensionpoints.EntryBuildFunctionArguments;
import com.ibm.pim.extensionpoints.ItemEntryBuildFunctionArguments;
import com.ibm.pim.extensionpoints.ItemPreviewFunctionArguments;

import com.ibm.pim.extensionpoints.ScriptingSandboxFunctionArguments;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.ibm.pim.utils.Logger;

import com.ibm.pim.extensionpoints.ScriptingSandboxFunction;

/**
 * @author venu.surampudi
 *
 */
public class ScriptingSandboxTestImpl implements  EntryBuildFunction {

	private ArrayList<String> otherOfferItemIDs    		= 	new ArrayList<String>();
	
	private static final String FILE_LOGGER 			=	"com.ibm.ccd.wpc_user_scripting.NMG_DEFAULT";
 
	public static final Context pimContext 			    = 	PIMContextFactory.getCurrentContext();
	
	private static Logger logger 						= 	pimContext.getLogger(FILE_LOGGER);
	
	private ArrayList<String> otherOfferCategories 		= 	new ArrayList<String>();
	
	@Override
	public void entryBuild(ItemEntryBuildFunctionArguments arg0) {
		
		Item it = arg0.getItem();

		
	//	logger.logInfo("-----------------------------Got item "  + it.getDisplayName());
		
		HashMap<String, Response> npa = getOtherOffers(it.getPrimaryKey(), it);
		
		for (String index : npa.keySet()){
			
			Response rp =   npa.get(index);
			
		//	logger.logInfo("index is " + index);
			
		//	logger.logInfo("Response is "  + rp.getOfferCode());
			
			it.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Offer Code", rp.getOfferCode());	
			
			it.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/From Date",  rp.getOfferDate());		
			
			ArrayList<NPAttributes> otherOfferAttributes = rp.getOtherOfferItems();
			
			int counter = 0;
			
			for (NPAttributes otherOfferAttribute : otherOfferAttributes )  {
				
				it.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
						             "/Offer Item Id" ,  otherOfferAttribute.getOfferItemID());	
				
				it.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
			                         "/Advertised Description" ,  otherOfferAttribute.getAdvertisedDescription());
				
				it.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
			                         "/Retail" ,  otherOfferAttribute.getRetail());
				
				it.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
			                         "/Differentiator 1 Adv Desc" ,  otherOfferAttribute.getDifferentiator1());
				
				it.setAttributeValue("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers#" + index + "/Other Offer Items#" + counter + 
			                         "/Discontinue Code" ,  otherOfferAttribute.getDiscontinueCode());
				
				counter++;

			}
			
		}
		
	}
	
	private void setOtherItemIDArray(String attributePath, Item item) {

		try {
			
			AttributeInstance attrInstance = item.getAttributeInstance(attributePath);
			
//			AttributeInstance attrInstanceT = item.getAttributeInstance("iLiNK Offer Item Ctg Spec/Details#0/Style#0/Other Offers#0/Offer iLiNK Id");
			
//			logger.logInfo(attrInstanceT.getValue());

//			AttributeDefinition ad = attrInstance.getAttributeDefinition();
				 
			List<AttributeInstance> innerInstances = (List<AttributeInstance>) attrInstance.getChildren();
			
	//		logger.logInfo("path passed is " + attributePath + " Children size is " + attrInstance.getChildren().size());
			
	/*		logger.logInfo("item passed is " + item.getDisplayName() + "attribute path is " + attrInstance.getPath() + " instance owner is  " + attrInstance.getOwner()
							+  " attr instance displayValue " + attrInstance + " index= " + attrInstance.getOccurrenceIndex()
							+ " Attribute children count=" + attrInstance.getChildren().size()
							+ " Localized name" + attrInstance.getAttributeDefinition().getLocalizedName()
							+  " Is multi occurenc is " + attrInstance.isMultiOccurrence()
							+ " Is grouping " +  attrInstance.isGrouping() 
						
							
					
					);*/
				
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

	@Override
	public void entryBuild(CategoryEntryBuildFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entryBuild(CollaborationItemEntryBuildFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entryBuild(CollaborationCategoryEntryBuildFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void entryBuild(EntryBuildFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
	}

	public  HashMap<String, Response> getOtherOffers(String primaryKey, Item item){
		
		com.ibm.pim.context.Context PIMContext;
		
		HashMap<String, Response> npa = new HashMap<String, Response>();
		
	//	logger.logInfo("Inside get OtherOffers with primary key as " + primaryKey);
		
		logger.logInfo( "Processors are " + Runtime.getRuntime().availableProcessors() );
	
		try {
							
		//		PIMContext = com.ibm.pim.context.PIMContextFactory.getContext("Admin", "trinitron", "nmg");
				
				CatalogManager ctgManger = pimContext.getCatalogManager();
					
				Catalog ctg = ctgManger.getCatalog("iLiNK Offer Item Catalog");
				
				ProcessingOptions po = ctg.getProcessingOptions();

				po.setEntryBuildScriptProcessing(false);

			//	Item item = ctg.getItemByPrimaryKey(primaryKey); 
				
			//	logger.logInfo("with khai got item object from catalog " + primaryKey);
				
				setOtherItemIDArray("iLiNK Offer Item Ctg Spec/Details/Style/Other Offers", item);
				
			//	logger.logInfo("got Other item arrays with otherOfferItemIDs having " + otherOfferItemIDs + "----" +
			//			       "otherOfferCategories having  "  + otherOfferCategories 
			//			       );
				
		//		pimContext.cleanUp();
				
				logger.logInfo("Before constructing SKUAsyncService with context " + pimContext.toString());
							
				SKUAsyncService skuService = new SKUAsyncService(PIMContextFactory.getCurrentContext());
				
				logger.logInfo("After constructing SKUAsyncService ");
			
			    npa = skuService.getSKU(otherOfferItemIDs,otherOfferCategories );
			    
		} 
	
	catch (Exception e) {
		
		logger.logInfo("Exception string is  " + e.toString());
		
		for (StackTraceElement st : e.getStackTrace()){
			
			logger.logInfo(st.getLineNumber() + "-----" +  st.getClassName() + "-----" + st.getMethodName());
		}
		 
		}
		
		return npa;
		
	}

}
