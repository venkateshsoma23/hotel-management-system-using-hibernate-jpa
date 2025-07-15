package com.venkatesh.service;

import java.util.List;

import com.venkatesh.entity.Customer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class ViewService {

    public static void viewAllBookings(EntityManager em) {
        TypedQuery<Customer> query = em.createQuery("SELECT c FROM Customer c ORDER BY c.id", Customer.class);
        List<Customer> customers = query.getResultList();

        if (customers.isEmpty()) {
            System.out.println("\nNo bookings found.");
            return;
        }

        System.out.println("\n********* All Bookings **********");
        for (Customer c : customers) {
            System.out.println("Booking ID: " + c.getId());
            System.out.println("Name      : " + c.getName());
            System.out.println("Contact   : " + c.getContact());
            System.out.println("Room No   : " + c.getRoomNumber());
            System.out.println("Room Type : " + c.getRoomType());
            System.out.println("*************************");
        }
    }

    public static void viewRoomAvailabilitySummary(EntityManager em) {
        long acRooms = em.createQuery(
                "SELECT COUNT(r) FROM Room r WHERE r.roomType = 'AC' AND r.isBooked = false", Long.class)
                .getSingleResult();

        long nonAcRooms = em.createQuery(
                "SELECT COUNT(r) FROM Room r WHERE r.roomType = 'NON-AC' AND r.isBooked = false", Long.class)
                .getSingleResult();

        System.out.println("\n************ Room Availability ************");
        System.out.println("AC Rooms     : " + acRooms + " out of 50 available");
        System.out.println("Non-AC Rooms : " + nonAcRooms + " out of 50 available");
    }
}
