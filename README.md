# Project Title

Event Ticketing System with Seat Selection and Temporary Reservations

(BookMyShow / Cinema / Theater / Concert–style platform)

## Project Goal

Design and build a system that sells tickets for events with limited seating, ensuring that:

- a seat is never assigned or sold more than once  
- users have a fair and limited time window to complete payment  
- the system works correctly under high concurrent usage  
- failures (abandoned sessions, payment errors, concurrency issues) do not create inconsistencies  

In short, the system must reliably coordinate time, location, seats, and payments.

## Core Problem Being Solved

Fair and consistent allocation of limited resources (seats) under high concurrency and possible external failures.

This problem appears when:

- demand is higher than supply  
- many users try to buy at the same time  
- payment is not instant or fully reliable  

The system must decide who gets a seat and who does not, without errors.

