package com.walmart.store.recruiting.ticket.service.impl;

import com.walmart.store.recruiting.ticket.domain.ReserveSeat;
import com.walmart.store.recruiting.ticket.domain.SeatHold;
import com.walmart.store.recruiting.ticket.domain.Venue;
import com.walmart.store.recruiting.ticket.service.TicketService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A ticket service implementation.
 */
public class TicketServiceImpl implements TicketService  {

    private int seatsAvailable;
    private int seatsReserved;
    private int nextSeatAvailable;
    private final int TIME_OUT=4000;
    

    private Map<String, SeatHold> seatHoldMap = new ConcurrentHashMap<>();
    private Map<String, ReserveSeat> reserveSeatMap = new ConcurrentHashMap<>();
   
    public TicketServiceImpl(Venue venue) {
        seatsAvailable = venue.getMaxSeats();
        nextSeatAvailable=1;
        
    }

    public int getNextSeatAvailable() {
		return nextSeatAvailable;
	}

	@Override
    public int numSeatsAvailable() {
        return seatsAvailable;
    }

    public int numSeatsReserved() {
        return this.seatsReserved;
    }

    @Override
    public synchronized Optional<SeatHold> findAndHoldSeats(int numSeats) {
        Optional<SeatHold> optionalSeatHold = Optional.empty();
        System.out.println("seatsAvailable " + seatsAvailable);
        if (seatsAvailable >= numSeats) {
            String holdId = generateId();
            nextSeatAvailable+=numSeats;
            System.out.println("nextSeatAvailable " + nextSeatAvailable);
            SeatHold seatHold = new SeatHold(holdId, numSeats,nextSeatAvailable );
            optionalSeatHold = Optional.of(seatHold);
            seatHoldMap.put(holdId, seatHold);
            System.out.println("seatHoldMap " + seatHoldMap.size());
            seatsAvailable -= numSeats;
            HoldTimeout holdTimeout=new HoldTimeout(holdId);
            holdTimeout.start();
        }

        return optionalSeatHold;
    }

   
    
    public int getSeatsAvailable() {
		return seatsAvailable;
	}

	public int getSeatsReserved() {
		return seatsReserved;
	}

	@Override
    public Optional<String> reserveSeats(String seatHoldId) {
        Optional<String> optionalReservation = Optional.empty();
        SeatHold seatHold = seatHoldMap.get(seatHoldId);
        
	    if (seatHold !=null){
	          seatsReserved += seatHold.getNumSeats();
	          optionalReservation =  Optional.of(seatHold.getId());
	          ReserveSeat reserveSeat = new ReserveSeat(seatHold);
	          //seatHoldMap.put(seatHoldId, seatHold); 
	          reserveSeatMap.put(seatHoldId, reserveSeat); 
	          seatHoldMap.remove(seatHoldId);
	      }
		  else {
			System.out.println("Error in reserveSeats ...");
		}
      
        return optionalReservation;
    }
    
    
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    
    class HoldTimeout extends Thread{
    	private String holdId;
    	
    	public HoldTimeout (String holdId){
    		this.holdId=holdId;
    	}
    	
		@Override
		public void run() {
			try {
				Thread.sleep(TIME_OUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (seatHoldMap.containsKey(holdId)){
				System.out.println("thread remove .... " + seatsAvailable);
				
				seatsAvailable+= seatHoldMap.get(holdId).getNumSeats();
				nextSeatAvailable-=seatHoldMap.get(holdId).getNumSeats();
				seatHoldMap.remove(holdId);
			}
		}
    	
    }
    
}
