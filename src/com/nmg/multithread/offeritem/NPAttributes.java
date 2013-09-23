package com.nmg.multithread.offeritem;

public class NPAttributes {

	public NPAttributes () {}
	
	public NPAttributes (String index, String offerItemIlinkID, String offerItemID, String advertisedDescription, String retail, String differentiator1, String discontinueCode  ){
		
		this.index 					= index;
		
		this.offerItemIlinkID 		= offerItemIlinkID ;
		
		this.offerItemID 			= offerItemID;
		
		this.advertisedDescription 	= advertisedDescription;
		
		this.retail 				= retail;
		
		this.differentiator1 		= differentiator1;
		
		this.discontinueCode 		= discontinueCode;
		
	}
	
	String offerItemIlinkID ;
	
	String offerItemID;
	
	String advertisedDescription;
	
	String retail;
	
	String differentiator1 ;
	
	String discontinueCode ;
	
	String index;
	
	public String getIndex() {
		
		return index;
	}

	public void setIndex(String index) {
		
		this.index = index;
		
	}

	public String getOfferItemIlinkID() {
		
		return offerItemIlinkID;
		
	}
	public void setOfferItemIlinkID(String offerItemIlinkID) {
		
		this.offerItemIlinkID = offerItemIlinkID;
		
	}
	public String getOfferItemID() {
		
		return offerItemID;
		
	}
	public void setOfferItemID(String offerItemID) {
		
		this.offerItemID = offerItemID;
		
	}
	public String getAdvertisedDescription() {
		
		return advertisedDescription;
		
	}
	
	public void setAdvertisedDescription(String advertisedDescription) {
		
		this.advertisedDescription = advertisedDescription;
		
	}
	
	public String getRetail() {
		
		return retail; 
		
	}
	
	public void setRetail(String retail) {
		
		this.retail = retail;
		
	}
	
	public String getDifferentiator1() {
		
		return differentiator1;
		
	}
	
	public void setDifferentiator1(String differentiator1) {
		
		this.differentiator1 = differentiator1;
		
	}
	
	public String getDiscontinueCode() {
		
		return discontinueCode;
		
	}
	
	public void setDiscontinueCode(String discontinueCode) {
		
		this.discontinueCode = discontinueCode;
		
	}
	
	public String toString(){
		
		return  index           			+ ":" +
		        offerItemID     			+ ":" +  
				advertisedDescription 		+ ":" +  
		        retail						+ ":" +  
				differentiator1 			+ ":" +  
		        discontinueCode ;
	}

}
