package com.walmart.store.recruiting.ticket.service.impl;

import com.walmart.store.recruiting.ticket.domain.SeatHold;
import com.walmart.store.recruiting.ticket.domain.Venue;
import com.walmart.store.recruiting.ticket.service.TicketService;


import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A ticket service implementation.
 */
public class TicketServiceImpl implements TicketService ,Runnable {

    private int seatsAvailable;
    private int seatsReserved;
    private final int timeOut=2000;
    
    //private Map<String, SeatHold> seatHoldMap = new HashMap<>();  
    //here I used ConcurrentHashMap insted of hashMap to hold pool of 
    private ConcurrentHashMap<String, SeatHold> seatHoldMap = new ConcurrentHashMap<>();
    
   
    public TicketServiceImpl(Venue venue) {
        seatsAvailable = venue.getMaxSeats();
        
    }

    @Override
    public int numSeatsAvailable() {
        return seatsAvailable;
    }

    public int numSeatsReserved() {
        return this.seatsReserved;
    }

    @Override
    public Optional<SeatHold> findAndHoldSeats(int numSeats) {
    	
        Optional<SeatHold> optionalSeatHold = Optional.empty();
        
        if (seatsAvailable >= numSeats) {
            String holdId = generateId();
            SeatHold seatHold = new SeatHold(holdId, numSeats);
            optionalSeatHold = Optional.of(seatHold);
            seatHoldMap.put(holdId, seatHold);
            seatsAvailable -= numSeats;
            
            
        }

        return optionalSeatHold;
    }

    
    
    public void handelReserv(){
    	Timer timer = new Timer();
    	
    }
    
    @Override
    public synchronized Optional<String> reserveSeats(String seatHoldId) {
        Optional<String> optionalReservation = Optional.empty();
        SeatHold seatHold = seatHoldMap.get(seatHoldId);
        String holdKey="";
        final BlockingQueue<String> bq = new ArrayBlockingQueue<String>(26);
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        
        Runnable producer;
        
        
	    producer = new Runnable() {
		      public void run() {
		          if (seatHold != null) {
		              seatsReserved += seatHold.getNumSeats();
		              try {
						bq.put(Optional.of(seatHold.getId()).toString());
						seatHoldMap.remove(seatHoldId);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		              seatHoldMap.remove(seatHoldId);
		          }
		      }
		    };
		 
		    
		executor.execute(producer);
		
	
		//wait for timeout 
		try {
			producer.wait(timeOut);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	

	    Runnable consumer;
		
	    /// check if reserved seat has already been taken if not put the seat again 
	    consumer = new Runnable() {
		      public void run() {
		        String holdNo="";
		        while (!bq.isEmpty()){
		          try {
		        	  holdNo = bq.take();
		        	  
		        	  if (seatHoldMap.get(holdNo)==null){
		                seatsReserved -= seatHold.getNumSeats();
		               // holdKey= Optional.of(seatHold.getId()).toString();
		                //ptionalReservation =  Optional.of(seatHold.getId());
		                seatHoldMap.put(holdNo, seatHold); 
		        	  }
		        	  bq.remove(holdNo);
		            
		          } catch (InterruptedException ie) {
		        	  
		          }
		        }
		        executor.shutdownNow();
		      }
		    };
		    executor.execute(consumer);

        return optionalReservation;
    }
    
    
    
    public Optional<SeatHold> findAndHoldBlockSeats(int numSeats) {
        Optional<SeatHold> optionalSeatHold = Optional.empty();
      
        ConcurrentHashMap<Long,Long> tempSeatHolder = new ConcurrentHashMap<>();
        long holdId=0;
        
        if (seatsAvailable >= numSeats) {
        	//assuming the max number for seat 
        	
        	for (int i=1;i<=maxSeatSize() ; i ++){
        		holdId=i;
        		
        		//finish find location 
        		if (tempSeatHolder.size()==numSeats)
        			break;
        		
        		//already reserved before so free current hold values in seat
        		if (seatHoldMap.containsKey(holdId)){
        			while (!tempSeatHolder.isEmpty()) {
        				tempSeatHolder.remove(tempSeatHolder.get(tempSeatHolder.size()-1));
        			}
        		}
        		else {
        			tempSeatHolder.put(holdId, holdId);
        		}
        	}
        	
        	// the seat is loaded and available now do the hold process
        	if (tempSeatHolder.size()==numSeats) {
	        		while (!tempSeatHolder.isEmpty()){
	                    holdId = tempSeatHolder.get(tempSeatHolder.size()-1);
	                    tempSeatHolder.remove(holdId);
	                    SeatHold seatHold = new SeatHold(String.valueOf(holdId), numSeats);
	                    optionalSeatHold = Optional.of(seatHold);
	                    seatHoldMap.put(String.valueOf(holdId), seatHold);
	                    seatsAvailable -= numSeats;        		
	                 }
        	}
        	
        }

        return optionalSeatHold;
    }

    //assuming the max number for seat and generateId should be between min and max when looking for seat block 
    public Long maxSeatSize(){
    	return 999l;
    	
    }
    
    //last ID in map
    public String getMaxId(){
    	return seatHoldMap.get(seatHoldMap.size()).toString();
    }
    
    private String generateId() {
        return UUID.randomUUID().toString();
    }

	@Override
    public synchronized void run() {
        String importantInfo[] = {
        };
        try {
            for (int i = 0; i < importantInfo.length;i++) {
                Thread.sleep(4000);
                
            }
        } catch (InterruptedException e) {
            
        }
    }
    

    
    
}
