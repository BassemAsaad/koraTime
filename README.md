# Stadium Booking App

A mobile app for discovering, managing, and booking stadiums with real-time communication, matchmaking rooms, and friend-based chat features.

## Overview

This app connects stadium owners and players in one place. Stadium owners can manage their stadium details, schedule availability, and handle bookings. Players can search for stadiums, view location and contact details, book time slots, chat with friends, and join public or private rooms.

## Main Features

### Stadium Management for Owners

* Add and update stadium information
* Upload a cover image for each stadium
* Upload up to 3 additional images for each stadium
* Set opening and closing times
* Add phone number for direct contact
* Add stadium location
* Manage stadium details from one dashboard

### Booking System

* Custom calendar for bookings
* Time-slot based reservation flow
* View booking status:

  * Pending
  * Accepted
  * Rejected

### Stadium Discovery for Users

* Search for stadiums by name
* View stadium location
* Call the stadium manager directly

### Matchmaking System

* Search for players in a specific stadium
* Match users who want to play in the same stadium
* Automatically create a private room when the required number of players is reached
* Include the stadium manager and matched players in the room
* Allow discussion and booking coordination inside the room
* Stadium manager owns the room and can delete it

### Friends Management

* Add friends
* Build a network of connected users
* Communicate only with friends in private chats

### Chat System

* Chat with friends privately
* Join public rooms and chat with anyone
* Create private rooms with a password
* Share the password with others to join the room
* Ability to remove created rooms

## User Roles

### Stadium Owner / Manager

* Manage stadium profile
* Add pictures and contact details
* Set opening hours
* Review and handle bookings
* Own and manage matchmaking rooms
* Delete rooms when needed

### User / Player

* Search for stadiums
* View stadium details and location
* Call the stadium manager
* Book available time slots
* Track booking status
* Add friends
* Chat with friends
* Join public and private rooms
* Participate in matchmaking rooms

## Core Flow

1. The user searches for a stadium by name.
2. The user views stadium details, images, phone number, and location.
3. The user selects an available time slot from the custom calendar.
4. The booking is created and marked as pending.
5. The stadium manager accepts or rejects the booking.
6. Players can also join matchmaking for a specific stadium.
7. When enough players are matched, a private room is created automatically.
8. Friends and room chats help users coordinate before the game.

## Room Types

### Public Room

* Open for anyone to join
* Supports group chat

### Private Room

* Protected by a password
* Only users with the password can join
* Can be removed by the creator

### Matchmaking Room

* Automatically created when players are matched in the same stadium
* Includes the matched players and the stadium manager
* Used for coordination and booking discussion

## Booking Status

* **Pending**: Booking request has been sent and is waiting for review.
* **Accepted**: Booking has been approved by the stadium manager.
* **Rejected**: Booking has been declined by the stadium manager.

## Key Benefits

* Makes stadium booking easier and faster
* Helps stadium owners organize schedules and availability
* Connects players who want to play in the same place and time
* Supports direct communication through chat and rooms
* Improves user experience with real-time coordination

## Future Ideas

* Push notifications for booking updates
* Ratings and reviews for stadiums
* Advanced filters for stadium search
* Payment integration
* QR code check-in for bookings

## License

This project is intended for personal, educational, or commercial use depending on your chosen license.
