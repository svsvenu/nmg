package com.nmg.multithread;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import com.ibm.pim.attribute.AttributeDefinition;
import com.ibm.pim.attribute.AttributeDefinitionProperty;
import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.CatalogManager;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.common.ProcessingOptions;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.hierarchy.Hierarchy;
import com.ibm.pim.hierarchy.HierarchyManager;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.utils.Logger;
 
public class FetchItem implements Callable<HashMap<String,String>> {
	/**
	 * Default constructor. Venu
	 */
// NPA for other offers
	public static final String OFFER_ID_PATH           		= "iLiNK Offer Item Ctg Spec/Offer#0/Offer Id";
	public static final String OFFER_ITEM_ID_PATH           = "iLiNK Offer Item Ctg Spec/System/Identifiers/Offer Item Id";
	public static final String OFFER_OFFER_ITEM_ID_PATH     = "iLiNK Offer Item Ctg Spec/System/Identifiers/Offer Offer Item Id";
	public static final String OFFER_ITEM_STATUS_PATH     	= "iLiNK Offer Item Ctg Spec/System/Status";
	
	public static final String OFFER_ITEM_DATE_TO_PATH     	= "Category iLiNK Offer Spec/Date To";
	public static final String OFFER_ITEM_DATE_FROM_PATH   	= "Category iLiNK Offer Spec/Date From";
	
	public static final String OFFER_ILINK_ID_PATH   		= "iLiNK Offer Item Ctg Spec/Offer#0/iLiNK Id";

	
	public static final String ADVERTISED_DESCRIPTION_PATH  = "iLiNK Offer Item Ctg Spec/Core/Advertised Description";
	public static final String RETAIL_PATH                  = "iLiNK Offer Item Ctg Spec/Core/Retail";
	public static final String DIFFERENTIATOR_PATH          = "iLiNK Offer Item Ctg Spec/Differentiators#0/Codes#0/Advertised Description";
	public static final String DISCONTINUE_CODE_PATH        = "iLiNK Offer Item Ctg Spec/System/Discontinue Code";

//	NPA for item hierarchy
	public static final String OFFER_CODE_PATH      		=  "Category iLiNK Offer Spec/Offer Type Code" ;
	public static final String OFFER_HIER_DATE_FROM 		=  "Category iLiNK Offer Spec/Date From";
	
// NPA for PO
	
	public static final String PO_ITEM_STATUS_PATH      		=  "iLiNK PO Ctg Spec/System/Status" ;
	public static final String PO_ITEM_CMOS_PATH      			=  "iLiNK PO Ctg Spec/Identifiers/CMOS Style" ;
	public static final String PO_ITEM_VENDOR_STYLE_PATH   		=  "iLiNK PO Ctg Spec/Vendor/Vendor Style" ;
	public static final String PO_ITEM_VENDOR_DESCRIPTION_PATH  =  "iLiNK PO Ctg Spec/Core/Vendor Description" ;
	public static final String PO_ITEM_PO_NOTES_PATH			=  "iLiNK PO Ctg Spec/Core/Style PO Notes" ;
	public static final String PO_ITEM_UOM_PATH			  		=  "iLiNK PO Ctg Spec/?" ;

	
	    

	
	private static final String FILE_LOGGER 				=	"com.ibm.ccd.wpc_user_scripting.NMG_DEFAULT";

//	Context pimContext ;     
	
	Logger logger 		;
	
	public FetchItem() {
		
	}
	
	public FetchItem(Context pimContext , String skuItem, String catalogName) throws Exception {

	  this.skuItem = skuItem;
	  
	  this.catalogName = catalogName;
	  
	//  this.pimContext = pimContext;
	  
		logger = pimContext.getLogger(FILE_LOGGER);
		
	//    logger.logInfo("In fetch item constructor called with pim context " + pimContext.toString()); 

	}

    Item item;

	private String skuItem;
	
	private String catalogName;
	
	public HashMap<String,String> call() throws Exception {
		
		HashMap<String, String> returnHash = null;
		
		try{
		
		String itemID ="" ;
		
		String index  = "" ;

		com.ibm.pim.context.Context PIMContext = com.ibm.pim.context.PIMContextFactory.getContext("Admin", "trinitron", "nmg");

		if (catalogName.equalsIgnoreCase("iLiNK Offer Item Catalog")) {
			
			 itemID = this.skuItem.split("\\^\\|")[1];
			
			 index =  this.skuItem.split("\\^\\|")[0];
			
			CatalogManager ctgManger = PIMContext.getCatalogManager(); 
			
			logger.logInfo("got ctgManager " + ctgManger.getManagerName());
			
			Catalog ctg = ctgManger.getCatalog(catalogName);
			
			if ( ctg == null ) { logger.logInfo("ctg is null " ); }
			
			ProcessingOptions po = ctg.getProcessingOptions();

			po.setEntryBuildScriptProcessing(false);
			
			this.item = ctg.getItemByPrimaryKey(itemID);
			
			PIMContext.cleanUp();
			
			returnHash = gatherItemHash(index);

		}
		
		else if (catalogName.equalsIgnoreCase("item iLiNK Offer Hierarchy")) {
			
			CatalogManager ctgManger = PIMContext.getCatalogManager(); 
			
		//	logger.logInfo("got ctgManager " + ctgManger.getManagerName());
			
			Catalog ctg = ctgManger.getCatalog("iLiNK Offer Item Catalog");
			
			if ( ctg == null ) { logger.logInfo("ctg is null " ); }
			
			ProcessingOptions po = ctg.getProcessingOptions();

			po.setEntryBuildScriptProcessing(false);
			
			this.item = ctg.getItemByPrimaryKey(this.skuItem);
			
			returnHash = gatherOtherItemHashForItem();
			
			String offerIlinkId = getItemAttributeValue(OFFER_ILINK_ID_PATH);
			
			HierarchyManager hm = PIMContext.getHierarchyManager();
			
			Hierarchy iLinkHierarchy = PIMContext.getHierarchyManager().getHierarchy("iLiNK Offer Hierarchy");
			
			iLinkHierarchy.getProcessingOptions().setEntryBuildScriptProcessing(false);
			
			Category ct      = iLinkHierarchy.getCategoryByPrimaryKey(offerIlinkId);
			
			returnHash.put("dateTo", 					ct.getAttributeValue (OFFER_ITEM_DATE_TO_PATH).toString());
			
			returnHash.put("dateFrom", 					ct.getAttributeValue (OFFER_ITEM_DATE_FROM_PATH).toString());
	
			PIMContext.cleanUp();

		}
		
		else if (catalogName.equalsIgnoreCase("iLiNK PO Catalog")) {
			
			CatalogManager ctgManger = PIMContext.getCatalogManager(); 
			
		//	logger.logInfo("got ctgManager " + ctgManger.getManagerName());
			
			Catalog ctg = ctgManger.getCatalog("iLiNK Item Catalog");
			
			if ( ctg == null ) { logger.logInfo("ctg is null " ); }
			
			ProcessingOptions po = ctg.getProcessingOptions();

			po.setEntryBuildScriptProcessing(false);
			
			this.item = ctg.getItemByPrimaryKey(this.skuItem);
			
			returnHash = gatherStylesForPOItem();
			
			PIMContext.cleanUp();

		}
		
		else {
			
			returnHash = new HashMap<String, String>();
			
			PIMContext.getHierarchyManager().getManagerName();
		    
		    Category ct      = PIMContext.getHierarchyManager().getHierarchy("iLiNK Offer Hierarchy").getCategoryByPrimaryKey(itemID);
		    		 
		    String oc        = (String) ct.getAttributeValue(OFFER_CODE_PATH);
		  
		    String dateFrom  =  ct.getAttributeValue(OFFER_HIER_DATE_FROM).toString();
		    
		    	returnHash.put("offerCode", oc);
		   
		    	returnHash.put("dateFrom", dateFrom);
		   
		    	returnHash.put("index",index);
		   
		   PIMContext.cleanUp();
		    	
		}
		}
		
		catch(Exception e){
			
			logger.logInfo("Exception string is  " + e.toString());
			
			for (StackTraceElement st : e.getStackTrace()){
				
				logger.logInfo(st.getLineNumber() + "-----" +  st.getClassName() + "-----" + st.getMethodName());
			}
		}
			
		  return returnHash;
		
		
		
	}

	private String getItemAttributeValue(String attributePath) {
		
		AttributeInstance attrInstance = item.getAttributeInstance(attributePath);

		if (attrInstance != null) {

			 return getAttributeValue(attrInstance);
		}

		else {

			return "attr instance is null";
		}

	}

	private String getAttributeValue(AttributeInstance attrInstance) {

		String attrValueString = "none";

		try {

			AttributeDefinition ad = attrInstance.getAttributeDefinition();

		   if (ad.getType().equals(AttributeDefinition.Type.GROUPING)) {
			   
				List<AttributeInstance> innerInstances = (List<AttributeInstance>) attrInstance.getChildren();
				
				for (AttributeInstance innerInstance : innerInstances) {
					
					attrValueString += innerInstance.getDisplayValue();
					
				}
				
			} 
		   
		   else if (attrInstance.getValue() != null) {
				if (ad.getType().equals(AttributeDefinition.Type.CURRENCY)) {
					String currencyCode = ad.getProperty(
							AttributeDefinitionProperty.Name.CURRENCY)
							.getValue();
					attrValueString =  attrInstance.getValue().toString();
				} else if (ad.getType().equals(AttributeDefinition.Type.DATE)) {
					Date attributeDateValue = (Date) (attrInstance.getValue());
					attrValueString = String.valueOf(attributeDateValue
							.getTime());
				} else if (ad.getType().equals(
						AttributeDefinition.Type.RELATIONSHIP)) {
					com.ibm.pim.catalog.item.Item relItem = (com.ibm.pim.catalog.item.Item) (attrInstance
							.getValue());
					attrValueString = relItem.getCatalog().getName() + ":"
							+ relItem.getPrimaryKey();
				} else if (ad.getType().equals(
						AttributeDefinition.Type.TIMEZONE))
					attrValueString = attrInstance.getDisplayValue();
				else if (ad.getType().equals(AttributeDefinition.Type.NUMBER)
						|| ad.getType().equals(
								AttributeDefinition.Type.NUMBER_ENUMERATION)) {
					int precision = Integer.valueOf(
							ad.getProperty(
									AttributeDefinitionProperty.Name.PRECISION)
									.getValue()).intValue();
					Double d = (Double) attrInstance.getValue();
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMinimumFractionDigits(precision);
					attrValueString = nf.format(d.doubleValue());
				} else
					attrValueString = attrInstance.getValue().toString();
			} else if (ad.getType().equals(AttributeDefinition.Type.TIMEZONE)
					|| ad.getType().equals(
							AttributeDefinition.Type.LOOKUP_TABLE)
					|| ad.getType().equals(
							AttributeDefinition.Type.NUMBER_ENUMERATION)
					|| ad.getType().equals(
							AttributeDefinition.Type.STRING_ENUMERATION)
			// || ad.getType().equals(AttributeDefinition.Type.FLAG)
			)
				attrValueString = "-NONE-";
			else
				attrValueString = "none";

		}

		catch (Exception e) {
			
			attrValueString = e.getMessage();

		}

		return attrValueString;
	}
	
	
	public HashMap<String,String> gatherItemHash(String index){
		
		HashMap<String, String> returnHash = new HashMap<String, String>();
		
		returnHash.put("offerItemId", 			getItemAttributeValue(OFFER_ITEM_ID_PATH));
		
		returnHash.put("advertisedDescription", getItemAttributeValue(ADVERTISED_DESCRIPTION_PATH));
		
		returnHash.put("retail", 				getItemAttributeValue(RETAIL_PATH));
		
		returnHash.put("discontinueCode", 		getItemAttributeValue(DISCONTINUE_CODE_PATH));
		
		returnHash.put("differentiator", 		getItemAttributeValue(DIFFERENTIATOR_PATH));
		
		returnHash.put("index", index);
		
		return returnHash;
		
	}
		
	public HashMap<String,String> gatherOtherItemHashForItem(){
		
		HashMap<String, String> returnHash = new HashMap<String, String>();
		
		returnHash.put("offerId", 					getItemAttributeValue(OFFER_ID_PATH));
		
		returnHash.put("offerItemId", 				getItemAttributeValue(OFFER_ITEM_ID_PATH));
		
		returnHash.put("offerOfferItemId",		 	getItemAttributeValue(OFFER_OFFER_ITEM_ID_PATH));
		
		returnHash.put("offerItemStatus", 			getItemAttributeValue(OFFER_ITEM_STATUS_PATH));
				
		returnHash.put("advertisedDescription", 	getItemAttributeValue(ADVERTISED_DESCRIPTION_PATH));
		
		returnHash.put("retail", 					getItemAttributeValue(RETAIL_PATH));
		
		returnHash.put("differentiator", 			getItemAttributeValue(DIFFERENTIATOR_PATH));
		
		returnHash.put("discontinueCode", 			getItemAttributeValue(DISCONTINUE_CODE_PATH));
		
		return returnHash;
		
	}
	
	public HashMap<String,String> gatherStylesForPOItem(){
		
		HashMap<String, String> returnHash = new HashMap<String, String>();
		
		returnHash.put("offerId", 					getItemAttributeValue(OFFER_ID_PATH));
		
		returnHash.put("offerItemId", 				getItemAttributeValue(OFFER_ITEM_ID_PATH));
		
		returnHash.put("offerOfferItemId",		 	getItemAttributeValue(OFFER_OFFER_ITEM_ID_PATH));
		
		returnHash.put("offerItemStatus", 			getItemAttributeValue(OFFER_ITEM_STATUS_PATH));
				
		returnHash.put("advertisedDescription", 	getItemAttributeValue(ADVERTISED_DESCRIPTION_PATH));
		
		returnHash.put("retail", 					getItemAttributeValue(RETAIL_PATH));
		
		returnHash.put("differentiator", 			getItemAttributeValue(DIFFERENTIATOR_PATH));
		
		returnHash.put("discontinueCode", 			getItemAttributeValue(DISCONTINUE_CODE_PATH));
		
		return returnHash;
		
	}
		
}