package com.walmart.store.recruiting.ticket.domain;

public class ReserveSeat {
    private String id;
    private int numSeats;
    private int seatId;
    
    

	public ReserveSeat(SeatHold  seatHold) {
		super();
		this.id = seatHold.getId();
		this.numSeats = seatHold.getNumSeats();
		this.seatId = seatHold.getSeatId();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getNumSeats() {
		return numSeats;
	}
	public void setNumSeats(int numSeats) {
		this.numSeats = numSeats;
	}
	public int getSeatId() {
		return seatId;
	}
	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}
    
}
