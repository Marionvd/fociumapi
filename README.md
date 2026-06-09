# Focium API Documentation

## Introduction

Welcome to the documentation for the Focium API. This document provides detailed information on how to interact with the API, including its endpoints and system design.

## System Design

The Focium API is designed to provide a comprehensive set of features for managing user profiles, achievements, shop items, showdowns, and more. The system is built using Java and Spring Boot, with support for various databases and cloud services.

### Key Components

1. **Profile Management**: Users can create, update, and delete their profiles, upload profile pictures, and manage their achievements.
2. **Achievement System**: Users can earn achievements by completing tasks or meeting certain criteria.
3. **Shop Items**: Users can purchase shop items to enhance their experience, such as perks and boosts.
4. **Showdowns**: Users can compete in showdowns against other players to earn points and achieve higher levels.
5. **Authentication**: Users can register, log in, reset passwords, and manage their accounts.

### Database Design

The system uses a PostgreSQL database for storing user data, achievements, shop items, and showdowns. The database is designed to be scalable and efficient, with indexes on frequently queried fields.

### Security

The API includes robust security measures, including OAuth 2.0 authentication, password reset functionality, and input validation to prevent common vulnerabilities.

## Endpoints

Below are the main endpoints of the Focium API along with their descriptions:

### Profile Management

- **POST /api/auth/register**: Register a new user.
- **GET /api/profiles/{uuid}**: Retrieve a profile by UUID.
- **PUT /api/profiles/pfp**: Upload a profile picture for the authenticated user.
- **GET /api/profiles/{uuid}/items**: Retrieve all items owned by a profile.

### Achievement System

- **POST /api/achievements**: Create a new achievement.
- **GET /api/achievements/{uuid}**: Retrieve an achievement by UUID.
- **PUT /api/achievements/{uuid}**: Update an existing achievement.
- **DELETE /api/achievements/{uuid}**: Delete an achievement.

### Shop Items

- **POST /api/admin/shop**: Create a new shop item.
- **GET /api/admin/shop/{uuid}**: Retrieve a shop item by UUID.
- **PUT /api/admin/shop/{uuid}**: Update an existing shop item.
- **DELETE /api/admin/shop/{uuid}**: Delete a shop item.

### Showdowns

- **POST /api/showdowns**: Create a new showdown.
- **GET /api/showdowns/{uuid}**: Retrieve a showdown by UUID.
- **PUT /api/showdowns/{uuid}/complete**: Complete a showdown and award points to the winner.

### Authentication

- **POST /api/auth/login**: Log in a user.
- **POST /api/auth/reset-password**: Request a password reset for a user.
- **GET /api/auth/profile**: Retrieve the authenticated user's profile.

## Usage

To use the Focium API, you will need to:

1. **Register an account** by sending a POST request to `/api/auth/register` with your email and password.
2. **Log in** using your credentials by sending a POST request to `/api/auth/login`.
3. **Retrieve your profile** by sending a GET request to `/api/profiles/{uuid}`.
4. **Upload a profile picture** by sending a PUT request to `/api/profiles/pfp` with the file as a multipart form data.
5. **Create an achievement** by sending a POST request to `/api/achievements` with the achievement details.

For more detailed information on each endpoint, refer to the API documentation or the code comments in the source files.

## Contributing

Contributions are welcome! If you have any suggestions for improvements or bug fixes, please open an issue or submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).