package com.venkatesh;

import java.util.List;
import java.util.Scanner;

import com.venkatesh.entity.Customer;
import com.venkatesh.entity.Room;
import com.venkatesh.util.HibernateUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class HotelManagementSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EntityManager em = HibernateUtil.getEntityManagerFactory().createEntityManager();
        
        while (true) {
            System.out.println("\n************ Hotel Management System ************");
            System.out.println("1. Book Room");
            System.out.println("2. View All Bookings");
            System.out.println("3. View Room Availability");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input. Enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> bookRoom(em, scanner);
                case 2 -> viewAllBookings(em);
                case 3 -> viewRoomAvailabilitySummary(em);
                case 4 -> {
                    HibernateUtil.shutdown();
                    System.out.println("Thank you !!!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void bookRoom(EntityManager em, Scanner scanner) {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter contact number: ");
        String contact = scanner.nextLine().trim();

        System.out.print("How many rooms do you want to book? ");
        int roomCount;
        try {
            roomCount = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid number.");
            return;
        }

        System.out.print("Enter room type (AC/Non-AC): ");
        String roomType = scanner.nextLine().trim().toUpperCase();

        if (!roomType.equals("AC") && !roomType.equals("NON-AC")) {
            System.out.println("Room type must be either 'AC' or 'Non-AC'.");
            return;
        }

        TypedQuery<Room> query = em.createQuery(
                "SELECT r FROM Room r WHERE r.isBooked = false AND r.roomType = :type", Room.class);
        query.setParameter("type", roomType);
        query.setMaxResults(roomCount);

        List<Room> availableRooms = query.getResultList();

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available of type " + roomType);
            return;
        }

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        int booked = 0;
        try {
            for (Room room : availableRooms) {
                room.setBooked(true);
                em.merge(room);

                Customer customer = new Customer();
                customer.setName(name);
                customer.setContact(contact);
                customer.setRoomNumber(room.getRoomNumber());
                customer.setRoomType(roomType);

                em.persist(customer);
                System.out.println("Room " + room.getRoomNumber() + " booked for " + name);
                booked++;
            }
            tx.commit();

            if (booked < roomCount) {
                System.out.println("Only " + booked + " rooms were available of type " + roomType);
            }
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    private static void viewAllBookings(EntityManager em) {
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

    private static void viewRoomAvailabilitySummary(EntityManager em) {
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