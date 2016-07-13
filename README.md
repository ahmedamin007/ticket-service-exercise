# ticket-service-exercise

Implement a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance venue.

Assume that the venue has a stage and one level of seating, as such:

````
        ----------[[  STAGE  ]]----------
        ---------------------------------
        sssssssssssssssssssssssssssssssss
        sssssssssssssssssssssssssssssssss
        sssssssssssssssssssssssssssssssss
        sssssssssssssssssssssssssssssssss
        sssssssssssssssssssssssssssssssss
        sssssssssssssssssssssssssssssssss
        sssssssssssssssssssssssssssssssss
        sssssssssssssssssssssssssssssssss
        sssssssssssssssssssssssssssssssss
````


The following API can be used to find and reserve seats:

````
public interface TicketService {

/**
 * The number of seats in the requested level that are neither held nor reserved
 */
  int numSeatsAvailable();

/**
 * Find and hold the best available seats for a customer
 * 
 * @param numSeats the number of seats to find and hold
 * @return a SeatHold object identifying the found seats and related information 
 */
  SeatHold findAndHoldSeats(int numSeats);

/**
 * Complete reservation of held seats
 * 
 * @param seatHoldId the seat hold identifier
 * @return a reservation confirmation code 
 */  
  String reserveSeats(int seatHoldId);
}

````

##Instructions
We've created a simple and highly-naive implemenation of the ticket service.
Your assignment is to improve the implementation by adding the following features:

1. **Seat holds expire.**  After some period of time, held seats that are not reserved are returned to the pool of available seats.
2. **Seats are assigned together.** Seats and rows are numbered. Seats are held and reserved in blocks. 

## Notes
* We would like to see a design that can scale to support multiple concurrent users. 
* *Simple is better*. For example, a lazy seat expiration model may be a good alternative to a background thread or timer.
* We understand that tradeoffs must be made to complete the exercise within the alloted time window. Do your best to document any simplifying assumptions and design considerations as you work through the problem.


//////////////////////////////////////////////


Ahmed Amin Ali

First Problem
=============
1- First Problem I changed the data type from HashMap to ConcurrentHashMap in order to be thread save because HashMap is not Thread save
so I used build in data structure ConcurrentHashMap for this reason

2-for allowing user to reserve and support timeout I tried to use the same technique for Producer and Consumer problem and 
 I also used BlockingQueue which is thread save.  and when this method get called it will save the seat reserved in the Queue
 and letter i will wait for timeout and check again if the records  in the queue has already been taken or not if not taken the 
 seat will return back to available seat and reverse for the process will process 
 
Second Problem
=============

1- I created new method findAndHoldBlockSeats which will hold block of seats when the method get call I assuming the max number for seat
is already known so because I should have rang to search for so I assumed that the range from 1 to 999 and I created ConcurrentHashMap
to hold the seats available and loop from 1 and if i found available seat I put in the map and check if the map record size equal the 
Number of needed seat so finish or keep looking  and if i found seat not available between available seat in this case i remove from 
the map and keep looking 

 
 Note I did not test code becuase i did't have time 
  
