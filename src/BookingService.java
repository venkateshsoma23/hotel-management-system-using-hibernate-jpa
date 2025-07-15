package com.venkatesh.service;

import java.util.List;
import java.util.Scanner;

import com.venkatesh.entity.Customer;
import com.venkatesh.entity.Room;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class BookingService {

    public static void bookRoom(EntityManager em, Scanner scanner) {
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
}
