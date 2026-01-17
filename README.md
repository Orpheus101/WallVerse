# WallVerse - Modern Wallpaper Manager

## Description
WallVerse is a JavaFX desktop application for browsing, searching, and managing wallpapers from the Wallhaven API. Users can discover trending wallpapers, save favorites, like images, and download high-quality wallpapers directly to their computer.

## Features
- Browse trending wallpapers from Wallhaven
- Search wallpapers by keywords and categories
- Modern Pinterest-style details overlay with animations
- User authentication system (login/signup)
- Personal collections: Favorites and Liked wallpapers
- Direct wallpaper download to user's Downloads folder
- Responsive grid layout with smooth transitions

## Technology Stack
- Language: Java 17
- Framework: JavaFX 17 for desktop UI
- Database: MySQL for user data and wallpaper metadata
- Build Tool: Maven
- External API: Wallhaven API for wallpaper content
- Dependencies: MySQL Connector, org.json for JSON parsing

## Prerequisites
- Java 17 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher

## Installation
Clone the repository:
```bash
git clone https://github.com/yourusername/wallverse.git
cd wallverse
```

Set up the database:
```sql
CREATE DATABASE wallverse_1;
USE wallverse_1;
-- Execute the schema from db/schema.sql
```

Configure database connection in `src/main/java/com/wallverse/config/DbConfig.java`:
```java
public static final String URL = "jdbc:mysql://localhost:3306/wallverse_1?useSSL=false&serverTimezone=UTC";
public static final String USER = "your_mysql_username";
public static final String PASSWORD = "your_mysql_password";
```

Build the project:
```bash
mvn clean compile
```

Run the application:
```bash
mvn javafx:run
```

## Project Structure
```plaintext
wallverse/
├── db/
│   └── schema.sql              # Database schema definition
├── src/main/
│   ├── java/com/wallverse/
│   │   ├── api/
│   │   │   └── WallhavenClient.java    # Wallhaven API integration
│   │   ├── config/
│   │   │   └── DbConfig.java           # Database configuration
│   │   ├── dao/
│   │   │   ├── BaseDao.java            # Base database operations
│   │   │   ├── UserDao.java            # User data access
│   │   │   └── WallpaperActionDao.java # Wallpaper actions
│   │   ├── db/
│   │   │   └── DbUtil.java             # Database utilities
│   │   ├── model/
│   │   │   ├── User.java               # User entity
│   │   │   └── Wallpaper.java          # Wallpaper entity
│   │   ├── ui/
│   │   │   ├── DetailsOverlay.java     # Details view overlay
│   │   │   ├── FavoritesView.java      # Favorites screen
│   │   │   ├── LikedView.java          # Liked wallpapers screen
│   │   │   ├── LoginView.java          # Login interface
│   │   │   ├── MainView.java           # Main application view
│   │   │   └── SignupView.java         # Registration interface
│   │   │   ├── util/
│   │   │   │   ├── CategoryData.java       # Category definitions
│   │   │   │   ├── DownloadUtil.java       # Download functionality
│   │   │   │   ├── PasswordUtil.java       # Password security
│   │   │   │   └── ValidationUtil.java     # Input validation
│   │   │   └── MainApp.java                # Main application class
│   └── resources/
│       └── style.css                   # Application styling
└── pom.xml                             # Maven configuration
```

## Key Components
### Model Layer
- `User.java`: Manages user account information and authentication
- `Wallpaper.java`: Handles wallpaper metadata including tags, resolution, and download information

### Data Access Layer
- `UserDao.java`: Handles user registration, login, and account validation
- `WallpaperActionDao.java`: Manages user interactions like liking and favoriting wallpapers
- `BaseDao.java`: Provides common database connection functionality

### API Integration
- `WallhavenClient.java`: Communicates with Wallhaven API to fetch wallpapers and metadata
- Implements caching for improved performance
- Handles both search and detailed wallpaper information requests

### User Interface
- `MainView.java`: Primary application interface with search and grid display
- `DetailsOverlay.java`: Modern overlay view showing wallpaper details with download options
- `FavoritesView.java` and `LikedView.java`: Personal collection management screens
- `LoginView.java` and `SignupView.java`: User authentication interfaces

## Database Schema
The application uses four main tables:
- `users`: Stores user account information
- `wallpapers`: Caches wallpaper metadata including direct image URLs
- `likes`: Tracks user likes with timestamps
- `favorites`: Tracks user favorites with timestamps

## Recent Improvements
### Download System Enhancement
- Fixed download failures by using direct image URLs instead of page URLs
- Improved filename generation with proper extensions
- Better error handling and user feedback

### UI Modernization
- Added Pinterest-style details overlay with smooth animations
- Implemented favorites and liked wallpapers screens
- Enhanced navigation between different application views

### Database Optimization
- Extended wallpaper table with metadata fields for better performance
- Added timestamps to user interaction tables
- Implemented upsert logic for efficient data management

## Usage Guide
- Getting Started: Launch the application and either create an account or continue as guest
- Browsing: View trending wallpapers on the home screen
- Searching: Use the search bar to find specific wallpapers by keywords or categories
- Details: Click any wallpaper to view detailed information in the overlay
- Interactions: Like or favorite wallpapers from the details view
- Collections: Access your saved wallpapers through the Favorites and Liked buttons
- Downloading: Download wallpapers directly to your Downloads folder

## Development Notes
### Threading
All network operations (API calls, downloads) run on background threads to maintain UI responsiveness. UI updates are performed using `Platform.runLater()`.

### Error Handling
The application includes comprehensive error handling for:
- Network connectivity issues
- Database connection problems
- Invalid user inputs
- Download failures

### Security
- Passwords are securely hashed using SHA-256 with salt
- Database connections use prepared statements to prevent SQL injection
- User sessions are properly managed

## Contributing
Feel free to fork this repository and submit pull requests for improvements. Please ensure your code follows the existing style and includes appropriate error handling.

## License
This project is open source and available under the MIT License.
