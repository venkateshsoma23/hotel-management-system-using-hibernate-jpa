package com.venkatesh;

import java.util.Scanner;

import com.venkatesh.service.BookingService;
import com.venkatesh.service.ViewService;
import com.venkatesh.util.HibernateUtil;

import jakarta.persistence.EntityManager;

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
                case 1 -> BookingService.bookRoom(em, scanner);
                case 2 -> ViewService.viewAllBookings(em);
                case 3 -> ViewService.viewRoomAvailabilitySummary(em);
                case 4 -> {
                    HibernateUtil.shutdown();
                    System.out.println("Thank you !!!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
