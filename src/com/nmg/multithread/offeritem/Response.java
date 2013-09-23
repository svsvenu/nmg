package com.nmg.multithread.offeritem;

import java.util.ArrayList;

public class Response {
	
	private ArrayList<NPAttributes> otherOfferItems = new ArrayList<NPAttributes>();
	
	private String offerCode;
	
	private String offerDate;

	public ArrayList<NPAttributes> getOtherOfferItems() {
		return otherOfferItems;
	}

	public void setOtherOfferItems(ArrayList<NPAttributes> otherOfferItems) {
		this.otherOfferItems = otherOfferItems;
	}

	public String getOfferCode() {
		return offerCode;
	}

	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	public String getOfferDate() {
		return offerDate;
	}

	public void setOfferDate(String offerDate) {
		this.offerDate = offerDate;
	}
	
	public String toString() {
		
		return "offer code=" + offerCode + ":" + "offer date = " + offerDate + otherOfferItems.toString() ;
		
	}

}
